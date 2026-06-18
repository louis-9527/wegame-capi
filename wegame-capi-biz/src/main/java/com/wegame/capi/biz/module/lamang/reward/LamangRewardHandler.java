package com.wegame.capi.biz.module.lamang.reward;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.base.odds.OddsCalculateStrategy;
import com.wegame.capi.biz.module.base.reward.AbstractRewardHandler;
import com.wegame.capi.biz.module.lamang.LamangGameHelper;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;
import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
@Component("lamangRewardHandler")
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangRewardHandler extends AbstractRewardHandler<LamangSymbolDo> {
    private static final Integer MIN_SCATTER_COUNT_FOR_FREE_SPIN = 3;

    @Autowired
    OddsCalculateStrategy<LamangSymbolDo> lamangOddsCalculateStrategy;
    @Autowired
    MultipleCalculateStrategy<LamangSymbolDo> lamangMultipleCalculateStrategy;
    @Autowired
    LamangConfig lamangConfig;

    @Override
    public RewardResultDo<LamangSymbolDo> handle(RewardHandleDo<LamangSymbolDo> rewardHandle) {
        // 1.业务校验
        this.validate(rewardHandle);
        // 2.计算中奖线
        List<RewardLineDo<LamangSymbolDo>> rewardLineList = this.calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        // 3、计算倍率,倍率盘倍率
        BigDecimal totalMultiple = lamangMultipleCalculateStrategy.calculateMultiple(rewardHandle, rewardLineList);
        // 4.计算免费轮
        int freeWheelTimes = this.calcRewardFreeWheelTimes(rewardHandle, rewardLineList);
        if (CollectionUtil.isEmpty(rewardLineList)) {
            // 未中奖
            return RewardResultDo.<LamangSymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardType(RewardTypeEnum.NONE)
                    .rewardSubType(RewardSubTypeEnum.NONE)
                    .totalRewardAmount(0L)
                    .totalMultiple(totalMultiple)
                    .hasRewardFreeWheel(freeWheelTimes > 0)
                    .rewardLineDetailList(null)
                    .rewardFreeWheelTimes(freeWheelTimes)
                    .accumulateReachMaxMultiple(Boolean.FALSE)
                    .build();
        }
        // 4、中奖线处理
        // 中奖总金额
        long totalRewardAmount = 0L;
        // 中奖坐标列表
        List<RewardLineDetailDo<LamangSymbolDo>> rewardLineDetailList = Lists.newArrayList();
        for (RewardLineDo<LamangSymbolDo> rewardLine : rewardLineList) {
            // 计算赔率
            BigDecimal odds = lamangOddsCalculateStrategy.calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
            // 计算（中奖线）中奖金额：中奖线中奖金额计算公式 = 投注金额 * 赔率 * 倍率, 特殊逻辑：如果倍率是0，则倍率+1
            totalMultiple = NumberUtil.equals(totalMultiple, BigDecimal.ZERO) ? totalMultiple.add(BigDecimal.ONE) : totalMultiple;
            BigDecimal lineRewardAmount = calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), odds, totalMultiple);
            // 累加中奖总金额
            totalRewardAmount = totalRewardAmount + lineRewardAmount.longValue();
            // 中奖线明细列表
            rewardLineDetailList.add(RewardLineDetailDo.<LamangSymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardAmount(lineRewardAmount.longValue())
                    .odds(odds)
                    .multiple(totalMultiple)
                    .rewardLineType(rewardLine.getRewardLineType())
                    .rewardLineSubType(rewardLine.getRewardLineSubType())
                    .rewardLineCount(rewardLine.getRewardLineCount())
                    .rewardSymbol(rewardLine.getRewardSymbol())
                    .rewardSymbolList(rewardLine.getRewardSymbolList())
                    .build());
        }
        long totalAmount = Objects.isNull(rewardHandle.getCurrentTotalRewardAmount()) ? totalRewardAmount : rewardHandle.getCurrentTotalRewardAmount() + totalRewardAmount;
        boolean reachMaxMultiple = reachMaxMultiple(
                rewardHandle.getGamePlayType(),
                totalAmount,
                rewardHandle.getBetAmount(),
                lamangConfig.getRewardMaxMultiple());
        // 5.构建中奖结果
        RewardResultDo<LamangSymbolDo> rewardResult = RewardResultDo.<LamangSymbolDo>builder()
                .betAmount(rewardHandle.getBetAmount())
                .rewardType(RewardTypeEnum.valueOfRewardLineCont(rewardLineList.size()))
                .rewardSubType(RewardHelper.calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .totalRewardAmount(totalRewardAmount)
                .totalMultiple(totalMultiple)
                .hasRewardFreeWheel(freeWheelTimes > 0)
                .rewardLineDetailList(rewardLineDetailList)
                .accumulateReachMaxMultiple(reachMaxMultiple)
                .rewardFreeWheelTimes(freeWheelTimes)
                .build();
        // 6.返回中奖信息
        return rewardResult;
    }

    private List<RewardLineDo<LamangSymbolDo>> calcRewardLineList(GameInfoDo gameInfo, WheelDo<LamangSymbolDo> wheel) {
        LamangSymbolDo[][] symbolArr = wheel.getSymbolArr();
        // Map<第一列元素, TreeMap<列序号，满足规则的元素列表>
        Map<LamangSymbolDo, TreeMap<Integer, List<RewardSymbolDo<LamangSymbolDo>>>> map = new HashMap<>();
        // 目标元素集合targetSymbolSet
        Set<LamangSymbolDo> targetSymbolSet = new HashSet<>();
        // 1、处理第一列
        for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
            LamangSymbolDo targetSymbol = symbolArr[row][ONE_COLUM_INDEX];
            if (targetSymbol.isScatterSymbol()) {
                continue;
            }

            targetSymbolSet.add(targetSymbol);
            if (!map.containsKey(targetSymbol)) {
                map.put(targetSymbol, new TreeMap<>());
            }
        }
        // 2、循环处理(从第1列开始处理)
        for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
            Integer currentColum = column;
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                int currentRow = row;
                LamangSymbolDo currentSymbol = symbolArr[row][currentColum];

                for (LamangSymbolDo targetSymbol : CollectionUtils.emptyIfNull(targetSymbolSet)) {
                    if (!isMatchTargetSymbol(currentSymbol, targetSymbol)) {
                        continue;
                    }
                    TreeMap<Integer, List<RewardSymbolDo<LamangSymbolDo>>> treeMap = map.get(targetSymbol);
                    if (canPutIn(treeMap, currentColum)) {
                        RewardSymbolDo<LamangSymbolDo> rewardSymbol = RewardSymbolDo.<LamangSymbolDo>builder()
                                .symbol(currentSymbol)
                                .index(IndexFactory.buildDo(currentRow, currentColum))
                                .build();
                        if (Objects.isNull(treeMap.get(currentColum))) {
                            List<RewardSymbolDo<LamangSymbolDo>> list = new ArrayList<>();
                            list.add(rewardSymbol);
                            treeMap.put(currentColum, list);
                        } else {
                            treeMap.get(currentColum).add(rewardSymbol);
                        }
                    }
                }
            }
        }
        // 3、统计返回中奖线列表
        return buildRewardLineList(map);
    }

    private boolean canPutIn(TreeMap<Integer, List<RewardSymbolDo<LamangSymbolDo>>> treeMap, Integer currentColum) {
        // 第一列必处理
        if (Objects.equals(currentColum, ONE_COLUM_INDEX)) {
            return true;
        }
        // 判断上一列的列表是否为空
        return CollectionUtils.isNotEmpty(treeMap.get(currentColum - 1));
    }

    private List<RewardLineDo<LamangSymbolDo>> buildRewardLineList(Map<LamangSymbolDo, TreeMap<Integer, List<RewardSymbolDo<LamangSymbolDo>>>> map) {
        List<RewardLineDo<LamangSymbolDo>> list = new ArrayList<>();
        for (Map.Entry<LamangSymbolDo, TreeMap<Integer, List<RewardSymbolDo<LamangSymbolDo>>>> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (MapUtils.isEmpty(entry.getValue())) {
                continue;
            }
            TreeMap<Integer, List<RewardSymbolDo<LamangSymbolDo>>> columMap = entry.getValue();
            if (columMap.size() < RewardLineTypeEnum.THREE.getNo()) {
                continue;
            }
            int totalLineCount = columMap.values()
                    .stream()
                    .map(List::size)
                    .reduce(1, (a, b) -> a * b);
            list.add(RewardLineDo.<LamangSymbolDo>builder()
                    .rewardSymbol(entry.getKey())
                    .rewardLineType(RewardLineTypeEnum.valueOfNo(columMap.size()))
                    .rewardLineSubType(entry.getKey().isScatterSymbol() ? RewardLineSubTypeEnum.SCATTER : RewardLineSubTypeEnum.COMMON)
                    .rewardLineCount(totalLineCount)
                    .rewardSymbolList(columMap.values()
                            .stream()
                            .filter(CollectionUtils::isNotEmpty)
                            .flatMap(List::stream)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()))
                    .build());
        }
        return list;
    }

    private boolean isMatchTargetSymbol(SymbolDo symbol, SymbolDo targetSymbol) {
        return StringUtils.equals(symbol.getId(), targetSymbol.getId())
                || Objects.equals(symbol.getSymbolType(), SymbolTypeEnum.WILD)
                || Objects.equals(targetSymbol.getSymbolType(), SymbolTypeEnum.WILD);
    }


    /**
     * 计算免费轮次数
     * 区分正常轮和免费轮
     * 免费轮,计算scatter+1
     * 正常轮，没有中奖，计算scatter产生的免费轮
     * 正常轮,有中奖，返回0
     *
     * @param rewardHandle rewardHandle
     * @return 免费轮次数
     */
    private Integer calcRewardFreeWheelTimes(RewardHandleDo<LamangSymbolDo> rewardHandle, List<RewardLineDo<LamangSymbolDo>> rewardLineList) {
        // 区分正常轮和免费轮
        if (rewardHandle.getWheel().isFreeFlag()) {
            // 免费轮,计算scatter+1
            long newScatter1Count = Arrays.stream(rewardHandle.getWheel().getSymbolArr()).flatMap(Arrays::stream)
                    .filter(Objects::nonNull).filter(symbol -> Objects.equals(symbol.getId(), LamangGameHelper.LAMANG_SCATTER1.getId()) && symbol.isNewSymbol()).count();
            return (int) newScatter1Count;
        } else {
            if (CollectionUtil.isEmpty(rewardLineList)) {
                // 正常轮，没有中奖，计算scatter产生的免费轮
                long scatterCount = Arrays.stream(rewardHandle.getWheel().getSymbolArr()).flatMap(Arrays::stream)
                        .filter(Objects::nonNull).filter(symbol -> Objects.equals(symbol.getId(), LamangGameHelper.LAMANG_SCATTER.getId())).count();
                return scatterCount >= MIN_SCATTER_COUNT_FOR_FREE_SPIN ? lamangConfig.getFreeWheelTimes() : 0;
            }
        }
        // 正常轮,有中奖，返回0
        return 0;
    }

    private void validate(RewardHandleDo<LamangSymbolDo> rewardHandle) {
        AssertUtil.assertNotNull(rewardHandle, ErrorCode.ERR_PARAM, "rewardHandle is null");
        AssertUtil.assertNotNull(rewardHandle.getGameType(), ErrorCode.ERR_INTER, "not support gameType");
        AssertUtil.assertNotNull(rewardHandle.getBetType(), ErrorCode.ERR_PARAM, "not support betType");
        AssertUtil.assertNotNull(rewardHandle.getBetSubType(), ErrorCode.ERR_PARAM, "not support betSubType");
        AssertUtil.assertNotNull(rewardHandle.getWheel(), ErrorCode.ERR_PARAM, "wheel is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getIndex(), ErrorCode.ERR_PARAM, "wheel index is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getSymbolArr(), ErrorCode.ERR_PARAM, "wheel symbolArr is null");
        AssertUtil.assertTrue(Objects.equals(rewardHandle.getGameType(), GameTypeEnum.LAM_ANG), ErrorCode.ERR_PARAM, "just support game type of lamang");
        AssertUtil.assertTrue(rewardHandle.getBetAmount() > 0, ErrorCode.ERR_PARAM, "bet amount must greater than 0");
        SymbolDo[][] symbolArr = rewardHandle.getWheel().getSymbolArr();
        // 校验：滚轮行数
        GameInfoDo gameInfo = rewardHandle.getGameInfo();
        AssertUtil.assertTrue(symbolArr.length == gameInfo.getRow(), ErrorCode.ERR_PARAM, "symbol arr err");
    }
}
