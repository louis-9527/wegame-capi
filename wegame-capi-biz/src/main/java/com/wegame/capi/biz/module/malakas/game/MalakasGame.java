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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;
import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_SYMBOL_PLACEHOLDER;
import static com.wegame.capi.biz.module.malakas.MalakasProbabilisticUtil.getWheelProbabilisticConfig;
import static com.wegame.capi.biz.module.malakas.MalakasSymbolUtil.*;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
@Component
public class MalakasGame extends AbstractGame<MalakasGameRecordDo> {

    private static final int MAX_REWARD_MULTIPLE = 2500;
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

        // 生成初始游戏记录
        MalakasGameRecordDo initialRecord = generateInitialGameRecord(gamePlay);
        ret.add(initialRecord);
        totalRewardAmount.addAndGet(initialRecord.getRewardResult().getTotalRewardAmount());

        // 处理投注轮游戏后续
        CopyOnWriteArrayList<FreeWheelHandleDo> freeWheelList = new CopyOnWriteArrayList<>();
        processGameFlow(gamePlay, ret, totalRewardAmount, initialRecord, freeWheelList);

        // 处理免费轮游戏后续
        if (CollectionUtils.isNotEmpty(freeWheelList)) {
            handleFreeWheelGamePlay(gamePlay, ret, totalRewardAmount, freeWheelList);
        }
        postPlaceholderSymbol(ret);
        return ret;
    }

    private MalakasGameRecordDo generateInitialGameRecord(GamePlayDo gamePlay) {
        // 生成初始游戏滚轮
        WheelDo<MalakasSymbolDo> wheel;
        long gameRecordId = generateGameRecordId();
        switch (gamePlay.getBetType()) {
            // 正常投注
            case NORMAL_BET:
                wheel = generateNormalWheel(getGameInfo(), getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), false));
                break;
            // 购买投注
            case BUY_BET:
                wheel = generateRewardFreeWheel(getGameInfo(), getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), false));
                break;
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }
        // 判奖-根据游戏滚轮和中奖线算法判断
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(getGameInfo(), gamePlay, wheel, BigDecimal.ZERO));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 构建游戏记录
        return buildGameRecord(gameRecordId, RECORD_DEFAULT_PARENT_ID, gamePlay.getBetRecordId(), gameRecordId, wheel, rewardResult);
    }

    /**
     * 处理每一轮至到没有中间线
     */
    private void processGameFlow(GamePlayDo gamePlay,
                                 List<MalakasGameRecordDo> ret,
                                 AtomicLong totalRewardAmount,
                                 MalakasGameRecordDo initialRecord,
                                 CopyOnWriteArrayList<FreeWheelHandleDo> freeWheelList) {
        Queue<GameState> gameQueue = new LinkedList<>();
        gameQueue.offer(GameState.builder()
                .gameRecord(initialRecord)
                .totalRewardAmount(totalRewardAmount)
                .build());

        while (!gameQueue.isEmpty()) {
            GameState currentState = gameQueue.poll();
            MalakasGameRecordDo currentRecord = currentState.getGameRecord();
            AtomicLong currentTotalReward = currentState.getTotalRewardAmount();

            // 检查是否达到最大倍数限制
            if (currentTotalReward.get() >= MAX_REWARD_MULTIPLE * gamePlay.getBetAmount()) {
                log.warn("reach max reward multiple of {} bet. totalRewardAmount: {}, betAmount: {}", MAX_REWARD_MULTIPLE, currentTotalReward.get(), gamePlay.getBetAmount());
                freeWheelList.clear();
                return;
            }

            if (canContinuePlayOnPreWheel(currentRecord.getRewardResult(), currentTotalReward)) {
                handleCascadingPlay(gamePlay, ret, currentRecord, currentTotalReward, gameQueue);
            } else if (currentRecord.getRewardResult().isHasRewardFreeWheel()) {
                // 结算免费轮
                Optional.ofNullable(currentRecord.getGameRecordIds())
                        .orElse(Collections.emptyList())
                        .forEach(item -> {
                            freeWheelList.add(FreeWheelHandleDo.builder()
                                    .id(item)
                                    .parentId(currentRecord.getId())
                                    .build());
                        });
            }
        }
    }

    private void handleFreeWheelGamePlay(GamePlayDo gamePlay,
                                         List<MalakasGameRecordDo> ret,
                                         AtomicLong totalRewardAmount,
                                         CopyOnWriteArrayList<FreeWheelHandleDo> freeWheelList) {
        // 倍数
        BigDecimal totalMultiple = BigDecimal.ZERO;
        while (!freeWheelList.isEmpty()) {
            FreeWheelHandleDo freeWheel = freeWheelList.remove(0);
            // 生成免费轮游戏记录
            MalakasGameRecordDo freeWheelGameRecord = generateFreeWheelGameRecord(gamePlay, freeWheel, totalMultiple);
            ret.add(freeWheelGameRecord);
            totalRewardAmount.addAndGet(freeWheelGameRecord.getRewardResult().getTotalRewardAmount());
            // 如果有中奖，处理后续游戏流程
            if (CollectionUtils.isNotEmpty(freeWheelGameRecord.getRewardResult().getRewardLineDetailList())) {
                // 倍数更新
                totalMultiple = freeWheelGameRecord.getRewardResult().getTotalMultiple();
                processGameFlow(gamePlay, ret, totalRewardAmount, freeWheelGameRecord, freeWheelList);
            }
        }
    }

    private MalakasGameRecordDo generateFreeWheelGameRecord(GamePlayDo gamePlay, FreeWheelHandleDo freeWheel, BigDecimal currentMultiple) {
        WheelDo<MalakasSymbolDo> wheel = generateNormalWheel(getGameInfo(), getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), true));

        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(getGameInfo(), gamePlay, wheel, currentMultiple));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);

        return buildGameRecord(freeWheel.getId(), freeWheel.getParentId(), gamePlay.getBetRecordId(), freeWheel.getId(), wheel, rewardResult);
    }

    // 处理级联游戏逻辑
    private void handleCascadingPlay(GamePlayDo gamePlay,
                                     List<MalakasGameRecordDo> ret,
                                     MalakasGameRecordDo currentRecord,
                                     AtomicLong currentTotalReward,
                                     Queue<GameState> gameQueue) {
        // 生成新的滚轮和奖励
        WheelDo<MalakasSymbolDo> newWheel = generateNormalWheelFromPreWheel(
                getGameInfo(),
                getWheelProbabilisticConfig(gamePlay, malakasConfig.getProbabilisticConfig(), false),
                currentRecord
        );
        RewardResultDo<MalakasSymbolDo> newRewardResult = malakasRewardHandler.handle(
                RewardHandlerBuilder.build(getGameInfo(), gamePlay, newWheel,
                        currentRecord.getRewardResult().getTotalMultiple())
        );
        // 生成新的游戏记录
        long newGameRecordId = generateGameRecordId();
        MalakasGameRecordDo newGameRecord = buildGameRecord(
                newGameRecordId,
                currentRecord.getId(),
                gamePlay.getBetRecordId(),
                currentRecord.getBatchId(),
                newWheel,
                newRewardResult
        );
        ret.add(newGameRecord);
        // 中奖金额累加
        currentTotalReward.addAndGet(newRewardResult.getTotalRewardAmount());
        // 将新状态加入队列
        gameQueue.offer(GameState.builder()
                .gameRecord(newGameRecord)
                .totalRewardAmount(currentTotalReward)
                .build());
    }

    // 生成游戏记录ID列表
    public List<Long> generateGameRecordIdList(int times) {
        List<Long> gameRecordIdList = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            gameRecordIdList.add(snowflakeIdGenerator.nextId());
        }
        return gameRecordIdList;
    }

    private WheelDo<MalakasSymbolDo> generateNormalWheelFromPreWheel(GameInfoDo gameInfo,
                                                                     Map<String, List<BigDecimal>> probabilisticConfig,
                                                                     MalakasGameRecordDo preGameRecord) {
        WheelDo<MalakasSymbolDo> preWheel = preGameRecord.getWheel();
        RewardResultDo<MalakasSymbolDo> preRewardResult = preGameRecord.getRewardResult();
        // todo del
        // MalakasSymbolUtil.print(preWheel.getSymbolArr(), "pre wheel");
        // 复制：元素数组
        MalakasSymbolDo[][] symbolArr = copySymbolArr(preWheel.getSymbolArr());
        // todo del
        // MalakasSymbolUtil.print(symbolArr, "copy after");
        // 消除：中奖元素
        clearRewardSymbol(symbolArr, preRewardResult);
        MalakasSymbolUtil.print(symbolArr, "clear after");
        // 重建：元素数组（非空元素按列下落处理：第1-6Reel非空元素向下移动，第7Reel非空元素向左移动）
        rebuildSymbolArr(symbolArr, gameInfo);
        // todo del
        // MalakasSymbolUtil.print(symbolArr, "rebuild after");
        // 生产：缺失元素(填补剩余元素)
        WheelDo<MalakasSymbolDo> wheel = generateCommonNormalWheel(gameInfo, symbolArr, probabilisticConfig, preWheel.getIndex() + 1, WHEEL_DEFAULT_INDEX);
        // todo del
        // MalakasSymbolUtil.print(wheel.getSymbolArr(), "current wheel");
        return wheel;
    }

    /**
     * 生成正常滚轮：生成逻辑无任何限制
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<MalakasSymbolDo> generateNormalWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 第一行第一列： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][ONE_COLUM_INDEX] = MALAKAS_SYMBOL_PLACEHOLDER;
        // 第一行最后一列： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][gameInfo.getColumn() - 1] = MALAKAS_SYMBOL_PLACEHOLDER;
        return generateCommonNormalWheel(gameInfo, symbolArr, probabilisticConfig, WHEEL_DEFAULT_INDEX, WHEEL_DEFAULT_INDEX);
    }

    /**
     * 生成中奖免费轮滚轮: 需要满足4-6个Scatter
     *
     * @param gameInfo 游戏信息
     * @return 滚轮
     */
    private WheelDo<MalakasSymbolDo> generateRewardFreeWheel(GameInfoDo gameInfo, Map<String, List<BigDecimal>> probabilisticConfig) {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[gameInfo.getRow()][gameInfo.getColumn()];
        // 第一行第一列： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][ONE_COLUM_INDEX] = MALAKAS_SYMBOL_PLACEHOLDER;
        // 第一行最后一列： 不生成元素,使用占位元素
        symbolArr[ONE_ROW_INDEX][gameInfo.getColumn() - 1] = MALAKAS_SYMBOL_PLACEHOLDER;
        WheelDo<MalakasSymbolDo> wheel = generateCommonNormalWheel(gameInfo, symbolArr, probabilisticConfig, WHEEL_DEFAULT_INDEX, WHEEL_DEFAULT_INDEX);
        int scatterSymbolCount = countIndexSymbol(wheel.getSymbolArr(), SymbolEnum.Scatter);
        int randomCount = randomService.randomInt(4, 6);
        int needAddCount = randomCount - scatterSymbolCount;
        int num = 0;
        while (num < needAddCount) {
            int row = randomService.randomInt(0, gameInfo.getRow() - 1);
            int column = randomService.randomInt(0, gameInfo.getColumn() - 1);
            if (Objects.equals(wheel.getSymbolArr()[row][column], MALAKAS_SYMBOL_PLACEHOLDER)
                    || StringUtils.equals(wheel.getSymbolArr()[row][column].getId(), SymbolEnum.Scatter.getId())) {
                // 为占位元素或者scatter元素，则继续
                // do nothing;
            } else {
                wheel.getSymbolArr()[row][column].setId(SymbolEnum.Scatter.getId());
                wheel.getSymbolArr()[row][column].setName(SymbolEnum.Scatter.getName());
                wheel.getSymbolArr()[row][column].setSymbolType(SymbolTypeEnum.SCATTER);
                num++;
            }
        }
        return wheel;
    }

    private WheelDo<MalakasSymbolDo> generateCommonNormalWheel(GameInfoDo gameInfo,
                                                               MalakasSymbolDo[][] symbolArr,
                                                               Map<String, List<BigDecimal>> probabilisticConfig,
                                                               int wheelIndex,
                                                               int parentIndex) {
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        int randomNumberIndex = 0;
        int scatterNumberInWheel = 0;
        for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
            // 一列只能包含两个scatter
            int scatterNumberInReel = 0;
            Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(column, probabilisticConfig);
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    // 当前元素非空，则跳过
                    continue;
                }
                Integer randomNumber = randomNumberList.get(randomNumberIndex);
                if (scatterNumberInReel >= MAX_SCATTER_NUMBER_IN_REEL || scatterNumberInWheel >= MAX_SCATTER_NUMBER_IN_WHEEL || Objects.equals(row, ONE_ROW_INDEX)) {
                    // 第一行 || 列scatter达到上限 || 滚轮scatter达到上限，则生产非scatter元素
                    // 生成非scatter
                    SymbolDo symbolDo = randomNonScatterSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(row, MalakasSymbolUtil.getMaxRow(symbolArr, row, column), column, gameInfo);
                    MalakasSymbolUtil.handlePlaceholderSymbol(symbolArr, row, column, symbolSize);
                    // 构建元素金边
                    boolean symbolGoldBorder = generateSymbolGoldBorder(gameInfo, row, column);
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbol(symbolDo, symbolSize, symbolGoldBorder);
                    symbolArr[row][column] = malakasSymbolDo;
                } else {
                    // 随机生成
                    SymbolDo symbolDo = randomNormalSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(row, MalakasSymbolUtil.getMaxRow(symbolArr, row, column), column, gameInfo);
                    MalakasSymbolUtil.handlePlaceholderSymbol(symbolArr, row, column, symbolSize);
                    // 构建元素金边
                    boolean symbolGoldBorder = generateSymbolGoldBorder(gameInfo, row, column);
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbol(symbolDo, symbolSize, symbolGoldBorder);
                    symbolArr[row][column] = malakasSymbolDo;
                    if (symbolDo.isScatterSymbol()) {
                        scatterNumberInReel++;
                        scatterNumberInWheel++;
                    }
                }
                randomNumberIndex++;
            }
        }
        // 返回：滚轮
        return WheelDo.<MalakasSymbolDo>builder()
                .index(wheelIndex)
                .symbolArr(symbolArr)
                .build();
    }

    private boolean canContinuePlayOnPreWheel(RewardResultDo<MalakasSymbolDo> rewardResult, AtomicLong totalRewardAmount) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return false;
        }
        if (totalRewardAmount.get() >= MAX_REWARD_MULTIPLE * rewardResult.getBetAmount()) {
            log.warn("reach max reward multiple of {} bet. totalRewardAmount: {}, betAmount: {}", MAX_REWARD_MULTIPLE, totalRewardAmount, rewardResult.getBetAmount());
            return false;
        }
        return CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList());
    }

    private SymbolSizeEnum generateSymbolSize(int currentRow, int maxRow, int currentColum, GameInfoDo gameInfo) {
        if (Objects.equals(currentRow, ONE_ROW_INDEX)
                || Objects.equals(currentColum, ONE_COLUM_INDEX)
                || Objects.equals(currentColum, gameInfo.getColumn() - 1)) {
            // 第一行，第一列，最后一列：只能为小元素
            return SymbolSizeEnum.S;
        }
        Map<String, Integer> map = MalakasProbabilisticUtil.buildColumSymboleSizeProbabilisticMap(currentColum, maxRow - currentRow + 1, malakasConfig.getProbabilisticConfig());
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        return randomSymbolSize(map, randomService.randomInt(1, map.values()
                .stream()
                .reduce(0, Integer::sum)));
    }

    private boolean generateSymbolGoldBorder(GameInfoDo gameInfo, int row, int column) {
        List<BigDecimal> goldBoarderProbabilisticConfig = malakasConfig.getProbabilisticConfig().getGoldBoarderProbabilisticConfig();
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
        List<Long> gameRecordIdList = null;
        if (CollectionUtils.isEmpty(rewardResult.getRewardLineDetailList()) && rewardResult.isHasRewardFreeWheel()) {
            gameRecordIdList = generateGameRecordIdList(rewardResult.getRewardFreeWheelTimes());
//            gameRecordIdList = generateGameRecordIdList(2);
        }
        return MalakasGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .batchId(batchId)
                .playerId(userInfo.getPlayerId())
                .playerName(userInfo.getPlayerName())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .gameRecordIds(gameRecordIdList)
                .build();
    }

    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        private long id;
        private long parentId;
    }

    @Data
    @SuperBuilder
    private static class GameState {
        private MalakasGameRecordDo gameRecord;
        private AtomicLong totalRewardAmount;
    }
}
