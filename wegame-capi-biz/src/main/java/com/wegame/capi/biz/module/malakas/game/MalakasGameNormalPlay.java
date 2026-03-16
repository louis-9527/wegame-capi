package com.wegame.capi.biz.module.malakas.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.malakas.MalakasProbabilisticUtil;
import com.wegame.capi.biz.module.malakas.MalakasSymbolUtil;
import com.wegame.capi.biz.module.malakas.reward.MalakasRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import com.wegame.capi.common.utils.TtlUtil;
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

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;
import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_SYMBOL_PLACEHOLDER;
import static com.wegame.capi.biz.module.malakas.MalakasProbabilisticUtil.getWheelProbabilisticConfig;
import static com.wegame.capi.biz.module.malakas.MalakasSymbolUtil.*;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component("malakasGameNormalPlay")
public class MalakasGameNormalPlay extends AbstractGame<MalakasGameRecordDo> {

    private static final int MAX_SCATTER_NUMBER_IN_REEL = 2;
    private static final int MAX_SCATTER_NUMBER_IN_WHEEL = 6;
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = MALAKAS_GAME_INFO;

    @Autowired
    MalakasConfig malakasConfig;
    @Autowired
    MalakasRewardHandler malakasRewardHandler;

    @Override
    public List<MalakasGameRecordDo> play(BetDo betDo, GamePlayDo gamePlay) {
        List<MalakasGameRecordDo> ret = new ArrayList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // 生成-游戏滚轮
        WheelDo<MalakasSymbolDo> wheel;
        long gameRecordId = generateGameRecordId();
        switch (gamePlay.getBetType()) {
            // 正常投注
            case NORMAL_BET:
                wheel = generateNormalWheel(getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), false), false);
                break;
            // 购买投注
            case BUY_BET:
                wheel = generateRewardFreeWheel(getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), false), false);
                break;
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }
        // 判奖-根据游戏滚轮和中奖线算法判断
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, BigDecimal.ZERO, totalRewardAmount.get()));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 组装-游戏记录
        MalakasGameRecordDo gameRecord = buildGameRecord(gameRecordId, RECORD_DEFAULT_PARENT_ID, gamePlay.getBetRecordId(), gameRecordId, wheel, rewardResult);
        ret.add(gameRecord);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        // loop play
        LinkedList<FreeWheelHandleDo> freeWheelList = new LinkedList<>();
        loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, false);
        if (CollectionUtils.isNotEmpty(freeWheelList)) {
            handleFreeWheelGamePlay(gamePlay, ret, totalRewardAmount, freeWheelList);
        }
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }
        // 占位元素-将占位元素设置为空:
        postProcessPlaceholderSymbol(ret);
        return ret;
    }

    public void loopPlayOnPreGameRecord(GamePlayDo gamePlay,
                                        List<MalakasGameRecordDo> ret,
                                        AtomicLong totalRewardAmount,
                                        MalakasGameRecordDo preGameRecord,
                                        LinkedList<FreeWheelHandleDo> freeWheelList,
                                        boolean freeWheelFlag) {
        if (canContinuePlayOnPreWheel(gamePlay, preGameRecord.getRewardResult(), totalRewardAmount)) {
            WheelDo<MalakasSymbolDo> wheel = generateNormalWheelFromPreWheel(
                    getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), freeWheelFlag),
                    preGameRecord);
            RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, preGameRecord.getRewardResult().getTotalMultiple(), totalRewardAmount.get()));
            long gameRecordId = generateGameRecordId();
            MalakasGameRecordDo gameRecord = buildGameRecord(gameRecordId, preGameRecord.getId(), gamePlay.getBetRecordId(), preGameRecord.getBatchId(), wheel, rewardResult);
            ret.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, freeWheelFlag);
        } else {
            if (!preGameRecord.getRewardResult().getAccumulateReachMaxMultiple()) {
                if (preGameRecord.getRewardResult().isHasRewardFreeWheel()) {
                    for (int times = 1; times <= preGameRecord.getRewardResult().getRewardFreeWheelTimes(); times++) {
                        freeWheelList.addLast(FreeWheelHandleDo.builder()
                                .parentGameRecordId(preGameRecord.getId())
                                .build());
                    }
                }
            } else {
                // help gc
                freeWheelList.clear();
            }
        }
    }

    private void handleFreeWheelGamePlay(GamePlayDo gamePlay,
                                         List<MalakasGameRecordDo> ret,
                                         AtomicLong totalRewardAmount,
                                         LinkedList<FreeWheelHandleDo> freeWheelList) {
        BigDecimal totalMultipleAmount = new BigDecimal("0");
        while (!freeWheelList.isEmpty()) {
            FreeWheelHandleDo freeWheel = freeWheelList.removeFirst();
            MalakasGameRecordDo parentGameRecord = ret.stream().filter(item -> Objects.equals(freeWheel.getParentGameRecordId(), item.getId())).findFirst().orElse(null);
            AssertUtil.assertNotNull(parentGameRecord, ErrorCode.ERR_INTER);
            WheelDo<MalakasSymbolDo> wheel = generateNormalWheel(getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), true), true);
            wheel.setFreeFlag(true);
            // 判奖-根据游戏滚轮和中奖线算法判断
            RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, totalMultipleAmount, totalRewardAmount.get()));
            AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
            // 组装-游戏记录
            long gameRecordId = generateGameRecordId();
            MalakasGameRecordDo gameRecord = buildGameRecord(gameRecordId, freeWheel.getParentGameRecordId(), gamePlay.getBetRecordId(), gameRecordId, wheel, rewardResult);
            ret.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            if (CollectionUtils.isEmpty(parentGameRecord.getGameRecordIds())) {
                parentGameRecord.setGameRecordIds(new ArrayList<>());
            }
            parentGameRecord.getGameRecordIds().add(gameRecordId);
            // loop play
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, true);
            if (CollectionUtils.isNotEmpty(ret)) {
                totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
            }
        }
    }

    private WheelDo<MalakasSymbolDo> generateNormalWheelFromPreWheel(MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig,
                                                                     MalakasGameRecordDo preGameRecord) {
        WheelDo<MalakasSymbolDo> preWheel = preGameRecord.getWheel();
        RewardResultDo<MalakasSymbolDo> preRewardResult = preGameRecord.getRewardResult();
        // 复制：元素数组
        MalakasSymbolDo[][] symbolArr = copySymbolArr(preWheel.getSymbolArr());
        // 消除：中奖元素
        clearRewardSymbol(symbolArr, preRewardResult);
        // 重建：元素数组（非空元素按列下落处理：第1-6Reel非空元素向下移动，第7Reel非空元素向左移动）
        rebuildSymbolArr(symbolArr, GAME_INFO);
        // 生产：缺失元素(填补剩余元素)
        int scatterNumberInWheel = countIndexSymbol(symbolArr, SymbolEnum.Scatter);
        return generateCommonNormalWheel(GAME_INFO,
                symbolArr,
                scatterNumberInWheel,
                playProbabilisticConfig,
                preWheel.getIndex() + 1,
                preGameRecord.getFreeWheelFlag());
    }

    /**
     * 生成正常滚轮：生成逻辑无任何限制
     *
     * @return 滚轮
     */
    private WheelDo<MalakasSymbolDo> generateNormalWheel(MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig,
                                                         boolean freeWheelFlag) {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];
        // 第一行（第一列 && 最后一列）： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][ONE_COLUM_INDEX] = MALAKAS_SYMBOL_PLACEHOLDER;
        symbolArr[ONE_ROW_INDEX][GAME_INFO.getColumn() - 1] = MALAKAS_SYMBOL_PLACEHOLDER;
        return generateCommonNormalWheel(GAME_INFO,
                symbolArr,
                0,
                playProbabilisticConfig,
                WHEEL_DEFAULT_INDEX,
                freeWheelFlag);
    }

    /**
     * 生成中奖免费轮滚轮: 需要满足4-6个Scatter
     * @return 滚轮
     */
    private WheelDo<MalakasSymbolDo> generateRewardFreeWheel(MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig,
                                                             boolean freeWheelFlag) {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];
        // 第一行（第一列 && 最后一列）： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][ONE_COLUM_INDEX] = MALAKAS_SYMBOL_PLACEHOLDER;
        symbolArr[ONE_ROW_INDEX][GAME_INFO.getColumn() - 1] = MALAKAS_SYMBOL_PLACEHOLDER;
        WheelDo<MalakasSymbolDo> wheel = generateCommonNormalWheel(GAME_INFO, symbolArr, 0, playProbabilisticConfig, WHEEL_DEFAULT_INDEX, freeWheelFlag);
        int scatterSymbolCount = countIndexSymbol(wheel.getSymbolArr(), SymbolEnum.Scatter);
        int randomScatterSymbolCount = randomService.randomInt(4, 6);
        int needAddCount = randomScatterSymbolCount - scatterSymbolCount;
        if (needAddCount <= 0) {
            return wheel;
        }

        // 提取有效坐标集合
        List<IndexDo> indexList = new ArrayList<>();
        for (int row = 1; row < GAME_INFO.getRow(); row++) {
            for (int column = 0; column < GAME_INFO.getColumn(); column++) {
                MalakasSymbolDo symbol = wheel.getSymbolArr()[row][column];
                if (Objects.nonNull(symbol)) {
                    if (!symbol.isPlaceholderSymbol() && !symbol.isScatterSymbol()) {
                        indexList.add(IndexFactory.buildDo(row, column));
                    }
                }
            }
        }

        int tempCount = 0;
        while (tempCount < needAddCount) {
            int randomIndex = randomService.randomInt(0, indexList.size() - 1);
            IndexDo indexDo = indexList.get(randomIndex);
            int row = indexDo.getRowIndex();
            int column = indexDo.getColumnIndex();
            AssertUtil.assertNotNull(wheel.getSymbolArr()[row][column], ErrorCode.ERR_INTER);
            MalakasSymbolDo symbol = wheel.getSymbolArr()[row][column];
            if (symbol.isPlaceholderSymbol() || symbol.isScatterSymbol()) {
                // 为占位元素或者scatter元素，则继续
                // do nothing;
            } else {
                int columnScatterNum = countIndexSymbol(wheel.getSymbolArr(), SymbolEnum.Scatter, column);
                if (columnScatterNum >= MAX_SCATTER_NUMBER_IN_REEL) {
                    continue;
                }
                symbol.setId(SymbolEnum.Scatter.getId());
                symbol.setName(SymbolEnum.Scatter.getName());
                symbol.setSymbolType(SymbolTypeEnum.SCATTER);
                tempCount = tempCount + 1;
            }
        }
        return wheel;
    }

    private WheelDo<MalakasSymbolDo> generateCommonNormalWheel(GameInfoDo gameInfo,
                                                               MalakasSymbolDo[][] symbolArr,
                                                               int totalScatterNumberInWheel,
                                                               MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig,
                                                               int wheelIndex,
                                                               boolean freeFlag) {
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        int scatterNumberInWheel = totalScatterNumberInWheel;
        int randomNumberIndex = 0;
        TreeMap<String, Integer> oneRowColumProbabilisticMap = buildColumProbabilisticMap(SEVEN_COLUM_INDEX, playProbabilisticConfig.getWheelProbabilisticConfig());
        for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
            // 一列只能包含两个scatter
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
                    SymbolSizeEnum symbolSize = generateSymbolSize(row, MalakasSymbolUtil.getMaxRow(symbolArr, row, column), column, gameInfo, playProbabilisticConfig);
                    MalakasSymbolUtil.handlePlaceholderSymbol(symbolArr, row, column, symbolSize);
                    // 构建元素金边
                    boolean symbolGoldBorder = generateSymbolGoldBorder(playProbabilisticConfig, row, column);
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbol(symbolDo, symbolSize, symbolGoldBorder);
                    symbolArr[row][column] = malakasSymbolDo;
                } else {
                    // 生成元素：随机生成
                    SymbolDo symbolDo = randomNormalSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(row, MalakasSymbolUtil.getMaxRow(symbolArr, row, column), column, gameInfo, playProbabilisticConfig);
                    MalakasSymbolUtil.handlePlaceholderSymbol(symbolArr, row, column, symbolSize);
                    // 构建元素金边
                    boolean symbolGoldBorder = generateSymbolGoldBorder(playProbabilisticConfig, row, column);
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbol(symbolDo, symbolSize, symbolGoldBorder);
                    symbolArr[row][column] = malakasSymbolDo;
                    if (symbolDo.isScatterSymbol()) {
                        scatterNumberInColumn = scatterNumberInColumn + 1;
                        scatterNumberInWheel = scatterNumberInWheel + 1;
                    }
                }
                randomNumberIndex = randomNumberIndex + 1;
            }
        }
        // 返回：滚轮
        return WheelDo.<MalakasSymbolDo>builder()
                .index(wheelIndex)
                .freeFlag(freeFlag)
                .symbolArr(symbolArr)
                .build();
    }

    private boolean canContinuePlayOnPreWheel(GamePlayDo gamePlay, RewardResultDo<MalakasSymbolDo> rewardResult, AtomicLong totalRewardAmount) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return false;
        }
        if (rewardResult.getAccumulateReachMaxMultiple()) {
            log.warn("reach max reward multiple bet. totalRewardAmount: {}, payAmount: {}", totalRewardAmount.get(), gamePlay.getPayAmount());
            return false;
        }
        return CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList());
    }

    private SymbolSizeEnum generateSymbolSize(int currentRow, int maxRow, int currentColum, GameInfoDo gameInfo, MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig) {
        if (Objects.equals(currentRow, ONE_ROW_INDEX)
                || Objects.equals(currentColum, ONE_COLUM_INDEX)
                || Objects.equals(currentColum, gameInfo.getColumn() - 1)) {
            // 第一行，第一列，最后一列：只能为小元素
            return SymbolSizeEnum.S;
        }
        Map<String, Integer> map = MalakasProbabilisticUtil.buildColumSymboleSizeProbabilisticMap(currentColum, maxRow - currentRow + 1, playProbabilisticConfig);
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        return randomSymbolSize(map, randomService.randomInt(1, map.values()
                .stream()
                .reduce(0, Integer::sum)));
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

    private MalakasGameRecordDo buildGameRecord(Long id,
                                                Long parentId,
                                                Long betRecordId,
                                                Long batchId,
                                                WheelDo<MalakasSymbolDo> wheel,
                                                RewardResultDo<MalakasSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return MalakasGameRecordDo.builder()
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
                .creator(userInfo.getPlayerId())
                .createTime(DateUtil.getCurrentMilli())
                .updater(userInfo.getPlayerId())
                .updateTime(DateUtil.getCurrentMilli())
                .expireAt(TtlUtil.calculateExpiredAt(DateUtil.getCurrentMilli()))
                .build();
    }

    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        private long parentGameRecordId;
    }
}