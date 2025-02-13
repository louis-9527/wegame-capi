package com.wegame.capi.biz.module.malakas.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.base.odds.OddsCalculateStrategy;
import com.wegame.capi.biz.module.base.reward.AbstractRewardHandler;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_SCATTER;
import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_SYMBOL_PLACEHOLDER;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
@Component("malakasRewardHandler")
public class MalakasRewardHandler extends AbstractRewardHandler<MalakasSymbolDo> {
    private static final Integer MIN_SCATTER_COUNT_FOR_FREE_SPIN = 4;

    @Autowired
    OddsCalculateStrategy<MalakasSymbolDo> malakasOddsCalculateStrategy;
    @Autowired
    MultipleCalculateStrategy<MalakasSymbolDo> malakasMultipleCalculateStrategy;
    @Autowired
    BetRecordService betRecordService;
    @Autowired
    MalakasConfig malakasConfig;

    @Override
    public RewardResultDo<MalakasSymbolDo> handle(RewardHandleDo<MalakasSymbolDo> rewardHandle) {
        long current = System.currentTimeMillis();
        int scatterCount = scatterCount(rewardHandle);
        Integer freeSpinsTimes = malakasConfig.getScatterFreeSpinsTimesConfig().get(scatterCount);
        // 1、业务校验
        validate(rewardHandle);
        // 2、计算中奖线
        List<RewardLineDo<MalakasSymbolDo>> rewardLineList = calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        // 3、计算倍率
        BigDecimal totalMultiple = malakasMultipleCalculateStrategy.calculateMultiple(rewardHandle, rewardLineList);
        if (CollectionUtil.isEmpty(rewardLineList)) {
            // 未中奖
            return RewardResultDo.<MalakasSymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardType(RewardTypeEnum.NONE)
                    .rewardSubType(RewardSubTypeEnum.NONE)
                    .totalRewardAmount(0L)
                    .totalMultiple(null)
                    .hasRewardFreeSpin(scatterCount >= MIN_SCATTER_COUNT_FOR_FREE_SPIN)
                    .rewardLineDetailList(null)
                    .rewardFreeSpinTimes(Objects.isNull(freeSpinsTimes) ? 0 : freeSpinsTimes)
                    .build();
        }

        // 4、中奖线处理
        // 中奖总金额
        long totalRewardAmount = 0L;
        // 中奖坐标列表
        List<RewardLineDetailDo<MalakasSymbolDo>> rewardLineDetailList = Lists.newArrayList();
        for (RewardLineDo<MalakasSymbolDo> rewardLine : CollectionUtils.emptyIfNull(rewardLineList)) {
            // 计算赔率
            BigDecimal odds = malakasOddsCalculateStrategy.calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
            // 计算（中奖线）中奖金额：中奖线中奖金额计算公式 = 投注金额 * 赔率(1 + 倍率)
            BigDecimal lineRewardAmount = calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), odds, totalMultiple);
            // 累加中奖总金额
            totalRewardAmount = totalRewardAmount + lineRewardAmount.longValue();
            // 中奖线明细列表
            rewardLineDetailList.add(RewardLineDetailDo.<MalakasSymbolDo>builder()
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
        // 5.构建中奖结果
        RewardResultDo<MalakasSymbolDo> rewardResult = RewardResultDo.<MalakasSymbolDo>builder()
                .rewardType(RewardTypeEnum.valueOfRewardLineCont(rewardLineList.size()))
                .rewardSubType(calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .betAmount(rewardHandle.getBetAmount())
                .totalRewardAmount(totalRewardAmount)
                .hasRewardFreeSpin(scatterCount >= MIN_SCATTER_COUNT_FOR_FREE_SPIN)
                .rewardLineDetailList(rewardLineDetailList)
                .rewardFreeSpinTimes(Objects.isNull(freeSpinsTimes) ? 0 : freeSpinsTimes)
                .totalMultiple(totalMultiple)
                .build();
        // 6.返回中奖信息
        log.info("reward cost time: {} ms", System.currentTimeMillis() - current);
        return rewardResult;
    }

    private List<RewardLineDo<MalakasSymbolDo>> calcRewardLineList(GameInfoDo gameInfo, WheelDo<MalakasSymbolDo> wheel) {
        MalakasSymbolDo[][] symbolArr = wheel.getSymbolArr();
        // Map<第一列元素, TreeMap<列序号，满足规则的元素列表>
        Map<MalakasSymbolDo, TreeMap<Integer, List<RewardSymbolDo<MalakasSymbolDo>>>> map = new HashMap<>();
        // 目标元素集合targetSymbolSet
        Set<MalakasSymbolDo> targetSymbolSet = new HashSet<>();
        // 1、处理第一列
        for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
            MalakasSymbolDo targetSymbol = symbolArr[row][ONE_COLUM_INDEX];
            if (Objects.equals(targetSymbol, MALAKAS_SYMBOL_PLACEHOLDER) || Objects.equals(targetSymbol, MALAKAS_SCATTER)) {
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
                Integer currentRow = row;
                MalakasSymbolDo currentSymbol = symbolArr[row][currentColum];
                if (Objects.equals(currentSymbol, MALAKAS_SYMBOL_PLACEHOLDER)) {
                    continue;
                }
                for (MalakasSymbolDo targetSymbol : CollectionUtils.emptyIfNull(targetSymbolSet)) {
                    if (!isMatchTargetSymbol(currentSymbol, targetSymbol)) {
                        continue;
                    }
                    TreeMap<Integer, List<RewardSymbolDo<MalakasSymbolDo>>> treeMap = map.get(targetSymbol);
                    if (canPutIn(treeMap, currentColum)) {
                        RewardSymbolDo<MalakasSymbolDo> symbolIndex = RewardSymbolDo.<MalakasSymbolDo>builder()
                                .symbol(currentSymbol)
                                .index(IndexFactory.buildDo(currentRow, currentColum))
                                .build();
                        if (Objects.isNull(treeMap.get(currentColum))) {
                            List<RewardSymbolDo<MalakasSymbolDo>> list = new ArrayList<>();
                            list.add(symbolIndex);
                            treeMap.put(currentColum, list);
                        } else {
                            treeMap.get(currentColum).add(symbolIndex);
                        }
                    }
                }
            }
        }
        // 3、统计中奖线
        List<RewardLineDo<MalakasSymbolDo>> list = buildRewardLineList(map);
        // 4、返回中奖线列表
        return list;
    }

