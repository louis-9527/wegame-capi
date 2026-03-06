package com.wegame.capi.biz.module.atlantis.reward;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.OddsCalculateBuilder;
import com.wegame.capi.biz.config.domain.AtlantisConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.bizevent.GameBizEventDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.atlantis.AtlantisBizEventHandler;
import com.wegame.capi.biz.module.base.RewardHelper;
import com.wegame.capi.biz.module.base.odds.OddsCalculateStrategy;
import com.wegame.capi.biz.module.base.reward.AbstractRewardHandler;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.*;
import com.wegame.capi.common.utils.GameUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;


@Slf4j
@Component("atlantisRewardHandler")
public class AtlantisRewardHandler extends AbstractRewardHandler<SymbolDo> {
    private static final Integer SCATTER_COUNT_FOR_FREE_SPIN = 4;
    private static final Integer BIZ_EVENT_TOTAL_STEP_COUNT = 24;

    @Autowired
    OddsCalculateStrategy<SymbolDo> atlantisOddsCalculateStrategy;

    @Autowired
    AtlantisConfig atlantisConfig;
    @Autowired
    RandomService randomService;
    @Autowired
    private AtlantisBizEventHandler atlantisBizEventHandler;

    @Override
    public RewardResultDo<SymbolDo> handle(RewardHandleDo<SymbolDo> rewardHandle) {
        // 1、业务校验
        this.validate(rewardHandle);
        // 2、计算中奖线
        List<RewardLineDo<SymbolDo>> rewardLineList = this.calcRewardLineList(rewardHandle.getGameInfo(), rewardHandle.getWheel());
        if (CollectionUtil.isEmpty(rewardLineList)) {
            // 未中奖
            // 免费轮计算-根据scatter数量进行计算
            int freeWheelTimes = this.calcFreeWheelTimes(rewardHandle);
            return RewardResultDo.<SymbolDo>builder()
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
//        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // 中奖坐标列表
        List<RewardLineDetailDo<SymbolDo>> rewardLineDetailList = Lists.newArrayList();
        for (RewardLineDo<SymbolDo> rewardLine : rewardLineList) {
            // 计算赔率
            BigDecimal odds = atlantisOddsCalculateStrategy.calculateOdds(OddsCalculateBuilder.build(rewardHandle, rewardLine.getRewardSymbol(), rewardLine.getRewardLineType()));
            // 计算（中奖线）中奖金额：
            // 普通中奖 ：总金额 = 投注金额 * 赔率 * 中奖线数量
            BigDecimal lineRewardAmount = super.calculateLineRewardAmount(rewardLine, rewardHandle.getBetAmount(), odds, BigDecimal.ONE);
            // 累加中奖总金额
            totalRewardAmount = totalRewardAmount + lineRewardAmount.longValue();
            // 中奖线明细列表
            rewardLineDetailList.add(RewardLineDetailDo.<SymbolDo>builder()
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

        // 中奖事件
        List<BizEventEnum> bizEventList = this.rewardBizEventHandler(rewardLineList, rewardHandle);
        // 黄金奶牛处理
        List<GameBizEventDo> effectBizEventList = this.goldCowEventEffectHandler(bizEventList, totalRewardAmount, rewardHandle);
        // 5.构建中奖结果
        boolean reachMaxMultiple = reachMaxMultiple(
                rewardHandle.getGamePlayType(),
                Objects.isNull(rewardHandle.getCurrentTotalRewardAmount()) ? totalRewardAmount : rewardHandle.getCurrentTotalRewardAmount() + totalRewardAmount,
                rewardHandle.getBetAmount(),
                atlantisConfig.getRewardMaxMultiple());
        // 6.返回中奖信息
        return RewardResultDo.<SymbolDo>builder()
                .betAmount(rewardHandle.getBetAmount())
                .rewardType(RewardTypeEnum.valueOfRewardLineCont(rewardLineList.size()))
                .rewardSubType(RewardHelper.calculateRewardSubType(rewardHandle.getBetAmount(), totalRewardAmount))
                .totalRewardAmount(totalRewardAmount)
                .rewardLineDetailList(rewardLineDetailList)
                .hasRewardFreeWheel(false)
                .rewardFreeWheelTimes(0)
                .bizEventList(bizEventList)
                .effectBizEventList(effectBizEventList)
                .accumulateReachMaxMultiple(reachMaxMultiple)
                .build();
    }

    private void reBuildTotalRewardAmount(GameBizEventDo gameBizEventDo, AtomicLong totalRewardAmount) {
        long extRewardAmount = (long) gameBizEventDo.getExtInfo();
        totalRewardAmount.addAndGet(extRewardAmount);
    }

    private List<GameBizEventDo> goldCowEventEffectHandler(List<BizEventEnum> bizEventList, Long totalRewardAmount, RewardHandleDo<SymbolDo> rewardHandle) {
        if (CollectionUtils.isEmpty(bizEventList) || !bizEventList.contains(BizEventEnum.EVENT_GOLDEN_COW)) {
            return null;
        }
        List<GameBizEventDo> bizEventDoList = Lists.newArrayList();
        // 统计黄金奶牛事件的个数
        List<Integer> goldCowEventIndexList = Lists.newArrayList();
        for (int i = 0; i < bizEventList.size(); i++) {
            BizEventEnum bizEventEnum = bizEventList.get(i);
            if (Objects.equals(bizEventEnum, BizEventEnum.EVENT_GOLDEN_COW)) {
                goldCowEventIndexList.add(i);
            }
        }
        AtomicLong allTotalRewardAmount = new AtomicLong(totalRewardAmount + rewardHandle.getCurrentTotalRewardAmount());
        // 如果是固定事件
        if (CollectionUtil.isNotEmpty(rewardHandle.getFixedBizEventList())) {
            // 固定事件的个数
            List<GameBizEventDo> goldCowFixedEventList = rewardHandle.getFixedBizEventList().stream().filter(bizEventEnum -> Objects.equals(BizEventEnum.EVENT_GOLDEN_COW, bizEventEnum.getBizEventType())).collect(Collectors.toList());
            // 个数必须匹配
            AssertUtil.assertTrue(goldCowEventIndexList.size() == goldCowFixedEventList.size(), ErrorCode.ERR_PARAM, "reward error");
            // 单个处理
            for (GameBizEventDo fixedGameBizEventDo : goldCowFixedEventList) {
                Long extInfo = this.calcGoldCowEventExtRewardAmount(rewardHandle, allTotalRewardAmount.get(), fixedGameBizEventDo.getEventMultiple());
                GameBizEventDo bizEventDo = GameBizEventDo.builder()
                        .sort(fixedGameBizEventDo.getSort())
                        .bizEventType(fixedGameBizEventDo.getBizEventType())
                        .eventMultiple(fixedGameBizEventDo.getEventMultiple())
                        .extInfo(extInfo)
                        .build();
                bizEventDoList.add(bizEventDo);
                this.reBuildTotalRewardAmount(bizEventDo, allTotalRewardAmount);
            }
        } else {
            // 非固定事件
            for (Integer goldCowEventIndex : goldCowEventIndexList) {
                GameBizEventDo bizEventDo = atlantisBizEventHandler.handleEventGoldenCow(goldCowEventIndex);
                // 额外增加的中奖金额
                bizEventDo.setExtInfo(this.calcGoldCowEventExtRewardAmount(rewardHandle, allTotalRewardAmount.get(), bizEventDo.getEventMultiple()));
                bizEventDoList.add(bizEventDo);
                this.reBuildTotalRewardAmount(bizEventDo, allTotalRewardAmount);
            }
        }
        return bizEventDoList;
    }

    public Long calcGoldCowEventExtRewardAmount(RewardHandleDo<SymbolDo> rewardHandler, long totalRewardAmount, BigDecimal eventMultiple) {
        if (eventMultiple.compareTo(BigDecimal.ONE) >= 0) {
            BigDecimal extMultiple = eventMultiple.subtract(BigDecimal.ONE);
            // 额外增加的中奖金额
            return extMultiple.multiply(new BigDecimal(totalRewardAmount)).longValue();
        }
        return 0L;
    }


    /**
     * 产生中奖事件
     * 上一轮总的中奖次数
     *
     * @param rewardLineList 中奖线列表
     * @return 中奖事件列表
     */
    private List<BizEventEnum> rewardBizEventHandler(List<RewardLineDo<SymbolDo>> rewardLineList, RewardHandleDo<SymbolDo> rewardHandle) {
        // 存在固定事件则返回固定事件-分离变与不变
        if (CollectionUtil.isNotEmpty(rewardHandle.getFixedBizEventList())) {
            return rewardHandle.getFixedBizEventList().stream().map(GameBizEventDo::getBizEventType).collect(Collectors.toList());
        } else {
            List<BizEventEnum> bizEventList = Lists.newArrayList();
            Integer biEventBaseNumber = rewardHandle.getTotalRewardCount();
            AssertUtil.assertNotNull(biEventBaseNumber, ErrorCode.ERR_INTER, "biEventBaseNumber is null");

            for (int i = 0; i < rewardLineList.size(); i++) {
                TreeMap<Integer, TreeMap<BizEventEnum, BigDecimal>> bizEventProbabilisticConfig = atlantisConfig.getProbabilisticConfig().getBizEventProbabilisticConfig();
                TreeMap<BizEventEnum, BigDecimal> bizEventEnumBigDecimalTreeMap = bizEventProbabilisticConfig.get(this.getBizEventStepIndex(biEventBaseNumber));
                int randomInt = randomService.randomInt(Constants.CONST_1, Constants.CONST_1000000);
                BizEventEnum bizEventEnum = GameUtil.randomKey(bizEventEnumBigDecimalTreeMap, Constants.CONST_10000, randomInt);
                if (Objects.nonNull(bizEventEnum) && !Objects.equals(bizEventEnum, BizEventEnum.EVENT_NONE)) {
                    bizEventList.add(bizEventEnum);
                }
                biEventBaseNumber++;
            }
            return bizEventList;
        }
    }

    private Integer getBizEventStepIndex(Integer biEventBaseNumber) {
        return biEventBaseNumber % BIZ_EVENT_TOTAL_STEP_COUNT;
    }

    private List<RewardLineDo<SymbolDo>> calcRewardLineList(GameInfoDo gameInfo, WheelDo<SymbolDo> wheel) {
        // 统计元素的个数,排除scatter,wild
        Map<SymbolDo, List<RewardSymbolDo<SymbolDo>>> symbolDoListMap = new HashMap<>();
        // 统计wild 个数
        List<RewardSymbolDo<SymbolDo>> wildSymbolDoList = Lists.newArrayList();
        SymbolDo[][] symbolArr = wheel.getSymbolArr();
        for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
            for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
                SymbolDo symbolDo = symbolArr[row][column];
                // scatter 不统计
                if (symbolDo.isScatterSymbol()) {
                    continue;
                }
                // 元素组装
                RewardSymbolDo<SymbolDo> rewardSymbolDo = RewardSymbolDo.<SymbolDo>builder()
                        .symbol(symbolDo)
                        .index(IndexFactory.buildDo(row, column))
                        .build();
                // wild-单独统计
                if (symbolDo.isWildSymbol()) {
                    wildSymbolDoList.add(rewardSymbolDo);
                    continue;
                }
                // 中奖统计
                List<RewardSymbolDo<SymbolDo>> rewardSymbolDos = symbolDoListMap.get(symbolDo);
                if (CollectionUtil.isEmpty(rewardSymbolDos)) {
                    symbolDoListMap.put(symbolDo, Lists.newArrayList(rewardSymbolDo));
                } else {
                    rewardSymbolDos.add(rewardSymbolDo);
                }
            }
        }
        // 满足条件的元素构建中奖线
        return this.buildRewardLineList(symbolDoListMap, wildSymbolDoList);

    }

    private List<RewardLineDo<SymbolDo>> buildRewardLineList(Map<SymbolDo, List<RewardSymbolDo<SymbolDo>>> symbolDoListMap, List<RewardSymbolDo<SymbolDo>> wildSymbolDoList) {
        List<RewardLineDo<SymbolDo>> list = new ArrayList<>();
        for (Map.Entry<SymbolDo, List<RewardSymbolDo<SymbolDo>>> entry : CollectionUtils.emptyIfNull(symbolDoListMap.entrySet())) {
            // wild加入到中奖线计算
            List<RewardSymbolDo<SymbolDo>> rewardSymbolDoList = entry.getValue();
            rewardSymbolDoList.addAll(wildSymbolDoList);
            // 不满足条件的元素，不统计-元素个数小于8个
            if (rewardSymbolDoList.size() < RewardLineTypeEnum.EIGHT.getNo()) {
                continue;
            }
            list.add(RewardLineDo.<SymbolDo>builder()
                    .rewardSymbol(entry.getKey())
                    .rewardLineType(RewardLineTypeEnum.valueOfNo(entry.getValue().size()))
                    .rewardLineSubType(RewardLineSubTypeEnum.COMMON)
                    .rewardLineCount(1)
                    .rewardSymbolList(rewardSymbolDoList)
                    .build());

        }
        return list;
    }

    /**
     * 计算免费轮次数
     * 4个scatter中8次免费轮,每增加一个scatter额外添加1个
     *
     * @param rewardHandle 中奖处理参数
     * @return 免费轮次数
     */
    private int calcFreeWheelTimes(RewardHandleDo<SymbolDo> rewardHandle) {
        Integer scatterCount = super.scatterCount(rewardHandle);
        AtlantisConfig.FreeWheelTimesConfig freeWheelTimesConfig = atlantisConfig.getFreeWheelTimesConfig();
        if (scatterCount < SCATTER_COUNT_FOR_FREE_SPIN) {
            return 0;
        }
        int extraScatterCount = scatterCount - SCATTER_COUNT_FOR_FREE_SPIN;
        return freeWheelTimesConfig.getBaseFreeWheelTimes() + extraScatterCount * freeWheelTimesConfig.getStep();
    }

    private void validate(RewardHandleDo<SymbolDo> rewardHandle) {
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
        AssertUtil.assertTrue(Objects.equals(rewardHandle.getGameType(), GameTypeEnum.ATLANTIS), ErrorCode.ERR_PARAM, "just support game type of atlantis");
        AssertUtil.assertTrue(rewardHandle.getBetAmount() > 0, ErrorCode.ERR_PARAM, "bet amount must greater than 0");
        SymbolDo[][] symbolArr = rewardHandle.getWheel().getSymbolArr();
        // 校验：滚轮行数
        GameInfoDo gameInfo = rewardHandle.getGameInfo();
        AssertUtil.assertTrue(symbolArr.length == gameInfo.getRow(), ErrorCode.ERR_PARAM, "symbol arr err");
    }
}
