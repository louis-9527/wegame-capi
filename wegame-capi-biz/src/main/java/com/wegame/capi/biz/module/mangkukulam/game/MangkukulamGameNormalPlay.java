package com.wegame.capi.biz.module.mangkukulam.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MangkukulamConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractWheelChartGame;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamSymbolUtil;
import com.wegame.capi.biz.module.mangkukulam.reward.MangkukulamRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.VolatilityEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.RewardTypeEnum;
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
import java.util.stream.IntStream;

import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.MANGKUKULAM_GAME_INFO;
import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;

@Slf4j
@Component("mangkukulamGameNormalPlay")
public class MangkukulamGameNormalPlay extends AbstractWheelChartGame<MangkukulamGameRecordDo> {


    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = MANGKUKULAM_GAME_INFO;
    private static final int MAX_FIRE_BORDER_IN_WHEEL = 3;
    private static final int SPECIAL_WILD_CLEAR_TIMES = 3;
    private static final List<Integer> SPECIAL_WILD_EXISTS_COLUMNS = Arrays.asList(1, 2, 3);
    private static final List<Integer> FIRE_BORDER_EXISTS_COLUMNS = Arrays.asList(1, 2, 3, 4);
    private static final List<Integer> WILD_EXISTS_COLUMNS = Arrays.asList(1, 2, 3, 4);
    private static final int MAX_SCATTER_IN_COLUMN = 1;

    @Autowired
    MangkukulamConfig mangkukulamConfig;

    @Autowired
    MangkukulamRewardHandler mangkukulamRewardHandler;