    private boolean canPutIn(TreeMap<Integer, List<RewardSymbolDo<MalakasSymbolDo>>> treeMap, Integer currentColum) {
        // 第一列必处理
        if (Objects.equals(currentColum, ONE_COLUM_INDEX)) {
            return true;
        }
        // 判断上一列的列表是否为空
        return CollectionUtils.isNotEmpty(treeMap.get(currentColum - 1));
    }

    private List<RewardLineDo<MalakasSymbolDo>> buildRewardLineList(Map<MalakasSymbolDo, TreeMap<Integer, List<RewardSymbolDo<MalakasSymbolDo>>>> map) {
        List<RewardLineDo<MalakasSymbolDo>> list = new ArrayList<>();
        for (Map.Entry<MalakasSymbolDo, TreeMap<Integer, List<RewardSymbolDo<MalakasSymbolDo>>>> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (MapUtils.isEmpty(entry.getValue())) {
                continue;
            }
            TreeMap<Integer, List<RewardSymbolDo<MalakasSymbolDo>>> columMap = entry.getValue();
            if (columMap.size() < RewardLineTypeEnum.THREE.getNo()) {
                continue;
            }
            int totalLineCount = columMap.values()
                    .stream()
                    .map(item -> item.size())
                    .reduce(1, (a, b) -> a * b);
            list.add(RewardLineDo.<MalakasSymbolDo>builder()
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
        return Objects.equals(symbol, targetSymbol)
                || Objects.equals(symbol.getSymbolType(), SymbolTypeEnum.WILD)
                || Objects.equals(targetSymbol.getSymbolType(), SymbolTypeEnum.WILD);
    }

    private void validate(RewardHandleDo<MalakasSymbolDo> rewardHandle) {
        AssertUtil.assertNotNull(rewardHandle, ErrorCode.ERR_PARAM, "rewardHandle is null");
        AssertUtil.assertNotNull(rewardHandle.getGameType(), ErrorCode.ERR_INTER, "not support gameType");
        AssertUtil.assertNotNull(rewardHandle.getBetType(), ErrorCode.ERR_PARAM, "not support betType");
        AssertUtil.assertNotNull(rewardHandle.getBetSubType(), ErrorCode.ERR_PARAM, "not support betSubType");
        AssertUtil.assertNotNull(rewardHandle.getWheel(), ErrorCode.ERR_PARAM, "wheel is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getIndex(), ErrorCode.ERR_PARAM, "wheel index is null");
        AssertUtil.assertNotNull(rewardHandle.getWheel().getSymbolArr(), ErrorCode.ERR_PARAM, "wheel symbolArr is null");
        AssertUtil.assertTrue(Objects.equals(rewardHandle.getGameType(), GameTypeEnum.MALAKAS), ErrorCode.ERR_PARAM, "just support game type of malakas");
        AssertUtil.assertTrue(rewardHandle.getBetAmount() > 0, ErrorCode.ERR_PARAM, "bet amount must greater than 0");
        SymbolDo[][] symbolArr = rewardHandle.getWheel().getSymbolArr();
        // 校验：滚轮行数
        GameInfoDo gameInfo = rewardHandle.getGameInfo();
        AssertUtil.assertTrue(symbolArr.length == gameInfo.getRow(), ErrorCode.ERR_PARAM, "symbol arr err");
        for (int row = 0; row < gameInfo.getRow(); row++) {
            // 校验：滚轮列数
            AssertUtil.assertTrue(symbolArr[row].length == gameInfo.getColumn(), ErrorCode.ERR_PARAM, "symbol arr err");
            for (int column = 0; column < gameInfo.getColumn(); column++) {
                // 校验：所有元素不能为空
                AssertUtil.assertNotNull(symbolArr[row][column], ErrorCode.ERR_PARAM, "all symbol can not be null");
            }
        }
    }
}
