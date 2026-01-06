package com.wegame.capi.biz.module.atlantis.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.config.domain.AtlantisConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.AtlantisSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.odds.OddsCalculateStrategy;
import com.wegame.capi.biz.module.base.reward.AbstractRewardHandler;
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
@Component("atlantisRewardHandler")
public class AtlantisRewardHandler extends AbstractRewardHandler<AtlantisSymbolDo> {
    private static final Integer SCATTER_COUNT_FOR_FREE_SPIN = 3;

    @Autowired
    OddsCalculateStrategy<AtlantisSymbolDo> kapreOddsCalculateStrategy;

    @Autowired
    AtlantisConfig atlantisConfig;

    @Override
    public RewardResultDo<AtlantisSymbolDo> handle(RewardHandleDo<AtlantisSymbolDo> rewardHandle) {
        // 1、业务校验
        validate(rewardHandle);
        // 2、计算中奖线
        List<RewardLineDo<AtlantisSymbolDo>> rewardLineList = calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        int scatterCount = scatterCount(rewardHandle);
        Integer freeWheelTimes = atlantisConfig.getFreeWheelTimes();
        boolean rewardFreeWheel = scatterCount >= SCATTER_COUNT_FOR_FREE_SPIN;
        if (CollectionUtil.isEmpty(rewardLineList)) {
            // 未中奖
            return RewardResultDo.<AtlantisSymbolDo>builder()
                    .betAmount(rewardHandle.getBetAmount())
                    .rewardType(RewardTypeEnum.NONE)
                    .rewardSubType(RewardSubTypeEnum.NONE)
                    .totalRewardAmount(0L)
                    .totalMultiple(rewardHandle.getCurrentMultiple())
                    .rewardLineDetailList(null)
                    .hasRewardFreeWheel(rewardFreeWheel)
                    .rewardFreeWheelTimes(rewardFreeWheel ? freeWheelTimes : 0)
                    .accumulateReachMaxMultiple(Boolean.FALSE)
                    .build();
        }

        // 4、中奖线处理
        // 当前回合中奖总金额
        long totalRewardAmount = 0L;
        // 中奖坐标列表
        List<RewardLineDetailDo<AtlantisSymbolDo>> rewardLineDetailList = Lists.newArrayList();
        for (RewardLineDo<AtlantisSymbolDo> rewardLine : rewardLineList) {
            // 计算赔率
            BigDecimal odds = kapreOddsCalculateStrategy.calculateOddsOfRewardLine(rewardHandle, rewardLine);
            // 计算（中奖线）中奖金额：
            // 普通中奖 ：总金额 = 投注金额 * 赔率 * 中奖线数量
            // 现金中奖 ：总金额 = 投注金额 * （现金元素赔率之和）
            BigDecimal lineRewardAmount = this.calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), odds, null);
            // 累加中奖总金额
            totalRewardAmount = totalRewardAmount + lineRewardAmount.longValue();
            // 中奖线明细列表
            rewardLineDetailList.add(RewardLineDetailDo.<AtlantisSymbolDo>builder()
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
                atlantisConfig.getRewardMaxMultiple());
        RewardResultDo<AtlantisSymbolDo> rewardResult = RewardResultDo.<AtlantisSymbolDo>builder()
                .betAmount(rewardHandle.getBetAmount())
                .rewardType(RewardTypeEnum.valueOfRewardLineCont(rewardLineList.size()))
                .rewardSubType(RewardHelper.calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .totalRewardAmount(totalRewardAmount)
                .rewardLineDetailList(rewardLineDetailList)
                .hasRewardFreeWheel(rewardFreeWheel)
                .rewardFreeWheelTimes(rewardFreeWheel ? freeWheelTimes : 0)
                .accumulateReachMaxMultiple(reachMaxMultiple)
                .build();
        // 6.返回中奖信息
        return rewardResult;
    }