    @Override
    public List<MangkukulamGameRecordDo> play(GamePlayDo gamePlay) {
        List<MangkukulamGameRecordDo> gameRecordResult = new ArrayList<>();
        LinkedList<FreeWheelHandleDo> freeWheelList = new LinkedList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // todo 执行
        if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode()) && Objects.nonNull(gamePlay.getVolatility())) {
            // 执行完普通轮和免费轮
            generateFirstBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList);
            generateFreeBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList, null);
            setLastGameFlag(gameRecordResult);
        } else if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode()) && Objects.isNull(gamePlay.getVolatility())) {
            // 执行首轮
            generateFirstBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList);
            MangkukulamGameRecordDo lastGame = gameRecordResult.get(gameRecordResult.size() - 1);
            if (Objects.nonNull(lastGame) && !lastGame.getRewardResult().isHasRewardFreeWheel()) {
                setLastGameFlag(gameRecordResult);
            }
        } else if (Objects.equals(BetSubTypeEnum.FREE_WHEEL_NORMAL_BET.getCode(), gamePlay.getBetSubType().getCode()) && Objects.nonNull(gamePlay.getVolatility())) {
            // 执行免费轮
            GamePlayDo.ContinuePlayContext context = gamePlay.getContinuePlayContext();
            AssertUtil.assertNotNull(gamePlay.getContinuePlayContext(), ErrorCode.ERR_INTER);
            loadContinuePlayContext(context, freeWheelList, totalRewardAmount);
            generateFreeBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList, context.getParentBetRecordId());
            setLastGameFlag(gameRecordResult);
        }
        MangkukulamSymbolUtil.postProcessPlaceholderSymbol(gameRecordResult);
        return gameRecordResult;
    }

    /**
     * 生成首轮
     * scatter & wild 不会跟特殊wild在同一列
     * 特殊wild只会在2 3 4 列
     * wild & 火框 只会在 2 3 4 5 列
     */
    private void generateFirstBatch(GamePlayDo gamePlay, List<MangkukulamGameRecordDo> result, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList) {
        MangkukulamConfig.Stage stage = getRandomStage(gamePlay, false);
        WheelDo<MangkukulamSymbolDo> wheel = generateFirstWheel(gamePlay, stage, false);
        RewardResultDo<MangkukulamSymbolDo> rewardResult = mangkukulamRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, null, totalRewardAmount.get()));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 组装-游戏记录
        long gameRecordId = generateGameRecordId();
        // 计算抽奖次数
        MangkukulamGameRecordDo gameRecord = buildGameRecord(gameRecordId, RECORD_DEFAULT_PARENT_ID, gamePlay.getBetRecordId(), null,
                gameRecordId, wheel, rewardResult);
        result.add(gameRecord);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        loopPlayOnPreGameRecord(gamePlay, result, totalRewardAmount, gameRecord, freeWheelList, null, stage);
    }

    /**
     * 生成免费轮
     */
    private void generateFreeBatch(GamePlayDo gamePlay, List<MangkukulamGameRecordDo> result, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList, Long parentBetRecordId) {
        while (!freeWheelList.isEmpty()) {
            FreeWheelHandleDo freeWheel = freeWheelList.removeFirst();
            MangkukulamGameRecordDo parentGameRecord = result.stream().filter(item -> Objects.equals(freeWheel.getParentGameRecordId(), item.getId())).findFirst().orElse(null);
            MangkukulamConfig.Stage stage = getRandomStage(gamePlay, true);
            WheelDo<MangkukulamSymbolDo> wheel = generateFirstWheel(gamePlay, stage, true);
            RewardResultDo<MangkukulamSymbolDo> rewardResult = mangkukulamRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, null, totalRewardAmount.get()));
            long gameRecordId = generateGameRecordId();
            MangkukulamGameRecordDo gameRecord = buildGameRecord(gameRecordId, freeWheel.getParentGameRecordId(), gamePlay.getBetRecordId(), parentBetRecordId, gameRecordId, wheel, rewardResult);
            result.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            if (Objects.nonNull(parentGameRecord)) {
                parentGameRecord.addGameRecordId(gameRecordId);
            }
            loopPlayOnPreGameRecord(gamePlay, result, totalRewardAmount, gameRecord, freeWheelList, parentBetRecordId, stage);
        }
    }

    private void loopPlayOnPreGameRecord(GamePlayDo gamePlay,
                                         List<MangkukulamGameRecordDo> ret,
                                         AtomicLong totalRewardAmount,
                                         MangkukulamGameRecordDo preGameRecord,
                                         LinkedList<FreeWheelHandleDo> freeWheelList,
                                         Long parentBetRecordId,
                                         MangkukulamConfig.Stage stage) {
        if (canContinuePlayOnPreWheel(gamePlay, preGameRecord.getRewardResult(), totalRewardAmount)) {
            WheelDo<MangkukulamSymbolDo> wheel = generateFromPreWheel(preGameRecord, stage);
            RewardResultDo<MangkukulamSymbolDo> rewardResult = mangkukulamRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, preGameRecord.getRewardResult().getTotalMultiple(), totalRewardAmount.get()));
            long gameRecordId = generateGameRecordId();
            MangkukulamGameRecordDo gameRecord = buildGameRecord(gameRecordId, preGameRecord.getId(), gamePlay.getBetRecordId(), parentBetRecordId, preGameRecord.getBatchId(), wheel, rewardResult);
            ret.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList, parentBetRecordId, stage);
        } else {
            if (!preGameRecord.getRewardResult().getAccumulateReachMaxMultiple()) {
                if (preGameRecord.getRewardResult().isHasRewardFreeWheel()) {
                    addFreeWheelTimes(freeWheelList, preGameRecord.getRewardResult().getRewardFreeWheelTimes(), preGameRecord.getId());
                }
            } else {
                // help gc
                freeWheelList.clear();
            }
        }
    }

    private WheelDo<MangkukulamSymbolDo> generateFirstWheel(GamePlayDo gamePlay, MangkukulamConfig.Stage stage, boolean freeWheelFlag) {
        //todo 生成
        int currentIndex = 0;
        // 0. 初始化盘面，填充占位符
        MangkukulamSymbolDo[][] symbolArr = initWheel();
        // 1. 轮带表生成初始盘面
        for (int columNum = 0; columNum < GAME_INFO.getColumn(); columNum++) {
            // 随机循环获取row个元素id
            List<String> currentColumSymbolIdList = super.randomCircularGenSymbolIdList(stage.getReels().get(columNum), GAME_INFO.getRow());
            // 填充
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                if (Objects.nonNull(symbolArr[rowNum][columNum])) {
                    break;
                }
                SymbolDo symbolDo = GAME_INFO.getSymbolById(currentColumSymbolIdList.get(rowNum));
                AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER, "symbolDo is null of id " + currentColumSymbolIdList.get(rowNum));
                MangkukulamSymbolDo symbol = null;
                if (Objects.equals(MangkukulamGameHelper.MANGKUKULAM_SPECIAL_WILD.getId(), symbolDo.getId())) {
                    AssertUtil.assertTrue(SPECIAL_WILD_EXISTS_COLUMNS.contains(columNum), ErrorCode.ERR_INTER, "special wild can not exist in this column : " + columNum);
                    symbol = MangkukulamSymbolDo.buildSpecialWild(SPECIAL_WILD_CLEAR_TIMES);
                } else {
                    symbol = MangkukulamSymbolDo.buildFromSymbol(symbolDo);
                }
                // 填充
                symbolArr[rowNum][columNum] = symbol;
            }
        }
        // 2. 扩展 & 生成特殊wild倍率
        handleSpecialWild(symbolArr, stage);
        // 3. 生成火框
        handleFireBorder(symbolArr, stage, currentIndex);
        return WheelDo.<MangkukulamSymbolDo>builder().index(currentIndex).freeFlag(freeWheelFlag).symbolArr(symbolArr).build();
    }

    private void handleSpecialWild(MangkukulamSymbolDo[][] symbolArr, MangkukulamConfig.Stage stage) {
        //todo 生成倍率 & 该列用占位符占位
        Map<String, Integer> weightMap = stage.getWdExpandingTimesWeights();
        AssertUtil.assertNotNull(weightMap, ErrorCode.ERR_INTER, "wdExpandingTimesWeights is null ");
        List<Integer> superWildColumnList = new ArrayList<>();
        for (int columNum = 0; columNum < GAME_INFO.getColumn(); columNum++) {
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                if (Objects.nonNull(symbolArr[rowNum][columNum]) && symbolArr[rowNum][columNum].isSpecialWild()) {
                    symbolArr[rowNum][columNum].setMultiples(generateMultiples(weightMap));
                    superWildColumnList.add(columNum);
                }
            }
        }
        convertSpecialWild(symbolArr, superWildColumnList);
    }

    private List<BigDecimal> generateMultiples(Map<String, Integer> weightMap) {
        String multipleString = randomKeyByWeight(weightMap);
        AssertUtil.assertNotNull(multipleString, ErrorCode.ERR_INTER, "multipleString is null ");
        String[] multiples = multipleString.split(",");
        return Arrays.stream(multiples).map(BigDecimal::new).sorted().collect(Collectors.toList());
    }

    private void convertSpecialWild(MangkukulamSymbolDo[][] symbolArr, List<Integer> superWildColumnList) {
        if (CollectionUtils.isEmpty(superWildColumnList)) {
            return;
        }
        for (Integer column : superWildColumnList) {
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                if (Objects.nonNull(symbolArr[rowNum][column]) && !symbolArr[rowNum][column].isSpecialWild()) {
                    symbolArr[rowNum][column] = MANGKUKULAM_SYMBOL_PLACEHOLDER;
                }
            }
        }
    }

    private void handleFireBorder(MangkukulamSymbolDo[][] symbolArr, MangkukulamConfig.Stage stage, int currentIndex) {
        MangkukulamConfig.FireNumStageRange fireNumStageRange = stage.getCurrentFireNumberStageRange(currentIndex);
        AssertUtil.assertNotNull(fireNumStageRange, ErrorCode.ERR_INTER, "fireNumStageRange is null");
        Map<String, Integer> weightMap = stage.getFireNumWeights().get(fireNumStageRange.getValue());
        AssertUtil.assertNotNull(weightMap, ErrorCode.ERR_INTER, "fireNumWeights is null of id " + fireNumStageRange.getValue());
        String newFireBorderCount = randomKeyByWeight(weightMap);
        int currentFireBorderCount = MangkukulamSymbolUtil.countFireBorderInWheel(symbolArr);
        int needAddFireBorderCount = Math.min(Integer.parseInt(newFireBorderCount), MAX_FIRE_BORDER_IN_WHEEL - currentFireBorderCount);
        if (needAddFireBorderCount <= 0) {
            return;
        }
        // todo 填入火框元素
        List<IndexDo> canAddFireBorderIndexes = getFireBorderIndexes(symbolArr);
        for (int i = 0; i < needAddFireBorderCount; i++) {
            IndexDo indexDo = canAddFireBorderIndexes.get(i);
            symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()].setSymbolFireBorder(true);
        }
    }

    private List<IndexDo> getFireBorderIndexes(MangkukulamSymbolDo[][] symbolArr) {
        List<IndexDo> canAddFireBorderIndexes = new ArrayList<>();
        for (Integer column : FIRE_BORDER_EXISTS_COLUMNS) {
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                MangkukulamSymbolDo symbol = symbolArr[rowNum][column];
                if (symbol.isPlaceholderSymbol() || symbol.isWildSymbol() || symbol.isSymbolFireBorder()) {
                    break;
                }
                canAddFireBorderIndexes.add(new IndexDo(rowNum, column));
            }
        }
        Collections.shuffle(canAddFireBorderIndexes, new Random());
        return canAddFireBorderIndexes;
    }

    private WheelDo<MangkukulamSymbolDo> generateFromPreWheel(MangkukulamGameRecordDo preGameRecord, MangkukulamConfig.Stage stage) {
        //todo

        //0. 准备阶段 复制盘面
        //1. 消除中奖元素
        //2. 生成
        return null;
    }

    private MangkukulamConfig.Stage getRandomStage(GamePlayDo gamePlay, boolean freeWheelFlag) {
        MangkukulamConfig.MainGame mainGame = getMainGame(gamePlay, freeWheelFlag);

        List<MangkukulamConfig.Stage> stages = mainGame.getStages();

        // 索引作为键，元素作为值
        Map<String, Integer> stageWeight = IntStream.range(0, stages.size())
                .boxed()  // 将 IntStream 转为 Stream<Integer>
                .collect(Collectors.toMap(
                        String::valueOf,
                        i -> stages.get(i).getWeights()
                ));
        String key = randomKeyByWeight(stageWeight);
        return stages.get(Integer.parseInt(key));
    }

    private MangkukulamConfig.MainGame getMainGame(GamePlayDo gamePlay, boolean freeWheelFlag) {
        MangkukulamConfig.Bet bet = null;
        if (Objects.equals(BetTypeEnum.NORMAL_BET.getCode(), gamePlay.getBetType().getCode())) {
            bet = mangkukulamConfig.getNormalBet();
        } else {
            bet = mangkukulamConfig.getBuyBet();
        }
        MangkukulamConfig.MainGame mainGame = null;
        if (freeWheelFlag) {
            if (Objects.equals(VolatilityEnum.LOW.getCode(), gamePlay.getVolatility().getCode())) {
                mainGame = bet.getLow();
            } else {
                mainGame = bet.getHigh();
            }
        } else {
            mainGame = bet.getMainGame();
        }
        return mainGame;
    }

    /**
     * 根据权重随机key
     *
     * @param map 索引值 权重
     * @return 索引值
     */
    private String randomKeyByWeight(Map<String, Integer> map) {
        Integer totalWeights = getTotalWeights(map);
        int randomNumber = randomService.randomInt(Constants.CONST_1, totalWeights);
        int startVal = 0;
        for (Map.Entry<String, Integer> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
            if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                return entry.getKey();
            } else {
                startVal += entry.getValue();
            }
        }
        throw new BusinessException(ErrorCode.ERR_INTER, "random key generation failed");
    }

    private Integer getTotalWeights(Map<String, Integer> map) {
        Integer totalWeight = 0;
        for (Integer weight : map.values()) {
            totalWeight += weight;
        }
        return totalWeight;
    }

    private void loadContinuePlayContext(GamePlayDo.ContinuePlayContext context, LinkedList<FreeWheelHandleDo> freeWheelList, AtomicLong totalRewardAmount) {
        addFreeWheelTimes(freeWheelList, context.getFreeWheelTimes(), context.getParentGameRecordId());
        totalRewardAmount.getAndAdd(context.getTotalPayoutAmount());
    }

    private MangkukulamSymbolDo[][] initWheel() {
        MangkukulamSymbolDo[][] symbolArr = new MangkukulamSymbolDo[MANGKUKULAM_GAME_INFO.getRow()][MANGKUKULAM_GAME_INFO.getColumn()];
        int lastRow = MANGKUKULAM_GAME_INFO.getRow() - 1;
        int lastColumn = MANGKUKULAM_GAME_INFO.getColumn() - 1;
        symbolArr[lastRow][0] = MANGKUKULAM_SYMBOL_PLACEHOLDER;
        symbolArr[lastRow][lastColumn] = MANGKUKULAM_SYMBOL_PLACEHOLDER;
        return symbolArr;
    }


    private MangkukulamGameRecordDo buildGameRecord(Long id,
                                                    Long parentId,
                                                    Long betRecordId,
                                                    Long parentBetRecordId,
                                                    Long batchId,
                                                    WheelDo<MangkukulamSymbolDo> wheel,
                                                    RewardResultDo<MangkukulamSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return MangkukulamGameRecordDo.builder()
                .id(id)
                .parentId(parentId)
                .betRecordId(betRecordId)
                .parentBetRecordId(parentBetRecordId)
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

    private boolean canContinuePlayOnPreWheel(GamePlayDo gamePlay, RewardResultDo<MangkukulamSymbolDo> rewardResult, AtomicLong totalRewardAmount) {
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


    /**
     * 增加免费轮次数
     */
    private void addFreeWheelTimes(LinkedList<FreeWheelHandleDo> freeWheelList, int addTimes, Long parentGameRecordId) {
        if (addTimes < 1) {
            return;
        }
        for (int times = 1; times <= addTimes; times++) {
            freeWheelList.addLast(FreeWheelHandleDo.builder()
                    .parentGameRecordId(parentGameRecordId)
                    .build());
        }
    }

    private void setLastGameFlag(List<MangkukulamGameRecordDo> gameRecordResult) {
        if (CollectionUtils.isNotEmpty(gameRecordResult)) {
            gameRecordResult.get(gameRecordResult.size() - 1).setLastGameFlag(true);
        }
    }

    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        private long parentGameRecordId;
    }
}