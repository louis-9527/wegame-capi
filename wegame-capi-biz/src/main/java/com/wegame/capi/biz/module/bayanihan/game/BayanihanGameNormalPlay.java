package com.wegame.capi.biz.module.bayanihan.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.BayanihanConfig;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.game.BayanihanGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.BayanihanSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.bayanihan.BayanihanGameHelper;
import com.wegame.capi.biz.module.bayanihan.BayanihanSymbolUtil;
import com.wegame.capi.biz.module.bayanihan.reward.BayanihanRewardHandler;
import com.wegame.capi.biz.module.lamang.LamangSymbolUtil;
import com.wegame.capi.common.enums.RewardTypeEnum;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.wegame.capi.biz.module.lamang.LamangGameHelper.LAMANG_GAME_INFO;
import static com.wegame.capi.biz.module.lamang.LamangGameHelper.LAMANG_SCATTER1;
import static com.wegame.capi.biz.module.lamang.LamangSymbolUtil.countGoldenBorderInRow;
import static com.wegame.capi.common.Constants.CONST_1;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
@Component("bayanihanGameNormalPlay")
public class BayanihanGameNormalPlay extends AbstractGame<BayanihanGameRecordDo> {

    private static final GameInfoDo GAME_INFO = BayanihanGameHelper.BAYANIHAN_GAME_INFO;
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final int MAX_SCATTER_IN_COLUMN = 1;
    private static final int MAX_SCATTER_IN_WHEEL = 3;
    private static final List<Integer> SCATTER_IN_COLUMN = Arrays.asList(0, 2, 4);

    @Autowired
    BayanihanConfig bayanihanConfig;
    @Autowired
    BayanihanRewardHandler bayanihanRewardHandler;

