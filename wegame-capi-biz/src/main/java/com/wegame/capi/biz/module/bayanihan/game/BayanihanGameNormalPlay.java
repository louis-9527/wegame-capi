package com.wegame.capi.biz.module.bayanihan.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.BayanihanConfig;
import com.wegame.capi.biz.domain.biz.game.BayanihanGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.BayanihanSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.bayanihan.BayanihanGameHelper;
import com.wegame.capi.biz.module.bayanihan.BayanihanProbabilisticUtil;
import com.wegame.capi.biz.module.bayanihan.BayanihanSymbolUtil;
import com.wegame.capi.biz.module.bayanihan.reward.BayanihanRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.Constants;
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

import static com.wegame.capi.common.Constants.CONST_1;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
@Component("bayanihanGameNormalPlay")
public class BayanihanGameNormalPlay extends AbstractGame<BayanihanGameRecordDo> {

    private static final GameInfoDo GAME_INFO = BayanihanGameHelper.BAYANIHAN_GAME_INFO;

    private static final String COLLECT_SYMBOL_ID = BayanihanGameHelper.BAYANIHAN_SymbolH5.getId();
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final int MAX_SCATTER_IN_COLUMN = 1;
    private static final int MAX_SCATTER_IN_WHEEL = 3;
    private static final int DEFAULT_STICKY_WILD_TIMES = 3;
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
        WheelDo<BayanihanSymbolDo> firstWheel = null;
        switch (gamePlay.getBetType()) {
            // 正常投注
            case NORMAL_BET:
                firstWheel = generateFirstWheel(false, false, null);
                break;
            // 购买投注
            case BUY_BET:
                firstWheel = generateFirstWheel(true, false, null);
                break;
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }

        // 默认都是1倍
        RewardResultDo<BayanihanSymbolDo> rewardResult = bayanihanRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, firstWheel, null, totalRewardAmount.get()));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 组装-游戏记录
        long gameRecordId = generateGameRecordId();
        BayanihanGameRecordDo gameRecord = buildGameRecord(gameRecordId, RECORD_DEFAULT_PARENT_ID, gamePlay.getBetRecordId(), gameRecordId, firstWheel, rewardResult, null);
        ret.add(gameRecord);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, false, null);
    }

    private void handleFreeGame(GamePlayDo gamePlay, List<BayanihanGameRecordDo> ret, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList) {
        Map<IndexDo, BayanihanSymbolDo> beforeStickyWildIndex = null;
        CollectionInfo collectionInfo = null;
        while (!freeWheelList.isEmpty()) {
            if (Objects.isNull(collectionInfo)) {
                String multiStr = randomService.randomKeyByWeight(BayanihanProbabilisticUtil.buildWildProbabilisticMap(bayanihanConfig.getFreeGameConfig().getFreeGameInitialMultipleConfig()));
                BigDecimal multi = new BigDecimal(multiStr);
                collectionInfo = CollectionInfo.builder().initMultiple(multi).currentMultiple(multi).
                        currentRemainCollectSymbolCount(0).totalCollectSymbolCount(0)
                        .upgradeCost(bayanihanConfig.getFreeGameConfig().getMultipleUpgradeCost()).build();
            }
            FreeWheelHandleDo preFreeWheelHandleDo = freeWheelList.removeFirst();
            BayanihanGameRecordDo parentGameRecord = preFreeWheelHandleDo.getParentGameRecord();
            for (int i = Constants.CONST_0; i < preFreeWheelHandleDo.getTaskTimes(); i++) {
                // 免费轮处理
                WheelDo<BayanihanSymbolDo> firstWheel = this.generateFirstWheel(false, true, beforeStickyWildIndex);
                // 默认都是1倍
                collectionInfo.collectSymbol(BayanihanSymbolUtil.countNewSymbolInColumn(firstWheel.getSymbolArr(), COLLECT_SYMBOL_ID));
                RewardResultDo<BayanihanSymbolDo> rewardResult = bayanihanRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, firstWheel, null, totalRewardAmount.get()));
                AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
                // 组装-游戏记录
                long gameRecordId = generateGameRecordId();
                BayanihanGameRecordDo gameRecord = buildGameRecord(gameRecordId, parentGameRecord.getId(), gamePlay.getBetRecordId(), gameRecordId, firstWheel, rewardResult, collectionInfo);
                ret.add(gameRecord);
                parentGameRecord.addGameRecordId(gameRecordId);
                totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
                loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, true, collectionInfo);
                beforeStickyWildIndex = getStickyWildIndex(ret.get(ret.size() - 1).getWheel());
            }
        }
    }

    private Map<IndexDo, BayanihanSymbolDo> getStickyWildIndex(WheelDo<BayanihanSymbolDo> wheel) {
        BayanihanSymbolDo[][] symbolArr = wheel.getSymbolArr();
        Map<IndexDo, BayanihanSymbolDo> indexMap = new HashMap<>();
        for (int columNum = 0; columNum < GAME_INFO.getColumn(); columNum++) {
            // 填充
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                // 填充
                BayanihanSymbolDo symbolDo = symbolArr[rowNum][columNum];
                if (symbolDo.isWildSymbol()) {
                    IndexDo index = IndexDo.builder().rowIndex(rowNum).columnIndex(columNum).build();
                    indexMap.put(index, symbolDo.copy());
                }
            }
        }
        return indexMap;
    }

    /**
     * 生成一轮第一个wheel
     */
    private WheelDo<BayanihanSymbolDo> generateFirstWheel(boolean mustRewardFreeWheel,
                                                          boolean freeWheel,
                                                          Map<IndexDo, BayanihanSymbolDo> beforeStickyWildIndex) {
        BayanihanSymbolDo[][] symbolArr = new BayanihanSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];

        if (mustRewardFreeWheel && !freeWheel) {
            fillScatter(symbolArr);
        }
        // 填充上一轮的堆叠wild
        if (freeWheel) {
            if(Objects.nonNull(beforeStickyWildIndex)) {
                beforeStickyWildIndex.forEach((indexDo, symbolDo) -> {
                    symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = symbolDo;
                });
            }
        }

        //填充滚轮
        fillWheel(symbolArr, freeWheel);

        return WheelDo.<BayanihanSymbolDo>builder()
                .index(WHEEL_DEFAULT_INDEX)
                .freeFlag(freeWheel)
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 根据前一个wheel生成后续
     */
    private WheelDo<BayanihanSymbolDo> generateFromPreWheel(BayanihanGameRecordDo preGameRecord, boolean freeWheel) {

        WheelDo<BayanihanSymbolDo> preWheel = preGameRecord.getWheel();
        RewardResultDo<BayanihanSymbolDo> preRewardResult = preGameRecord.getRewardResult();
        // 复制：元素数组
        BayanihanSymbolDo[][] symbolArr = BayanihanSymbolUtil.copySymbolArr(preWheel.getSymbolArr());
        // 消除：中奖元素
        BayanihanSymbolUtil.clearRewardSymbol(symbolArr, preRewardResult);

        // 重新填充
        fillWheel(symbolArr, freeWheel);

        return WheelDo.<BayanihanSymbolDo>builder()
                .index(preWheel.getIndex() + 1)
                .freeFlag(freeWheel)
                .symbolArr(symbolArr)
                .build();
    }

    private void fillScatter(BayanihanSymbolDo[][] symbolArr) {
        // 在1、3、5列（索引0、2、4）中，每列随机选择一个行位置填充scatter
        for (Integer column : SCATTER_IN_COLUMN) {
            // 随机选择一个行索引 [0, row-1]
            int randomRow = randomService.randomInt(0, GAME_INFO.getRow() - 1);
            // 填充scatter元素
            symbolArr[randomRow][column] = BayanihanSymbolDo.buildFromSymbol(BayanihanGameHelper.BAYANIHAN_SCATTER);
        }
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
            int scatterNumberInColumn = BayanihanSymbolUtil.countScatterInColumn(symbolArr, column);
            TreeMap<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(column, playProbabilisticConfig);
            for (int row = ONE_ROW_INDEX; row < GAME_INFO.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    // 当前元素非空，则不生成
                    randomNumberIndex = randomNumberIndex + 1;
                    continue;
                }

                AssertUtil.assertTrue(MapUtils.isNotEmpty(columProbabilisticMap), ErrorCode.ERR_INTER);
                Integer randomNumber = randomNumberList.get(randomNumberIndex);
                SymbolDo symbolDo = null;
                if (scatterNumberInColumn >= MAX_SCATTER_IN_COLUMN || scatterNumberInWheel >= MAX_SCATTER_IN_WHEEL) {
                    // 列scatter达到上限 || 滚轮scatter达到上限，则生产非scatter元素
                    symbolDo = randomNonScatterSymbol(randomNumber, GAME_INFO, columProbabilisticMap);
                } else {
                    // 生成元素：随机生成
                    symbolDo = randomNormalSymbol(randomNumber, GAME_INFO, columProbabilisticMap);
                }
                AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER);
                //scatter 必须在135列
                if (symbolDo.isScatterSymbol()) {
                    AssertUtil.assertTrue(SCATTER_IN_COLUMN.contains(column), ErrorCode.ERR_INTER);
                }
                BayanihanSymbolDo res = BayanihanSymbolDo.buildFromSymbol(symbolDo);
                if (res.isWildSymbol()) {
                    res.setSymbolClearTimes(DEFAULT_STICKY_WILD_TIMES);
                }
                symbolArr[row][column] = res;
                if (symbolDo.isScatterSymbol()) {
                    scatterNumberInColumn = scatterNumberInColumn + 1;
                    scatterNumberInWheel = scatterNumberInWheel + 1;
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
                                         boolean freeWheel,
                                         CollectionInfo collectionInfo) {
        if (canContinuePlayOnPreWheel(gamePlay, preGameRecord.getRewardResult(), totalRewardAmount)) {
            WheelDo<BayanihanSymbolDo> wheel = generateFromPreWheel(preGameRecord, freeWheel);
            RewardResultDo<BayanihanSymbolDo> rewardResult = bayanihanRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, preGameRecord.getRewardResult().getTotalMultiple(), totalRewardAmount.get()));
            if (freeWheel && Objects.nonNull(collectionInfo)) {
                collectionInfo.collectSymbol(BayanihanSymbolUtil.countNewSymbolInColumn(wheel.getSymbolArr(), COLLECT_SYMBOL_ID));
            }
            long gameRecordId = generateGameRecordId();
            BayanihanGameRecordDo gameRecord = buildGameRecord(gameRecordId, preGameRecord.getId(), gamePlay.getBetRecordId(),
                    preGameRecord.getBatchId(), wheel, rewardResult, collectionInfo);
            ret.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, freeWheel, collectionInfo);
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

    private BayanihanGameRecordDo buildGameRecord(Long id,
                                               Long parentId,
                                               Long betRecordId,
                                               Long batchId,
                                               WheelDo<BayanihanSymbolDo> wheel,
                                               RewardResultDo<BayanihanSymbolDo> rewardResult, CollectionInfo collectionInfo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return BayanihanGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(batchId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .index(wheel.getIndex())
                .freeWheelFlag(wheel.isFreeFlag())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(null)
                .lastGameFlag(false)
                .collectionInfo(Objects.nonNull(collectionInfo) ? collectionInfo.snapshot() : null )
                .build();
    }

    /**
     * 用于记录收集信息
     */
    @Data
    @SuperBuilder
    private static class CollectionInfo {
        private BigDecimal initMultiple;

        private BigDecimal currentMultiple;

        private int upgradeCost;

        private int totalCollectSymbolCount;

        private int currentRemainCollectSymbolCount;

        /**
         * 收集x个元素
         * @param count
         */
        public void collectSymbol(int count) {
            totalCollectSymbolCount += count;
            currentRemainCollectSymbolCount += count;
            if (currentRemainCollectSymbolCount >= upgradeCost) {
                currentMultiple = currentMultiple.add(BigDecimal.valueOf(currentRemainCollectSymbolCount / upgradeCost));
                currentRemainCollectSymbolCount = currentRemainCollectSymbolCount % upgradeCost;
            }
        }

        public BayanihanGameRecordDo.CollectionInfo snapshot() {
            return BayanihanGameRecordDo.CollectionInfo.builder()
                    .initialMultiple(initMultiple)
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