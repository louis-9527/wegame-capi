package com.wegame.capi.biz.module.kapre.game;

import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.KapreConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.KapreGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.*;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractWheelChartGame;
import com.wegame.capi.biz.module.kapre.KapreGameHelper;
import com.wegame.capi.biz.module.kapre.reward.KapreRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.utils.GameUtil;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Component("kapreGameNormalPlay")
public class KapreGameNormalPlay extends AbstractWheelChartGame<KapreGameRecordDo> {

    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = KapreGameHelper.KAPRE_GAME_INFO;

    @Autowired
    KapreConfig kapreConfig;
    @Autowired
    KapreRewardHandler kapreRewardHandler;

    @Override
    public List<KapreGameRecordDo> play(GamePlayDo gamePlay) {
        List<KapreGameRecordDo> ret = new ArrayList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // 首轮游戏处理
        OneWheelPlayWrapDo firstWheelPlayWrapDo = this.oneWheelPlay(gamePlay, RECORD_DEFAULT_PARENT_ID, totalRewardAmount, ret, null, BigDecimal.ZERO, false);
        // 免费轮处理-迭代替代递归
        this.freeWheelHandler(firstWheelPlayWrapDo.getGameRecordDo(), totalRewardAmount, ret, gamePlay);
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }
        return ret;
    }


    private OneWheelPlayWrapDo oneWheelPlay(GamePlayDo gamePlay,
                                            Long parentId,
                                            AtomicLong totalRewardAmount,
                                            List<KapreGameRecordDo> ret,
                                            IndexDo preDynamicWildIndex,
                                            BigDecimal currentMultiple,
                                            boolean freeFlag) {
        // 1.生成滚轮
        WheelDo<KapreSymbolDo> wheelDo = this.generateCommonWheel(this.buildWheelChartModel(gamePlay, freeFlag),
                this.buildCashSymbolMultipleProbabilisticConfig(freeFlag), freeFlag);
        ;
        IndexDo dynamicWildIndex = null;
        // 1.1 免费轮注入动游荡wild
        if (freeFlag) {
            dynamicWildIndex = this.injectFreeWheelDynamicWildSymbol(wheelDo, preDynamicWildIndex);
        }
        // 2.中奖以及滚轮后处理
        RewardResultDo<KapreSymbolDo> firstRewardResult = this.rewardAndWheelAfterHandler(gamePlay, wheelDo, currentMultiple, totalRewardAmount.get());
        // 3.组装首轮游戏记录
        KapreGameRecordDo gameRecordDo = this.buildGameRecord(generateGameRecordId(), parentId, gamePlay.getBetRecordId(), wheelDo, firstRewardResult);
        ret.add(gameRecordDo);
        // 4.总的中奖金额添加
        totalRewardAmount.addAndGet(firstRewardResult.getTotalRewardAmount());
        return OneWheelPlayWrapDo.builder().gameRecordDo(gameRecordDo).dynamicWildIndex(dynamicWildIndex).build();
    }

    private IndexDo injectFreeWheelDynamicWildSymbol(WheelDo<KapreSymbolDo> wheelDo, IndexDo preDynamicWildIndex) {
        // 生成一个游荡wild
        KapreSymbolDo dynamicWildSymbolDo = this.buildKapreSymbol(KapreGameHelper.KAPRE_Dynamic_Wild, this.buildCashSymbolMultipleProbabilisticConfig(true));
        // 直到和上一个游离wild不一样为止
        IndexDo currDynamicWildIndex;
        do {
            int randomRowNum = randomService.randomInt(0, GAME_INFO.getRow() - 1);
            // 不生成在reel 1
            int randomColumRum = randomService.randomInt(1, GAME_INFO.getColumn() - 1);
            currDynamicWildIndex = IndexFactory.buildDo(randomRowNum, randomColumRum);
        } while (Objects.equals(currDynamicWildIndex, preDynamicWildIndex));
        wheelDo.getSymbolArr()[currDynamicWildIndex.getRowIndex()][currDynamicWildIndex.getColumnIndex()] = dynamicWildSymbolDo;
        return currDynamicWildIndex;
    }


    private void freeWheelHandler(KapreGameRecordDo firstGameRecord, AtomicLong totalRewardAmount, List<KapreGameRecordDo> ret, GamePlayDo gamePlay) {
        // 是否需要处理
        if (!this.canContinuePlayOnPreGameRecord(gamePlay, firstGameRecord.getRewardResult(), totalRewardAmount)) {
            return;
        }
        LinkedList<FreeWheelHandleDo> freeWheelQueue = Lists.newLinkedList();
        freeWheelQueue.addLast(FreeWheelHandleDo.builder().parentGameRecord(firstGameRecord).taskTimes(firstGameRecord.getRewardResult().getRewardFreeWheelTimes()).build());
        // 初始队列
        while (!freeWheelQueue.isEmpty()) {
            // 取出一个任务
            FreeWheelHandleDo freeWheelHandleDo = freeWheelQueue.pollFirst();
            KapreGameRecordDo parentGameRecord = freeWheelHandleDo.getParentGameRecord();
            // 是否需要继续处理
            if (!this.canContinuePlayOnPreGameRecord(gamePlay, firstGameRecord.getRewardResult(), totalRewardAmount)) {
                return;
            }
            long parentGameRecordId = parentGameRecord.getId();
            IndexDo preDynamicWildIndex = null;
            for (int i = 0; i < freeWheelHandleDo.getTaskTimes(); i++) {
                OneWheelPlayWrapDo freeWheelPlayWrapDo = this.oneWheelPlay(gamePlay, parentGameRecordId, totalRewardAmount, ret, preDynamicWildIndex, BigDecimal.ZERO, true);
                // parentGameRecord 添加子gameRecordId
                parentGameRecord.addGameRecordId(freeWheelPlayWrapDo.getGameRecordDo().getId());
                // 如果有中免费轮，则加入队列
                RewardResultDo<KapreSymbolDo> freeRewardResult = freeWheelPlayWrapDo.getRewardResult();
                if (freeRewardResult.isHasRewardFreeWheel() && freeRewardResult.getRewardFreeWheelTimes() > 0) {
                    freeWheelQueue.addLast(FreeWheelHandleDo.builder().parentGameRecord(freeWheelPlayWrapDo.getGameRecordDo()).taskTimes(freeRewardResult.getRewardFreeWheelTimes()).build());
                }
                preDynamicWildIndex = freeWheelPlayWrapDo.getDynamicWildIndex();
            }
        }
    }

    private RewardResultDo<KapreSymbolDo> rewardAndWheelAfterHandler(GamePlayDo gamePlay, WheelDo<KapreSymbolDo> wheel, BigDecimal currentMultiple, Long currentTotalRewardAmount) {
        // 中奖处理
        RewardResultDo<KapreSymbolDo> rewardResult = kapreRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, currentMultiple, currentTotalRewardAmount));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 中奖后滚轮后处理
        this.wheelAfterHandler(rewardResult, wheel);
        return rewardResult;
    }

    /**
     * 非现金symbol中奖线内的wild，嵌套symbol设置成null
     *
     * @param handleResultDo handleResultDo
     * @param wheel          wheel
     */
    private void wheelAfterHandler(RewardResultDo<KapreSymbolDo> handleResultDo, WheelDo<KapreSymbolDo> wheel) {
        // 统计出所有现金symbol中奖线内的wild坐标
        List<IndexDo> cashSymbolWildIndexList = Optional.ofNullable(handleResultDo.getRewardLineDetailList()).orElse(Collections.emptyList()).stream()
                // 所有现金symbol 中奖线详情
                .filter(line -> line.getRewardSymbol().isCashSymbol())
                // 现金symbol 中奖线元素详情
                .flatMap(line -> line.getRewardSymbolList().stream())
                // 现金symbol 中奖线元素详情 中的wild元素
                .filter(rewardSymbol -> rewardSymbol.getSymbol().isWildSymbol()).map(RewardSymbolDo::getIndex)
                .collect(Collectors.toList());

        KapreSymbolDo[][] symbolArr = wheel.getSymbolArr();
        for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
            for (int columnNum = 0; columnNum < GAME_INFO.getColumn(); columnNum++) {
                KapreSymbolDo kapreSymbolDo = symbolArr[rowNum][columnNum];
                if (!kapreSymbolDo.isWildSymbol()) {
                    continue;
                }
                IndexDo currentWildIndex = IndexFactory.buildDo(rowNum, columnNum);
                // 非现金symbol中奖线内的wild，嵌套symbol设置成null
                if (!cashSymbolWildIndexList.contains(currentWildIndex)) {
                    kapreSymbolDo.setNestSymbol(null);
                }
            }
        }
    }


    /**
     * 通用滚轮生成
     *
     * @param wheelChartModel                       轮代表moudle 二维数组，一维：reel 二维：元素id轮带
     * @param cashSymbolMultipleProbabilisticConfig 现金symbol 概率配置
     * @return 滚轮
     */
    private WheelDo<KapreSymbolDo> generateCommonWheel(List<List<String>> wheelChartModel,
                                                       TreeMap<BigDecimal, BigDecimal> cashSymbolMultipleProbabilisticConfig,
                                                       boolean freeFlag) {
        KapreSymbolDo[][] symbolArr = new KapreSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];
        // 通过当前轮带，按照colum 生成整列元素
        for (int columNum = 0; columNum < GAME_INFO.getColumn(); columNum++) {
            // 当前列(reel)的轮带
            List<String> columWheelChart = wheelChartModel.get(columNum);
            // 生成随机数：从当前随机数开始，顺序循环取row个元素id
            int reelSymbolStartIndex = randomService.randomInt(0, columWheelChart.size() - 1);
            // 取出当前列的元素id 进行元素填充
            List<String> currentColumSymbolIdList = GameUtil.circularGetList(columWheelChart, reelSymbolStartIndex, GAME_INFO.getRow());
            // 填充
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                SymbolDo symbolDo = GAME_INFO.getSymbolById(currentColumSymbolIdList.get(rowNum));
                AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER, "symbolDo is null of id " + currentColumSymbolIdList.get(rowNum));
                // 构建kapreSymbolDo
                KapreSymbolDo kapreSymbolDo = this.buildKapreSymbol(symbolDo, cashSymbolMultipleProbabilisticConfig);
                // 填充
                symbolArr[rowNum][columNum] = kapreSymbolDo;
            }
        }
        // 返回：滚轮
        return WheelDo.<KapreSymbolDo>builder()
                .index(WHEEL_DEFAULT_INDEX)
                .freeFlag(freeFlag)
                .symbolArr(symbolArr)
                .build();
    }

    private KapreSymbolDo buildKapreSymbol(SymbolDo symbolDo, TreeMap<BigDecimal, BigDecimal> cacheSymbolMultipleProbabilisticConfig) {
        // 区分是否wild
        if (symbolDo.isWildSymbol()) {
            // wild,则构建嵌套的现金symbol
            // 随机生成现金元素倍率
            BigDecimal cashSymbolMultiple = GameUtil.randomKey(cacheSymbolMultipleProbabilisticConfig, Constants.CONST_10000, randomService.randomInt(1, Constants.CONST_1000000));
            // 构建现金元素
            KapreSymbolDo cashSymbol = KapreSymbolDo.buildFromSymbol(KapreGameHelper.KAPRE_CASH_SYMBOL, cashSymbolMultiple, null);
            return KapreSymbolDo.buildFromSymbol(symbolDo, BigDecimal.ZERO, cashSymbol);
        } else {
            KapreSymbolDo kapreSymbolDo = KapreSymbolDo.buildFromSymbol(symbolDo, BigDecimal.ZERO, null);
            if (kapreSymbolDo.isCashSymbol()) {
                // 是否现金symbol
                // 随机生成现金元素倍率
                BigDecimal cashSymbolMultiple = GameUtil.randomKey(cacheSymbolMultipleProbabilisticConfig, Constants.CONST_10000, randomService.randomInt(1, Constants.CONST_1000000));
                kapreSymbolDo.setSymbolMultiple(cashSymbolMultiple);
            }
            return kapreSymbolDo;

        }
    }

    /**
     * 获取轮代表moudel
     *
     * @param gamePlay gameplay
     * @return 轮代表moudle
     */
    private List<List<String>> buildWheelChartModel(GamePlayDo gamePlay, boolean isFreeSpinFlag) {
        TreeMap<String, BigDecimal> wheelChartProbabilisticConfig;
        TreeMap<String, List<List<String>>> wheelChartModelConfig;
        // 获取轮代表
        if (isFreeSpinFlag) {
            // 免费轮
            wheelChartProbabilisticConfig = kapreConfig.getProbabilisticConfig().getWheelChartProbabilisticConfig().getFreeProbabilisticConfig();
            wheelChartModelConfig = kapreConfig.getWheelChart().getNormal();
        } else {
            // 付费轮-正常投注
            if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.NORMAL_BET)) {
                wheelChartProbabilisticConfig = kapreConfig.getProbabilisticConfig().getWheelChartProbabilisticConfig().getNormalBetPayProbabilisticConfig();
                wheelChartModelConfig = kapreConfig.getWheelChart().getNormal();
            } else if (Objects.equals(gamePlay.getBetType(), BetTypeEnum.BUY_BET)) {
                // 付费轮-购买投注
                wheelChartProbabilisticConfig = kapreConfig.getProbabilisticConfig().getWheelChartProbabilisticConfig().getBuyBetPayProbabilisticConfig();
                wheelChartModelConfig = kapreConfig.getWheelChart().getBuy();
            } else {
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT BET TYPE");
            }
        }
        // 根据概率随机出model名称
        String modelName = GameUtil.randomKey(wheelChartProbabilisticConfig, Constants.CONST_10000, randomService.randomInt(1, Constants.CONST_1000000));
        List<List<String>> wheelChartModel = wheelChartModelConfig.get(modelName);
        AssertUtil.assertNotNull(wheelChartModel, ErrorCode.ERR_INTER, "wheelChartModel is null");
        return wheelChartModel;
    }

    /**
     * 构建现金symbol 倍率概率配置
     *
     * @return 倍率概率配置
     */
    private TreeMap<BigDecimal, BigDecimal> buildCashSymbolMultipleProbabilisticConfig(boolean isFreeSpinFlag) {
        if (isFreeSpinFlag) {
            // 免费轮
            return kapreConfig.getProbabilisticConfig().getMultipleProbabilisticConfig().getFreeProbabilisticConfig();
        } else {
            // 付费轮
            return kapreConfig.getProbabilisticConfig().getMultipleProbabilisticConfig().getPayProbabilisticConfig();
        }
    }

    private boolean canContinuePlayOnPreGameRecord(GamePlayDo gamePlay, RewardResultDo<KapreSymbolDo> rewardResult, AtomicLong totalRewardAmount) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (rewardResult.isHasRewardFreeWheel() && rewardResult.getRewardFreeWheelTimes() > 0) {
            return true;
        }
        if (rewardResult.getAccumulateReachMaxMultiple()) {
            log.warn("reach max reward multiple bet. totalRewardAmount: {}, payAmount: {}", totalRewardAmount.get(), gamePlay.getPayAmount());
            return false;
        }
        return CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList());
    }

    private KapreGameRecordDo buildGameRecord(Long id,
                                              Long parentId,
                                              Long betRecordId,
                                              WheelDo<KapreSymbolDo> wheel,
                                              RewardResultDo<KapreSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return KapreGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(id)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(null)
                .lastGameFlag(false)
                .build();
    }

    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        // parentGameRecord
        private KapreGameRecordDo parentGameRecord;
        // 任务执行次数
        private int taskTimes;
    }

    @Data
    @SuperBuilder
    private static class OneWheelPlayWrapDo {
        private KapreGameRecordDo gameRecordDo;
        private IndexDo dynamicWildIndex;

        public RewardResultDo<KapreSymbolDo> getRewardResult() {
            return gameRecordDo.getRewardResult();
        }

    }
}