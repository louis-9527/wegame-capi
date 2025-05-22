package com.wegame.capi.biz.module.lamang.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.*;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.module.lamang.LamangSymbolUtil;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
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

import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;
import static com.wegame.capi.biz.module.lamang.LamangGameHelper.LAMANG_GAME_INFO;
import static com.wegame.capi.biz.module.lamang.LamangSymbolUtil.countIndexSymbol;
import static com.wegame.capi.biz.module.malakas.MalakasProbabilisticUtil.getWheelProbabilisticConfig;
import static com.wegame.capi.biz.module.lamang.LamangSymbolUtil.*;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component("lamangGameNormalPlay")
public class LamangGameNormalPlay extends AbstractGame<LamangGameRecordDo> {

    private static final int MAX_SCATTER_NUMBER_IN_REEL = 1;
    private static final int MAX_SCATTER_NUMBER_IN_WHEEL = 3;
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = LAMANG_GAME_INFO;

    @Autowired
    MalakasConfig lamangConfig;

    @Autowired
    RewardHandler malakasRewardHandler;

    @Override
    public List<LamangGameRecordDo> play(GamePlayDo gamePlay) {
        //todo 逻辑代码
        List<LamangGameRecordDo> gameRecordResult = new ArrayList<>();
        LinkedList<FreeWheelHandleDo> freeWheelList = new LinkedList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        generateFirstBatch();

        if (CollectionUtils.isNotEmpty(freeWheelList)) {
            generateFreeBatch(freeWheelList);
        }
        return gameRecordResult;
    }