    @Override
    public List<BayanihanGameRecordDo> play(GamePlayDo gamePlay) {

        List<BayanihanGameRecordDo> ret = new ArrayList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        LinkedList<FreeWheelHandleDo> freeWheelList = new LinkedList<>();
        handleMainGame(gamePlay, ret, totalRewardAmount, freeWheelList);
        handleFreeGame(gamePlay, ret, totalRewardAmount, freeWheelList);
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }
        return ret;
    }

    private void handleMainGame(GamePlayDo gamePlay, List<BayanihanGameRecordDo> ret, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList) {

    }

    private void handleFreeGame(GamePlayDo gamePlay, List<BayanihanGameRecordDo> ret, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList) {

    }

    /**
     * 生成一轮第一个wheel
     */
    private WheelDo<BayanihanSymbolDo> generateFirstWheel(boolean mustRewardFreeWheel,
                                                       boolean freeWheel) {
        BayanihanSymbolDo[][] symbolArr = new BayanihanSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];

        if (mustRewardFreeWheel) {
            fillScatter(symbolArr);
        }

        fillWheel(symbolArr, freeWheel);

        return WheelDo.<BayanihanSymbolDo>builder()
                .index(WHEEL_DEFAULT_INDEX)
                .freeFlag(freeWheel)
                .symbolArr(symbolArr)
                .build();
    }

    private void fillScatter(BayanihanSymbolDo[][] symbolArr) {
        //todo 135列每一列填充一个scatter
    }

    /**
     * 填充滚轮
     */
    private void fillWheel(BayanihanSymbolDo[][] symbolArr, boolean freeWheel) {

        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(CONST_1, CONST_1000000, GAME_INFO.getRow() * GAME_INFO.getColumn());
        // 当前scatter数量
        int scatterNumberInWheel = BayanihanSymbolUtil.countScatterInWheel(symbolArr);
        TreeMap<String, List<BigDecimal>> playProbabilisticConfig;
        if (freeWheel) {
            playProbabilisticConfig = bayanihanConfig.getProbabilisticConfig().getFreeGameProbabilisticConfig();
        } else {
            playProbabilisticConfig = bayanihanConfig.getProbabilisticConfig().getMainGameProbabilisticConfig();
        }

        int randomNumberIndex = 0;
        for (int column = ONE_COLUM_INDEX; column < GAME_INFO.getColumn(); column++) {
            // 一个reel只能包含一个scatter
            int scatterNumberInColumn = LamangSymbolUtil.countScatterInColumn(symbolArr, column);
            TreeMap<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(column, playProbabilisticConfig.getWheelProbabilisticConfig());
            for (int row = ONE_ROW_INDEX; row < LAMANG_GAME_INFO.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    // 当前元素非空，则不生成
                    randomNumberIndex = randomNumberIndex + 1;
                    continue;
                }

                AssertUtil.assertTrue(MapUtils.isNotEmpty(columProbabilisticMap), ErrorCode.ERR_INTER);
                Integer randomNumber = randomNumberList.get(randomNumberIndex);
                SymbolDo symbolDo = null;
                if (freeWheel) {
                    if (scatterNumberInColumn >= MAX_SCATTER_NUMBER_IN_REEL || scatterNumberInWheel >= MAX_SCATTER_NUMBER_IN_WHEEL || scatter1CountInBatch.get() >= MAX_SCATTER1_NUMBER_IN_BATCH) {
                        // 列scatter达到上限 || 滚轮scatter达到上限，则生产非scatter元素 || 一轮中产生的scatter+1元素超过3个
                        // 生成元素：非scatter元素
                        symbolDo = randomNonScatterSymbol(randomNumber, LAMANG_GAME_INFO, columProbabilisticMap);
                    } else {
                        // 生成元素：随机生成
                        symbolDo = randomNormalSymbol(randomNumber, LAMANG_GAME_INFO, columProbabilisticMap);
                    }
                } else {
                    if (scatterNumberInColumn >= MAX_SCATTER_NUMBER_IN_REEL || scatterNumberInWheel >= MAX_SCATTER_NUMBER_IN_WHEEL) {
                        // 列scatter达到上限 || 滚轮scatter达到上限，则生产非scatter元素
                        // 生成元素：非scatter元素
                        symbolDo = randomNonScatterSymbol(randomNumber, LAMANG_GAME_INFO, columProbabilisticMap);
                    } else {
                        // 生成元素：随机生成
                        symbolDo = randomNormalSymbol(randomNumber, LAMANG_GAME_INFO, columProbabilisticMap);
                    }
                }
                AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER);
                // 构建元素金边
                int goldenInRow = countGoldenBorderInRow(symbolArr, row);
                // 如果当前行有金边元素，则生成非金边元素
                if (!symbolDo.isScatterSymbol() && goldenInRow < MAX_GOLDEN_NUMBER_IN_ROW) {
                    boolean symbolGoldBorder = generateSymbolGoldBorder(playProbabilisticConfig, column);
                    LamangSymbolDo lamangSymbolDo = LamangSymbolDo.buildFromSymbol(symbolDo, symbolGoldBorder);
                    symbolArr[row][column] = lamangSymbolDo;
                } else {
                    LamangSymbolDo lamangSymbolDo = LamangSymbolDo.buildFromSymbol(symbolDo, false);
                    symbolArr[row][column] = lamangSymbolDo;
                }
                if (symbolDo.isScatterSymbol()) {
                    scatterNumberInColumn = scatterNumberInColumn + 1;
                    scatterNumberInWheel = scatterNumberInWheel + 1;
                }
                // 如果是scatter+1 增加计数
                if (symbolDo.getId().equals(LAMANG_SCATTER1.getId())) {
                    scatter1CountInBatch.incrementAndGet();
                }
                randomNumberIndex = randomNumberIndex + 1;
            }
        }
    }

    private void loopPlayOnPreGameRecord(GamePlayDo gamePlay,
                                         List<BayanihanGameRecordDo> ret,
                                         AtomicLong totalRewardAmount,
                                         BayanihanGameRecordDo preGameRecord,
                                         LinkedList<FreeWheelHandleDo> freeWheelList,
                                         Long parentBetRecordId,
                                         MangkukulamConfig.Stage stage) {
        if (canContinuePlayOnPreWheel(gamePlay, preGameRecord.getRewardResult(), totalRewardAmount)) {
            WheelDo<MangkukulamSymbolDo> wheel = generateFromPreWheel(preGameRecord, stage);
            RewardResultDo<MangkukulamSymbolDo> rewardResult = bayanihanRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, preGameRecord.getRewardResult().getTotalMultiple(), totalRewardAmount.get()));
            long gameRecordId = generateGameRecordId();
            MangkukulamGameRecordDo gameRecord = buildGameRecord(gameRecordId, preGameRecord.getId(), gamePlay.getBetRecordId(), parentBetRecordId, preGameRecord.getBatchId(), wheel, rewardResult);
            ret.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, parentBetRecordId, stage);
        } else {
            if (!preGameRecord.getRewardResult().getAccumulateReachMaxMultiple()) {
                if (preGameRecord.getRewardResult().isHasRewardFreeWheel()) {
                    freeWheelList.add(FreeWheelHandleDo.builder()
                            .parentGameRecord(preGameRecord)
                            .taskTimes(preGameRecord.getRewardResult().getRewardFreeWheelTimes())
                            .build());
                }
            } else {
                // help gc
                freeWheelList.clear();
            }
        }
    }

    private boolean canContinuePlayOnPreWheel(GamePlayDo gamePlay, RewardResultDo<BayanihanSymbolDo> rewardResult, AtomicLong totalRewardAmount) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return false;
        }
        if (rewardResult.getAccumulateReachMaxMultiple()) {
            log.warn("reach max reward multiple bet. totalRewardAmount: {}, betAmount: {}", totalRewardAmount.get(), gamePlay.getBetAmount());
            return false;
        }
        return CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList());
    }

    /**
     * 用于记录收集信息
     */
    @Data
    @SuperBuilder
    private static class CollectionInfo {
        private BigDecimal initialMultiplier;

        private BigDecimal currentMultiplier;

        private int upgradeCost;

        private int totalCollectSymbolCount;

        private int currentRemainCollectSymbolCount;

        public BayanihanGameRecordDo.CollectionInfo snapshot() {
            return BayanihanGameRecordDo.CollectionInfo.builder()
                    .initialMultiple(initialMultiplier)
                    .upgradeCost(upgradeCost)
                    .collectSymbolCount(totalCollectSymbolCount)
                    .build();
        }
    }

    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        // parentGameRecord
        private BayanihanGameRecordDo parentGameRecord;
        // 任务执行次数
        private int taskTimes;
    }
}