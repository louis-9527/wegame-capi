package com.wegame.capi.biz.module.sirena.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.config.domain.KapreConfig;
import com.wegame.capi.biz.config.domain.SirenaConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.*;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.odds.OddsCalculateStrategy;
import com.wegame.capi.biz.module.base.reward.AbstractRewardHandler;
import com.wegame.capi.biz.module.lamang.LamangGameHelper;
import com.wegame.capi.biz.module.sirena.multiple.SirenaMultipleCalculateStrategy;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;


@Slf4j
@Component("sirenaRewardHandler")
public class SirenaRewardHandler extends AbstractRewardHandler<SirenaSymbolDo> {
    private static final Integer MIN_SCATTER_COUNT_FOR_FREE_SPIN = 3;

    @Autowired
    OddsCalculateStrategy<SirenaSymbolDo> sirenaOddsCalculateStrategy;
    @Autowired
    SirenaMultipleCalculateStrategy sirenaMultipleCalculateStrategy;

    @Autowired
    SirenaConfig sirenaConfig;

    @Override
    public RewardResultDo<SirenaSymbolDo> handle(RewardHandleDo<SirenaSymbolDo> rewardHandle) {
        // 1、业务校验
        this.validate(rewardHandle);
        // 2、计算中奖线
        List<RewardLineDo<SirenaSymbolDo>> rewardLineList = this.calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        // 3. 计算免费轮次数
        Integer freeWheelTimes = this.calcRewardFreeWheelTimes(rewardHandle, rewardLineList);
        if (CollectionUtil.isEmpty(rewardLineList)) {
            // 未中奖
            return RewardResultDo.<SirenaSymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardType(RewardTypeEnum.NONE)
                    .rewardSubType(RewardSubTypeEnum.NONE)
                    .totalRewardAmount(0L)
                    .totalMultiple(rewardHandle.getCurrentMultiple())
                    .rewardLineDetailList(null)
                    .hasRewardFreeWheel(freeWheelTimes > 0)
                    .rewardFreeWheelTimes(freeWheelTimes)
                    .accumulateReachMaxMultiple(Boolean.FALSE)
                    .build();
        }

        // 4、中奖线处理
        // 当前回合中奖总金额
        long totalRewardAmount = 0L;
        // 中奖坐标列表
        List<RewardLineDetailDo<SirenaSymbolDo>> rewardLineDetailList = Lists.newArrayList();
        for (RewardLineDo<SirenaSymbolDo> rewardLine : rewardLineList) {
            // 获取赔率
            BigDecimal odds = sirenaOddsCalculateStrategy.calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
            // 计算中奖线维度的倍率
            BigDecimal rewardLineMultiple = sirenaMultipleCalculateStrategy.calculateMultipleOfRewardLine(rewardHandle, rewardLine);
            // 计算（中奖线）中奖金额：投注金额 * 赔率 * 中奖线数量
            rewardLineMultiple = rewardLineMultiple.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ONE : rewardLineMultiple;
            BigDecimal lineRewardAmount = super.calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), odds, rewardLineMultiple);
            // 累加中奖总金额
            totalRewardAmount = totalRewardAmount + lineRewardAmount.longValue();
            // 中奖线明细列表
            rewardLineDetailList.add(RewardLineDetailDo.<SirenaSymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardAmount(lineRewardAmount.longValue())
                    .odds(odds)
                    .multiple(null)
                    .rewardLineType(rewardLine.getRewardLineType())
                    .rewardLineSubType(rewardLine.getRewardLineSubType())
                    .rewardLineCount(rewardLine.getRewardLineCount())
                    .rewardSymbol(rewardLine.getRewardSymbol())
                    .rewardSymbolList(rewardLine.getRewardSymbolList())
                    .build());
        }

        // 5.构建中奖结果
        boolean reachMaxMultiple = reachMaxMultiple(
                rewardHandle.getGamePlayType(),
                Objects.isNull(rewardHandle.getCurrentTotalRewardAmount()) ? totalRewardAmount : rewardHandle.getCurrentTotalRewardAmount() + totalRewardAmount,
                rewardHandle.getBetAmount(),
                sirenaConfig.getRewardMaxMultiple());
        // 6.返回中奖信息
        return RewardResultDo.<SirenaSymbolDo>builder()
                .betAmount(rewardHandle.getBetAmount())
                .rewardType(RewardTypeEnum.valueOfRewardLineCont(rewardLineList.size()))
                .rewardSubType(RewardHelper.calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .totalRewardAmount(totalRewardAmount)
                .rewardLineDetailList(rewardLineDetailList)
                .hasRewardFreeWheel(freeWheelTimes > 0)
                .rewardFreeWheelTimes(freeWheelTimes)
                .accumulateReachMaxMultiple(reachMaxMultiple)
                .build();
    }


    private List<RewardLineDo<SirenaSymbolDo>> calcRewardLineList(GameInfoDo gameInfo, WheelDo<SirenaSymbolDo> wheel) {
        SirenaSymbolDo[][] symbolArr = wheel.getSymbolArr();
        // Map<第一列元素, TreeMap<列序号，满足规则的元素列表>
        Map<SirenaSymbolDo, TreeMap<Integer, List<RewardSymbolDo<SirenaSymbolDo>>>> map = new HashMap<>();
        // 目标元素集合targetSymbolSet
        Set<SirenaSymbolDo> targetSymbolSet = new HashSet<>();
        // 1、处理第一列
        for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
            SirenaSymbolDo targetSymbol = symbolArr[row][ONE_COLUM_INDEX];
            // scatter 在首列不参与中奖
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
                SirenaSymbolDo currentSymbol = symbolArr[row][currentColum];
                for (SirenaSymbolDo targetSymbol : CollectionUtils.emptyIfNull(targetSymbolSet)) {
                    if (!isMatchTargetSymbol(currentSymbol, targetSymbol)) {
                        continue;
                    }
                    TreeMap<Integer, List<RewardSymbolDo<SirenaSymbolDo>>> treeMap = map.get(targetSymbol);
                    if (canPutIn(treeMap, currentColum)) {
                        RewardSymbolDo<SirenaSymbolDo> rewardSymbol = RewardSymbolDo.<SirenaSymbolDo>builder()
                                .symbol(currentSymbol)
                                .index(IndexFactory.buildDo(currentRow, currentColum))
                                .build();
                        if (Objects.isNull(treeMap.get(currentColum))) {
                            List<RewardSymbolDo<SirenaSymbolDo>> list = new ArrayList<>();
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

    private boolean canPutIn(TreeMap<Integer, List<RewardSymbolDo<SirenaSymbolDo>>> treeMap, Integer currentColum) {
        // 第一列必处理
        if (Objects.equals(currentColum, ONE_COLUM_INDEX)) {
            return true;
        }
        // 判断上一列的列表是否为空
        return CollectionUtils.isNotEmpty(treeMap.get(currentColum - 1));
    }

    private List<RewardLineDo<SirenaSymbolDo>> buildRewardLineList(Map<SirenaSymbolDo, TreeMap<Integer, List<RewardSymbolDo<SirenaSymbolDo>>>> map) {
        List<RewardLineDo<SirenaSymbolDo>> list = new ArrayList<>();
        for (Map.Entry<SirenaSymbolDo, TreeMap<Integer, List<RewardSymbolDo<SirenaSymbolDo>>>> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (MapUtils.isEmpty(entry.getValue())) {
                continue;
            }
            TreeMap<Integer, List<RewardSymbolDo<SirenaSymbolDo>>> columMap = entry.getValue();
            if (columMap.size() < RewardLineTypeEnum.THREE.getNo()) {
                continue;
            }
            int totalLineCount = columMap.values()
                    .stream()
                    .map(List::size)
                    .reduce(1, (a, b) -> a * b);
            list.add(RewardLineDo.<SirenaSymbolDo>builder()
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


    /**
     * 计算免费轮次数
     * 区分正常轮和免费轮
     * 免费轮,计算scatter+1
     * 正常轮，计算scatter产生的免费轮
     *
     * @param rewardHandle rewardHandle
     * @return 免费轮次数
     */
    private Integer calcRewardFreeWheelTimes(RewardHandleDo<SirenaSymbolDo> rewardHandle, List<RewardLineDo<SirenaSymbolDo>> rewardLineList) {
        // 区分正常轮和免费轮
        if (rewardHandle.getWheel().isFreeFlag()) {
            // 免费轮,计算scatter+1
            long newScatter1Count = Arrays.stream(rewardHandle.getWheel().getSymbolArr()).flatMap(Arrays::stream)
                    .filter(Objects::nonNull).filter(SirenaSymbolDo::isScatter1IdSymbol).count();
            return (int) newScatter1Count * sirenaConfig.getFreeWheelTimesConfig().getFreeWheelStepForScatter1();
        } else {
            // 正常轮，计算scatter产生的免费轮
            long scatterCount = Arrays.stream(rewardHandle.getWheel().getSymbolArr()).flatMap(Arrays::stream)
                    .filter(Objects::nonNull).filter(SirenaSymbolDo::isScatterIdSymbol).count();
            return scatterCount >= MIN_SCATTER_COUNT_FOR_FREE_SPIN ? sirenaConfig.getFreeWheelTimesConfig().getBaseFreeWheelTimes() : 0;
        }
    }


    private boolean isMatchTargetSymbol(SymbolDo symbol, SymbolDo targetSymbol) {
        return StringUtils.equals(symbol.getId(), targetSymbol.getId())
                || Objects.equals(symbol.getSymbolType(), SymbolTypeEnum.WILD)
                || Objects.equals(targetSymbol.getSymbolType(), SymbolTypeEnum.WILD);
    }

    private void validate(RewardHandleDo<SirenaSymbolDo> rewardHandle) {
        AssertUtil.assertNotNull(rewardHandle, ErrorCode.ERR_PARAM, "rewardHandle is null");
        AssertUtil.assertNotNull(rewardHandle.getGameType(), ErrorCode.ERR_INTER, "not support gameType");
        AssertUtil.assertNotNull(rewardHandle.getBetType(), ErrorCode.ERR_PARAM, "not support betType");
        AssertUtil.assertNotNull(rewardHandle.getBetSubType(), ErrorCode.ERR_PARAM, "not support betSubType");
        AssertUtil.assertNotNull(rewardHandle.getGamePlayType(), ErrorCode.ERR_PARAM, "not support gamePlayType");
        AssertUtil.assertNotNull(rewardHandle.getWheel(), ErrorCode.ERR_PARAM, "wheel is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getSymbolArr(), ErrorCode.ERR_PARAM, "wheel symbolArr is null");
        if (Objects.equals(GamePlayTypeEnum.NORMAL_PLAY, rewardHandle.getGamePlayType())) {
            AssertUtil.assertNotNull(rewardHandle.getCurrentTotalRewardAmount(), ErrorCode.ERR_PARAM, "currentTotalRewardAmount is null");
        }
        AssertUtil.assertTrue(Objects.equals(rewardHandle.getGameType(), GameTypeEnum.SIRENA), ErrorCode.ERR_PARAM, "just support game type of sirena");
        AssertUtil.assertTrue(rewardHandle.getBetAmount() > 0, ErrorCode.ERR_PARAM, "bet amount must greater than 0");
        SymbolDo[][] symbolArr = rewardHandle.getWheel().getSymbolArr();
        // 校验：滚轮行数
        GameInfoDo gameInfo = rewardHandle.getGameInfo();
        AssertUtil.assertTrue(symbolArr.length == gameInfo.getRow(), ErrorCode.ERR_PARAM, "symbol arr err");
    }
}