    /**
     * 填充滚轮
     */
    private void fillWheel(GameInfoDo gameInfo,
                           LamangSymbolDo[][] symbolArr,
                           int totalScatterNumberInWheel,
                           MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig) {

        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        int scatterNumberInWheel = totalScatterNumberInWheel;
        int randomNumberIndex = 0;
        TreeMap<String, Integer> oneRowColumProbabilisticMap = buildColumProbabilisticMap(SEVEN_COLUM_INDEX, playProbabilisticConfig.getWheelProbabilisticConfig());
        for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
            // 一个reel只能包含一个reel
            int scatterNumberInColumn = countIndexSymbol(symbolArr, SymbolEnum.Scatter, column);
            TreeMap<String, Integer> columProbabilisticMap;
            TreeMap<String, Integer> nonOneRowColumProbabilisticMap = buildColumProbabilisticMap(column, playProbabilisticConfig.getWheelProbabilisticConfig());
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    // 当前元素非空，则不生成
                    randomNumberIndex = randomNumberIndex + 1;
                    continue;
                }
                if (Objects.equals(row, ONE_ROW_INDEX)) {
                    columProbabilisticMap = oneRowColumProbabilisticMap;
                } else {
                    columProbabilisticMap = nonOneRowColumProbabilisticMap;
                }
                AssertUtil.assertTrue(MapUtils.isNotEmpty(columProbabilisticMap), ErrorCode.ERR_INTER);
                Integer randomNumber = randomNumberList.get(randomNumberIndex);
                if (Objects.equals(row, ONE_ROW_INDEX) || scatterNumberInColumn >= MAX_SCATTER_NUMBER_IN_REEL || scatterNumberInWheel >= MAX_SCATTER_NUMBER_IN_WHEEL) {
                    // 第一行 || 列scatter达到上限 || 滚轮scatter达到上限，则生产非scatter元素
                    // 生成元素：非scatter元素
                    SymbolDo symbolDo = randomNonScatterSymbol(randomNumber, gameInfo, columProbabilisticMap);

                    // 构建元素金边
                    boolean symbolGoldBorder = generateSymbolGoldBorder(playProbabilisticConfig, row, column);
                    LamangSymbolDo malakasSymbolDo = LamangSymbolDo.buildFromSymbol(symbolDo, symbolGoldBorder);
                    symbolArr[row][column] = malakasSymbolDo;
                } else {
                    // 生成元素：随机生成
                    SymbolDo symbolDo = randomNormalSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    // 构建元素金边
                    boolean symbolGoldBorder = generateSymbolGoldBorder(playProbabilisticConfig, row, column);
                    LamangSymbolDo malakasSymbolDo = LamangSymbolDo.buildFromSymbol(symbolDo, symbolGoldBorder);
                    symbolArr[row][column] = malakasSymbolDo;
                    if (symbolDo.isScatterSymbol()) {
                        scatterNumberInColumn = scatterNumberInColumn + 1;
                        scatterNumberInWheel = scatterNumberInWheel + 1;
                    }
                }
                randomNumberIndex = randomNumberIndex + 1;
            }
        }
    }

    private WheelDo<LamangSymbolDo> generateFirstWheel(MalakasConfig.PlayProbabilisticConfig config){
        LamangSymbolDo[][] symbolArr = new LamangSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];

        fillWheel(GAME_INFO, symbolArr, 0, config);

        return WheelDo.<LamangSymbolDo>builder()
                .index(WHEEL_DEFAULT_INDEX)
                .freeFlag(false)
                .symbolArr(symbolArr)
                .build();
    }

    private WheelDo<LamangSymbolDo> generateFromPreWheel(LamangGameRecordDo preGameRecord, MalakasConfig.PlayProbabilisticConfig config){

        WheelDo<LamangSymbolDo> preWheel = preGameRecord.getWheel();
        RewardResultDo<LamangSymbolDo> preRewardResult = preGameRecord.getRewardResult();
        // 复制：元素数组
        LamangSymbolDo[][] symbolArr = copySymbolArr(preWheel.getSymbolArr());
        // 消除：中奖元素
        clearRewardSymbol(symbolArr, preRewardResult);
        // 下落
        rebuildSymbolArr(symbolArr, GAME_INFO);

        int scatterNumberInWheel = LamangSymbolUtil.countIndexSymbol(symbolArr, SymbolEnum.Scatter);

        // 填补剩余元素
        fillWheel(GAME_INFO, symbolArr, 0, config);

        return WheelDo.<LamangSymbolDo>builder()
                .index(WHEEL_DEFAULT_INDEX)
                .freeFlag(false)
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 根据前一条游戏记录
     */
    private void loopPlayOnPreGameRecord(GamePlayDo gamePlay,
                                         List<LamangGameRecordDo> gameRecordResult,
                                         AtomicLong totalRewardAmount,
                                         LamangGameRecordDo preGameRecord,
                                         LinkedList<FreeWheelHandleDo> freeWheelList,
                                         MalakasConfig.PlayProbabilisticConfig config) {

        if (canContinuePlayOnPreWheel(gamePlay, preGameRecord.getRewardResult(), totalRewardAmount)) {
            WheelDo<LamangSymbolDo> wheel = generateFromPreWheel(
                    preGameRecord,
                    config);
            //todo 换成lamang中奖线计算
            RewardResultDo<LamangSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, preGameRecord.getRewardResult().getTotalMultiple()));
            long gameRecordId = generateGameRecordId();
            LamangGameRecordDo gameRecord = buildGameRecord(gameRecordId, preGameRecord.getId(), gamePlay.getBetRecordId(), preGameRecord.getBatchId(), wheel, rewardResult);
            gameRecordResult.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            loopPlayOnPreGameRecord(gamePlay, gameRecordResult, totalRewardAmount, gameRecord, freeWheelList, config);
        } else if (preGameRecord.getRewardResult().isHasRewardFreeWheel()) {
            if (!reachMaxMultiple(totalRewardAmount.get(), gamePlay.getBetAmount(), lamangConfig.getRewardMaxMultiple())) {
                for (int times = 1; times <= preGameRecord.getRewardResult().getRewardFreeWheelTimes(); times++) {
                    freeWheelList.addLast(FreeWheelHandleDo.builder()
                            .parentGameRecordId(preGameRecord.getId())
                            .build());
                }
            } else {
                // help gc
                freeWheelList.clear();
            }
        }
    }

    private LamangGameRecordDo buildGameRecord(Long id,
                                                Long parentId,
                                                Long betRecordId,
                                                Long batchId,
                                                WheelDo<LamangSymbolDo> wheel,
                                                RewardResultDo<LamangSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return LamangGameRecordDo.builder()
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
                .build();
    }

    /**
     * 生成一轮
     */
    private void generateBatch(MalakasConfig.PlayProbabilisticConfig config) {
        WheelDo<LamangSymbolDo> wheel = generateFirstWheel(config);
        //todo
//        loopPlayOnPreGameRecord();
    }

    /**
     * 生成首轮
     */
    private void generateFirstBatch() {
        //todo
//        generateBatch();
    }

    /**
     * 生成免费轮
     */
    private void generateFreeBatch(LinkedList<FreeWheelHandleDo> freeWheelList) {
        while (!freeWheelList.isEmpty()) {
            FreeWheelHandleDo freeWheel = freeWheelList.removeFirst();
//            generateBatch();
        }
    }

    /**
     * 是否能继续游戏
     * @param gamePlay
     * @param rewardResult
     * @param totalRewardAmount
     * @return
     */
    private boolean canContinuePlayOnPreWheel(GamePlayDo gamePlay, RewardResultDo<LamangSymbolDo> rewardResult, AtomicLong totalRewardAmount) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return false;
        }
        if (reachMaxMultiple(totalRewardAmount.get(), gamePlay.getBetAmount(), lamangConfig.getRewardMaxMultiple())) {
            log.warn("reach max reward multiple bet. totalRewardAmount: {}, payAmount: {}", totalRewardAmount.get(), gamePlay.getPayAmount());
            return false;
        }
        return CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList());
    }



    private boolean generateSymbolGoldBorder(MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig, int row, int column) {
        List<BigDecimal> goldBoarderProbabilisticConfig = playProbabilisticConfig.getGoldBoarderProbabilisticConfig();
        BigDecimal goldBoarderProbabilistic = null;
        if (Objects.equals(row, ONE_ROW_INDEX)) {
            goldBoarderProbabilistic = goldBoarderProbabilisticConfig.get(SEVEN_COLUM_INDEX);
        } else {
            goldBoarderProbabilistic = goldBoarderProbabilisticConfig.get(column);
        }
        AssertUtil.assertNotNull(goldBoarderProbabilistic, ErrorCode.ERR_INTER);
        int probabilisticVal = goldBoarderProbabilistic.multiply(new BigDecimal(100)).intValue();
        if (Objects.equals(probabilisticVal, 0)) {
            return false;
        } else {
            int randomVal = randomService.randomInt(1, CONST_10000);
            return randomVal > 0 && randomVal <= probabilisticVal;
        }
    }


    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        private long parentGameRecordId;
    }
}