package com.wegame.capi.biz.module.malakas.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.module.malakas.MalakasProbabilisticUtil;
import com.wegame.capi.biz.module.malakas.MalakasSymbolUtil;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.wegame.capi.biz.module.base.AmountUtil.reachMaxMultiple;
import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;
import static com.wegame.capi.biz.module.malakas.MalakasProbabilisticUtil.getWheelProbabilisticConfig;
import static com.wegame.capi.biz.module.malakas.MalakasSymbolUtil.*;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
@Component
public class MalakasGame extends AbstractGame<MalakasGameRecordDo> {

    private static final int MAX_SCATTER_NUMBER_IN_REEL = 2;
    private static final int MAX_SCATTER_NUMBER_IN_WHEEL = 6;
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;

    @Autowired
    MalakasConfig malakasConfig;
    @Autowired
    RewardHandler malakasRewardHandler;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Override
    public GameInfoDo getGameInfo() {
        return MALAKAS_GAME_INFO;
    }

    @Override
    public List<MalakasGameRecordDo> play(GamePlayDo gamePlay) {
        List<MalakasGameRecordDo> ret = new ArrayList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // 生成-游戏滚轮
        WheelDo<MalakasSymbolDo> wheel;
        long gameRecordId = generateGameRecordId();
        MalakasSymbolDo placeholderSymbol = getPlaceholderSymbol();
        switch (gamePlay.getBetType()) {
            // 正常投注
            case NORMAL_BET:
                wheel = generateNormalWheel(getGameInfo(), getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), false), placeholderSymbol, false);
                break;
            // 购买投注
            case BUY_BET:
                wheel = generateRewardFreeWheel(getGameInfo(), getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), false), placeholderSymbol, false);
                break;
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }
        // 判奖-根据游戏滚轮和中奖线算法判断
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(getGameInfo(), gamePlay, wheel, BigDecimal.ONE));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 组装-游戏记录
        MalakasGameRecordDo gameRecord = buildGameRecord(gameRecordId, RECORD_DEFAULT_PARENT_ID, gamePlay.getBetRecordId(), gameRecordId, wheel, rewardResult);
        ret.add(gameRecord);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        // loop play
        LinkedList<FreeWheelHandleDo> freeWheelList = new LinkedList<>();
        loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, placeholderSymbol);
        if (CollectionUtils.isNotEmpty(freeWheelList)) {
            handleFreeWheelGamePlay(gamePlay, ret, totalRewardAmount, freeWheelList, placeholderSymbol);
        }
        // 占位元素-将占位元素设置为空:
        placeholderSymbol = null;
        // postPlaceholderSymbol(ret);
        // 返回-游戏记录列表
        return ret;
    }

    public void loopPlayOnPreGameRecord(GamePlayDo gamePlay,
                                        List<MalakasGameRecordDo> ret,
                                        AtomicLong totalRewardAmount,
                                        MalakasGameRecordDo preGameRecord,
                                        LinkedList<FreeWheelHandleDo> freeWheelList,
                                        MalakasSymbolDo placeholderSymbol) {
        if (canContinuePlayOnPreWheel(gamePlay, preGameRecord.getRewardResult(), totalRewardAmount)) {
            WheelDo<MalakasSymbolDo> wheel = generateNormalWheelFromPreWheel(
                    getGameInfo(),
                    getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), false),
                    preGameRecord,
                    placeholderSymbol);
            RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(getGameInfo(), gamePlay, wheel, preGameRecord.getRewardResult().getTotalMultiple()));
            long gameRecordId = generateGameRecordId();
            MalakasGameRecordDo gameRecord = buildGameRecord(gameRecordId, preGameRecord.getId(), gamePlay.getBetRecordId(), preGameRecord.getBatchId(), wheel, rewardResult);
            ret.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, placeholderSymbol);
        } else if (preGameRecord.getRewardResult().isHasRewardFreeWheel()) {
            if (!reachMaxMultiple(totalRewardAmount.get(), gamePlay.getPayAmount(), malakasConfig.getRewardMaxMultiple())) {
                for (int times = 1; times <= preGameRecord.getRewardResult().getRewardFreeWheelTimes(); times++) {
                    freeWheelList.addLast(FreeWheelHandleDo.builder()
                            .parentGameRecordId(preGameRecord.getId())
                            .build());
                }
                //                Optional.ofNullable(preGameRecord.getGameRecordIds())
                //                        .orElse(Collections.emptyList())
                //                        .forEach(gameRecordId -> freeWheelList.addLast(FreeWheelHandleDo.builder()
                //                                .gameRecordId(gameRecordId)
                //                                .parentGameRecordId(preGameRecord.getId())
                //                                .build()));
            } else {
                // help gc
                freeWheelList.clear();
                // preGameRecord.setGameRecordIds(null);
            }
        }
    }

    private void handleFreeWheelGamePlay(GamePlayDo gamePlay,
                                         List<MalakasGameRecordDo> ret,
                                         AtomicLong totalRewardAmount,
                                         LinkedList<FreeWheelHandleDo> freeWheelList,
                                         MalakasSymbolDo placeholderSymbol) {
        BigDecimal totalMultipleAmount = new BigDecimal("1");
        while (!freeWheelList.isEmpty()) {
            FreeWheelHandleDo freeWheel = freeWheelList.removeFirst();
            MalakasGameRecordDo parentGameRecord = ret.stream().filter(item -> Objects.equals(freeWheel.getParentGameRecordId(), item.getId())).findFirst().orElse(null);
            AssertUtil.assertNotNull(parentGameRecord, ErrorCode.ERR_INTER);
            WheelDo<MalakasSymbolDo> wheel = generateNormalWheel(getGameInfo(), getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), true), placeholderSymbol, true);
            wheel.setFreeFlag(true);
            // 判奖-根据游戏滚轮和中奖线算法判断
            RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(getGameInfo(), gamePlay, wheel, totalMultipleAmount));
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
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, placeholderSymbol);
            if (CollectionUtils.isNotEmpty(ret)) {
                totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
            }
        }
    }

    private WheelDo<MalakasSymbolDo> generateNormalWheelFromPreWheel(GameInfoDo gameInfo,
                                                                     MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig,
                                                                     MalakasGameRecordDo preGameRecord,
                                                                     MalakasSymbolDo placeholderSymbol) {
        WheelDo<MalakasSymbolDo> preWheel = preGameRecord.getWheel();
        RewardResultDo<MalakasSymbolDo> preRewardResult = preGameRecord.getRewardResult();
        // 复制：元素数组
        MalakasSymbolDo[][] symbolArr = copySymbolArr(preWheel.getSymbolArr(), placeholderSymbol);
        // 消除：中奖元素
        clearRewardSymbol(symbolArr, preRewardResult);
        MalakasSymbolUtil.print(symbolArr, "clear after");
        // 重建：元素数组（非空元素按列下落处理：第1-6Reel非空元素向下移动，第7Reel非空元素向左移动）
        rebuildSymbolArr(symbolArr, gameInfo);
        // 生产：缺失元素(填补剩余元素)
        int scatterNumberInWheel = countIndexSymbol(symbolArr, SymbolEnum.Scatter);
        return generateCommonNormalWheel(gameInfo,
                symbolArr,
                scatterNumberInWheel,
                playProbabilisticConfig,
                preWheel.getIndex() + 1,
                preGameRecord.getFreeWheelFlag(),
                placeholderSymbol);
    }

    /**
     * 生成正常滚轮：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<MalakasSymbolDo> generateNormalWheel(GameInfoDo gameInfo,
                                                         MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig,
                                                         MalakasSymbolDo placeholderSymbol,
                                                         boolean freeWheelFlag) {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 第一行（第一列 && 最后一列）： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][ONE_COLUM_INDEX] = placeholderSymbol;
        symbolArr[ONE_ROW_INDEX][gameInfo.getColumn() - 1] = placeholderSymbol;
        return generateCommonNormalWheel(gameInfo,
                symbolArr,
                0,
                playProbabilisticConfig,
                WHEEL_DEFAULT_INDEX,
                freeWheelFlag,
                placeholderSymbol);
    }

    /**
     * 生成中奖免费轮滚轮: 需要满足4-6个Scatter
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<MalakasSymbolDo> generateRewardFreeWheel(GameInfoDo gameInfo,
                                                             MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig,
                                                             MalakasSymbolDo placeholderSymbol,
                                                             boolean freeWheelFlag) {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 第一行（第一列 && 最后一列）： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][ONE_COLUM_INDEX] = placeholderSymbol;
        symbolArr[ONE_ROW_INDEX][gameInfo.getColumn() - 1] = placeholderSymbol;
        WheelDo<MalakasSymbolDo> wheel = generateCommonNormalWheel(gameInfo, symbolArr, 0, playProbabilisticConfig, WHEEL_DEFAULT_INDEX, freeWheelFlag, placeholderSymbol);
        int scatterSymbolCount = countIndexSymbol(wheel.getSymbolArr(), SymbolEnum.Scatter);
        int randomScatterSymbolCount = randomService.randomInt(4, 6);
        int needAddCount = randomScatterSymbolCount - scatterSymbolCount;
        if (needAddCount <= 0) {
            return wheel;
        }
        int tempCount = 0;
        while (tempCount < needAddCount) {
            // 注意：第一行不能出现scatter
            int row = randomService.randomInt(1, gameInfo.getRow() - 1);
            int column = randomService.randomInt(0, gameInfo.getColumn() - 1);
            AssertUtil.assertNotNull(wheel.getSymbolArr()[row][column], ErrorCode.ERR_INTER);
            if (StringUtils.equals(wheel.getSymbolArr()[row][column].getId(), placeholderSymbol.getId())
                    || StringUtils.equals(wheel.getSymbolArr()[row][column].getId(), SymbolEnum.Scatter.getId())) {
                // 为占位元素或者scatter元素，则继续
                // do nothing;
            } else {
                wheel.getSymbolArr()[row][column].setId(SymbolEnum.Scatter.getId());
                wheel.getSymbolArr()[row][column].setName(SymbolEnum.Scatter.getName());
                wheel.getSymbolArr()[row][column].setSymbolType(SymbolTypeEnum.SCATTER);
                tempCount++;
            }
        }
        return wheel;
    }

    private WheelDo<MalakasSymbolDo> generateCommonNormalWheel(GameInfoDo gameInfo,
                                                               MalakasSymbolDo[][] symbolArr,
                                                               int totalScatterNumberInWheel,
                                                               MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig,
                                                               int wheelIndex,
                                                               boolean freeFlag,
                                                               MalakasSymbolDo placeholderSymbol) {
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        int scatterNumberInWheel = totalScatterNumberInWheel;
        int randomNumberIndex = 0;
        for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
            // 一列只能包含两个scatter
            int scatterNumberInColumn = countIndexSymbol(symbolArr, SymbolEnum.Scatter, column);
            TreeMap<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(column, playProbabilisticConfig.getWheelProbabilisticConfig());
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    // 当前元素非空，则不生成
                    continue;
                }
                Integer randomNumber = randomNumberList.get(randomNumberIndex);
                if (Objects.equals(row, ONE_ROW_INDEX) || scatterNumberInColumn >= MAX_SCATTER_NUMBER_IN_REEL || scatterNumberInWheel >= MAX_SCATTER_NUMBER_IN_WHEEL) {
                    // 第一行 || 列scatter达到上限 || 滚轮scatter达到上限，则生产非scatter元素
                    // 生成元素：非scatter元素
                    SymbolDo symbolDo = randomNonScatterSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(row, MalakasSymbolUtil.getMaxRow(symbolArr, row, column), column, gameInfo, playProbabilisticConfig);
                    MalakasSymbolUtil.handlePlaceholderSymbol(symbolArr, row, column, symbolSize, placeholderSymbol);
                    // 构建元素金边
                    boolean symbolGoldBorder = generateSymbolGoldBorder(gameInfo, playProbabilisticConfig, row, column);
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbol(symbolDo, symbolSize, symbolGoldBorder);
                    symbolArr[row][column] = malakasSymbolDo;
                } else {
                    // 生成元素：随机生成
                    SymbolDo symbolDo = randomNormalSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(row, MalakasSymbolUtil.getMaxRow(symbolArr, row, column), column, gameInfo, playProbabilisticConfig);
                    MalakasSymbolUtil.handlePlaceholderSymbol(symbolArr, row, column, symbolSize, placeholderSymbol);
                    // 构建元素金边
                    boolean symbolGoldBorder = generateSymbolGoldBorder(gameInfo, playProbabilisticConfig, row, column);
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbol(symbolDo, symbolSize, symbolGoldBorder);
                    symbolArr[row][column] = malakasSymbolDo;
                    if (symbolDo.isScatterSymbol()) {
                        scatterNumberInColumn++;
                        scatterNumberInWheel++;
                    }
                }
                randomNumberIndex++;
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
        if (reachMaxMultiple(totalRewardAmount.get(), gamePlay.getPayAmount(), malakasConfig.getRewardMaxMultiple())) {
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

    private boolean generateSymbolGoldBorder(GameInfoDo gameInfo, MalakasConfig.PlayProbabilisticConfig playProbabilisticConfig, int row, int column) {
        List<BigDecimal> goldBoarderProbabilisticConfig = playProbabilisticConfig.getGoldBoarderProbabilisticConfig();
        BigDecimal goldBoarderProbabilistic = null;
        if (Objects.equals(row, ONE_ROW_INDEX)) {
            goldBoarderProbabilistic = goldBoarderProbabilisticConfig.get(gameInfo.getColumn() - 1);
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
                .build();
    }

    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        private long parentGameRecordId;
    }
}