    private List<RewardLineDo<AtlantisSymbolDo>> calcRewardLineList(GameInfoDo gameInfo, WheelDo<AtlantisSymbolDo> wheel) {
        AtlantisSymbolDo[][] symbolArr = wheel.getSymbolArr();
        // Map<第一列元素, TreeMap<列序号，满足规则的元素列表>
        Map<AtlantisSymbolDo, TreeMap<Integer, List<RewardSymbolDo<AtlantisSymbolDo>>>> map = new HashMap<>();
        // 目标元素集合targetSymbolSet
        Set<AtlantisSymbolDo> targetSymbolSet = new HashSet<>();
        // 1、处理第一列
        for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
            AtlantisSymbolDo targetSymbol = symbolArr[row][ONE_COLUM_INDEX];
            // 首列 wild & scatter 在首列不参与中奖
            if (targetSymbol.isWildSymbol() || targetSymbol.isScatterSymbol()) {
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
                AtlantisSymbolDo currentSymbol = symbolArr[row][currentColum];
                for (AtlantisSymbolDo targetSymbol : CollectionUtils.emptyIfNull(targetSymbolSet)) {
                    if (!isMatchTargetSymbol(currentSymbol, targetSymbol)) {
                        continue;
                    }
                    TreeMap<Integer, List<RewardSymbolDo<AtlantisSymbolDo>>> treeMap = map.get(targetSymbol);
                    if (canPutIn(treeMap, currentColum)) {
                        RewardSymbolDo<AtlantisSymbolDo> rewardSymbol = RewardSymbolDo.<AtlantisSymbolDo>builder()
                                .symbol(currentSymbol)
                                .index(IndexFactory.buildDo(currentRow, currentColum))
                                .build();
                        if (Objects.isNull(treeMap.get(currentColum))) {
                            List<RewardSymbolDo<AtlantisSymbolDo>> list = new ArrayList<>();
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

    private boolean canPutIn(TreeMap<Integer, List<RewardSymbolDo<AtlantisSymbolDo>>> treeMap, Integer currentColum) {
        // 第一列必处理
        if (Objects.equals(currentColum, ONE_COLUM_INDEX)) {
            return true;
        }
        // 判断上一列的列表是否为空
        return CollectionUtils.isNotEmpty(treeMap.get(currentColum - 1));
    }

    private List<RewardLineDo<AtlantisSymbolDo>> buildRewardLineList(Map<AtlantisSymbolDo, TreeMap<Integer, List<RewardSymbolDo<AtlantisSymbolDo>>>> map) {
        List<RewardLineDo<AtlantisSymbolDo>> list = new ArrayList<>();
        for (Map.Entry<AtlantisSymbolDo, TreeMap<Integer, List<RewardSymbolDo<AtlantisSymbolDo>>>> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (MapUtils.isEmpty(entry.getValue())) {
                continue;
            }
            TreeMap<Integer, List<RewardSymbolDo<AtlantisSymbolDo>>> columMap = entry.getValue();
            if (columMap.size() < RewardLineTypeEnum.THREE.getNo()) {
                continue;
            }
            int totalLineCount = columMap.values()
                    .stream()
                    .map(List::size)
                    .reduce(1, (a, b) -> a * b);
            list.add(RewardLineDo.<AtlantisSymbolDo>builder()
                    .rewardSymbol(entry.getKey())
                    .rewardLineType(RewardLineTypeEnum.valueOfNo(columMap.size()))
                    .rewardLineSubType(getRewardLineSubType(entry.getKey()))
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

    private RewardLineSubTypeEnum getRewardLineSubType(AtlantisSymbolDo rewardSymbol) {
        if (rewardSymbol.isCashSymbol()) {
            return RewardLineSubTypeEnum.CASH;
        } else {
            return RewardLineSubTypeEnum.COMMON;
        }
    }

    private boolean isMatchTargetSymbol(SymbolDo symbol, SymbolDo targetSymbol) {
        return StringUtils.equals(symbol.getId(), targetSymbol.getId())
                || Objects.equals(symbol.getSymbolType(), SymbolTypeEnum.WILD)
                || Objects.equals(targetSymbol.getSymbolType(), SymbolTypeEnum.WILD);
    }

    private void validate(RewardHandleDo<AtlantisSymbolDo> rewardHandle) {
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
        AssertUtil.assertTrue(Objects.equals(rewardHandle.getGameType(), GameTypeEnum.KAPRE), ErrorCode.ERR_PARAM, "just support game type of kapre");
        AssertUtil.assertTrue(rewardHandle.getBetAmount() > 0, ErrorCode.ERR_PARAM, "bet amount must greater than 0");
        SymbolDo[][] symbolArr = rewardHandle.getWheel().getSymbolArr();
        // 校验：滚轮行数
        GameInfoDo gameInfo = rewardHandle.getGameInfo();
        AssertUtil.assertTrue(symbolArr.length == gameInfo.getRow(), ErrorCode.ERR_PARAM, "symbol arr err");
    }

    /**
     * 计算中奖线中奖金额
     *
     * @param rewardLine 中奖线
     * @param betAmount  投注金额
     * @param odds       赔率
     * @param multiple   倍率
     * @return 中奖金额
     */
    @Override
    public BigDecimal calculateLineRewardAmount(RewardLineDo rewardLine, Long betAmount, BigDecimal odds, BigDecimal multiple) {
        if (rewardLine.isCommonReward()) {
            //普通中奖： 投注额 * 赔率 * 中奖线
            return BigDecimal.valueOf(betAmount)
                    .multiply(odds)
                    .multiply(new BigDecimal("" + rewardLine.getRewardLineCount()));
        } else if (rewardLine.isCashReward()) {
            //现金中奖 ： 投注额 * 赔率
            return BigDecimal.valueOf(betAmount)
                    .multiply(odds);
        } else {
            return BigDecimal.valueOf(betAmount)
                    .multiply(new BigDecimal("" + rewardLine.getRewardLineCount()));
        }
    }


}
