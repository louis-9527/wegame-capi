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
import com.wegame.capi.biz.module.base.AmountHelper;
import com.wegame.capi.biz.module.base.game.AbstractWheelChartGame;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamSymbolUtil;
import com.wegame.capi.biz.module.mangkukulam.reward.MangkukulamRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.VolatilityTypeEnum;
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

import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.*;

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

    @Autowired
    MangkukulamConfig mangkukulamConfig;

    @Autowired
    MangkukulamRewardHandler mangkukulamRewardHandler;

    @Override
    public List<MangkukulamGameRecordDo> play(GamePlayDo gamePlay) {
        List<MangkukulamGameRecordDo> gameRecordResult = new ArrayList<>();
        LinkedList<FreeWheelHandleDo> freeWheelList = new LinkedList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode()) && Objects.nonNull(gamePlay.getVolatility())) {
            // 执行完普通轮和免费轮
            generateFirstBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList);
            generateFreeBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList, null);
            setLastGameFlag(gameRecordResult);
        } else if (BetSubTypeEnum.isFirstStep(gamePlay.getBetSubType().getCode()) && Objects.isNull(gamePlay.getVolatility())) {
            // 执行首轮
            generateFirstBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList);
            MangkukulamGameRecordDo lastGame = gameRecordResult.get(gameRecordResult.size() - 1);
            boolean reachMaxMultiple = AmountHelper.reachMaxMultiple(totalRewardAmount.get(), gamePlay.getBetAmount(), mangkukulamConfig.getRewardMaxMultiple());
            boolean rewardFreeWheel = (Objects.nonNull(lastGame) && !lastGame.getRewardResult().isHasRewardFreeWheel());
            if (reachMaxMultiple || !rewardFreeWheel) {
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
        WheelDo<MangkukulamSymbolDo> wheel = generateFirstWheel(stage, false);
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
        if (AmountHelper.reachMaxMultiple(totalRewardAmount.get(), gamePlay.getBetAmount(), mangkukulamConfig.getRewardMaxMultiple())) {
            log.warn("reach max reward multiple bet. totalRewardAmount: {}, betAmount: {}", totalRewardAmount.get(), gamePlay.getBetAmount());
            return;
        }
        while (!freeWheelList.isEmpty()) {
            FreeWheelHandleDo freeWheel = freeWheelList.removeFirst();
            MangkukulamGameRecordDo parentGameRecord = result.stream().filter(item -> Objects.equals(freeWheel.getParentGameRecordId(), item.getId())).findFirst().orElse(null);
            // 每一轮都新随机一个配置
            MangkukulamConfig.Stage stage = getRandomStage(gamePlay, true);
            WheelDo<MangkukulamSymbolDo> wheel = generateFirstWheel(stage, true);
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

    private WheelDo<MangkukulamSymbolDo> generateFirstWheel(MangkukulamConfig.Stage stage, boolean freeWheelFlag) {
        int currentIndex = 0;
        // 0. 初始化盘面，填充占位符
        MangkukulamSymbolDo[][] symbolArr = initWheel();
        // 1. 轮带表生成初始盘面
        for (int columNum = 0; columNum < GAME_INFO.getColumn(); columNum++) {
            // 随机循环获取row个元素id
            List<String> currentColumSymbolIdList = super.randomCircularGenSymbolIdList(stage.getReels().get(columNum), GAME_INFO.getRow());
            // 填充
            boolean hasScatterInColum = false;
            boolean hasSpecialWildInColum = false;
            for (int rowNum = 0; rowNum < GAME_INFO.getRow(); rowNum++) {
                if (Objects.nonNull(symbolArr[rowNum][columNum])) {
                    continue;
                }
                MangkukulamSymbolDo symbol = buildSymbolFromString(currentColumSymbolIdList.get(rowNum));
                boolean validateSymbolInColumn = validateSymbolInColumn(symbol, columNum, hasScatterInColum, hasSpecialWildInColum);
                AssertUtil.assertTrue(validateSymbolInColumn, ErrorCode.ERR_PARAM, "invalid symbol in column : " + columNum);
                if (symbol.isScatterSymbol()) {
                    hasScatterInColum = true;
                } else if (symbol.isSpecialWild()) {
                    hasSpecialWildInColum = true;
                }
                // 填充
                symbolArr[rowNum][columNum] = symbol;
            }
        }
        postProcessSymbolArr(symbolArr, stage, currentIndex, null);
        return WheelDo.<MangkukulamSymbolDo>builder().index(currentIndex).freeFlag(freeWheelFlag).symbolArr(symbolArr).build();
    }

    private boolean validateSymbolInColumn(MangkukulamSymbolDo symbol, int columNum, boolean hasScatterInColum, boolean hasSpecialWildInColum) {
        if (symbol.isSpecialWild()) {
            // 特殊百搭只能出现在123列 & 并且当前列没有scatter 和 特殊百搭
            return SPECIAL_WILD_EXISTS_COLUMNS.contains(columNum) && !hasScatterInColum && !hasSpecialWildInColum;
        } else if (symbol.isNormalWild()) {
            // 普通wild只能出现在1234列
            return WILD_EXISTS_COLUMNS.contains(columNum);
        } else if (symbol.isScatterSymbol()) {
            // scatter一列只能出现一个
            return !hasScatterInColum && !hasSpecialWildInColum;
        } else {
            return true;
        }
    }

    private MangkukulamSymbolDo buildSymbolFromString(String symbolStr) {
        SymbolDo symbolDo = GAME_INFO.getSymbolById(symbolStr);
        AssertUtil.assertNotNull(symbolDo, ErrorCode.ERR_INTER, "symbolDo is null of id " + symbolStr);
        MangkukulamSymbolDo symbol = null;
        if (Objects.equals(MangkukulamGameHelper.MANGKUKULAM_SPECIAL_WILD.getId(), symbolDo.getId())) {
            symbol = MangkukulamSymbolDo.buildSpecialWild(SPECIAL_WILD_CLEAR_TIMES);
        } else {
            symbol = MangkukulamSymbolDo.buildFromSymbol(symbolDo);
        }
        return symbol;
    }

    private WheelDo<MangkukulamSymbolDo> generateFromPreWheel(MangkukulamGameRecordDo preGameRecord, MangkukulamConfig.Stage stage) {
        int currentIndex = preGameRecord.getIndex() + 1;
        boolean freeWheel = preGameRecord.getWheel().isFreeFlag();
        WheelDo<MangkukulamSymbolDo> preWheel = preGameRecord.getWheel();
        RewardResultDo<MangkukulamSymbolDo> preRewardResult = preGameRecord.getRewardResult();
        // 复制：元素数组
        MangkukulamSymbolDo[][] symbolArr = MangkukulamSymbolUtil.copySymbolArr(preWheel.getSymbolArr());
        //1. 消除中奖元素
        List<IndexDo> borderWildIndexes = MangkukulamSymbolUtil.clearRewardSymbol(symbolArr, preRewardResult);
        //2. 下落
        MangkukulamSymbolUtil.fallSymbols(symbolArr);
        //3. 补充元素
        fill(symbolArr, stage, currentIndex);
        //4. 后置处理
        postProcessSymbolArr(symbolArr, stage, currentIndex, borderWildIndexes);
        return WheelDo.<MangkukulamSymbolDo>builder().index(currentIndex).freeFlag(freeWheel).symbolArr(symbolArr).build();
    }

    private void handleSpecialWild(MangkukulamSymbolDo[][] symbolArr, MangkukulamConfig.Stage stage) {
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
        MangkukulamSymbolUtil.convertSpecialWild(symbolArr, superWildColumnList);
    }

    private List<BigDecimal> generateMultiples(Map<String, Integer> weightMap) {
        String multipleString = randomKeyByWeight(weightMap);
        AssertUtil.assertNotNull(multipleString, ErrorCode.ERR_INTER, "multipleString is null ");
        String[] multiples = multipleString.split(",");
        return Arrays.stream(multiples).map(String::trim).map(BigDecimal::new).sorted().collect(Collectors.toList());
    }

    private void handleFireBorder(MangkukulamSymbolDo[][] symbolArr, MangkukulamConfig.Stage stage, int currentIndex) {
        Map<String, Integer> weightMap = stage.getCurrentFireWeights(currentIndex);
        if (Objects.isNull(weightMap)) {
            return;
        }
        String newFireBorderCount = randomKeyByWeight(weightMap);
        int currentFireBorderCount = MangkukulamSymbolUtil.countFireBorderInWheel(symbolArr);
        int needAddFireBorderCount = Math.min(Integer.parseInt(newFireBorderCount), MAX_FIRE_BORDER_IN_WHEEL - currentFireBorderCount);
        if (needAddFireBorderCount <= 0) {
            return;
        }
        List<IndexDo> canAddFireBorderIndexes = MangkukulamSymbolUtil.getAvailableFireBorderIndexes(symbolArr, FIRE_BORDER_EXISTS_COLUMNS);
        int canAddFireBorderIndexCount = Math.min(canAddFireBorderIndexes.size(), needAddFireBorderCount);
        for (int i = 0; i < canAddFireBorderIndexCount; i++) {
            IndexDo indexDo = canAddFireBorderIndexes.get(i);
            symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()].setSymbolFireBorder(true);
        }
    }

    private void postProcessSymbolArr(MangkukulamSymbolDo[][] symbolArr, MangkukulamConfig.Stage stage, int currentIndex, List<IndexDo> borderWildIndexes) {
        handleSpecialWild(symbolArr, stage);
        handleFireBorder(symbolArr, stage, currentIndex);
        replaceMovableWild(symbolArr, borderWildIndexes);
    }

    private void replaceMovableWild(MangkukulamSymbolDo[][] symbolArr, List<IndexDo> borderWildIndexes) {
        if (CollectionUtils.isEmpty(borderWildIndexes)) {
            return;
        }
        List<IndexDo> availableIndexes = MangkukulamSymbolUtil.listAvailableMoveWildIndexes(symbolArr, WILD_EXISTS_COLUMNS);
        int needIndexCount = borderWildIndexes.size();
        // 如果位置足够就移动到非原位置
        if (availableIndexes.size() - needIndexCount >= needIndexCount) {
            availableIndexes.removeAll(borderWildIndexes);
        }
        int canBeWildIndexCount = Math.min(availableIndexes.size(), borderWildIndexes.size());
        for (int i = 0; i < canBeWildIndexCount; i++) {
            IndexDo indexDo = availableIndexes.get(i);
            symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = MangkukulamSymbolDo.buildFromSymbol(MANGKUKULAM_Wild);
        }
    }

    private void fill(MangkukulamSymbolDo[][] symbolArr, MangkukulamConfig.Stage stage, int index) {
        for (int i = 0; i < GAME_INFO.getColumn(); i++) {
            fillColumn(symbolArr, stage, i, index);
        }
    }

    private void fillColumn(MangkukulamSymbolDo[][] symbolArr, MangkukulamConfig.Stage stage, int column, int currentIndex) {
        //如果该列有scatter 生成非scatter
        LinkedList<String> fillSymbols = new LinkedList<>();
        randomSymbolList(fillSymbols, stage, currentIndex);
        boolean hasScatterInColumn = MangkukulamSymbolUtil.hasScatterInColumn(symbolArr, column);
        boolean hasSpecialWildInColumn = MangkukulamSymbolUtil.hasSpecialWildInColumn(symbolArr, column);
        for (int i = GAME_INFO.getRow() - 1; i >= 0; i--) {
            // 从下往上补
            MangkukulamSymbolDo symbol = symbolArr[i][column];
            if (Objects.nonNull(symbol)) {
                continue;
            }
            boolean filled = false;
            while (!filled) {
                if (fillSymbols.isEmpty()) {
                    randomSymbolList(fillSymbols, stage, currentIndex);
                }
                MangkukulamSymbolDo symbolDo = buildSymbolFromString(fillSymbols.removeFirst());
                if (!validateSymbolInColumn(symbolDo, column, hasScatterInColumn, hasSpecialWildInColumn)) {
                    continue;
                }
                // 如果当前列是scatter
                if (symbolDo.isScatterSymbol()) {
                    hasScatterInColumn = true;
                } else if (symbolDo.isSpecialWild()) {
                    hasSpecialWildInColumn = true;
                }
                symbolArr[i][column] = symbolDo;
                filled = true;
            }
        }
        fillSymbols.clear();
    }

    private void randomSymbolList(List<String> fillSymbols, MangkukulamConfig.Stage stage, int currentIndex) {
        Map<String, Integer> weightMap = stage.getCurrentSymbolsWeights(currentIndex);
        AssertUtil.assertNotNull(weightMap, ErrorCode.ERR_INTER, "fireNumWeights is null");
        String symbolListString = randomKeyByWeight(weightMap);
        List<String> symbols = Arrays.stream(symbolListString.split(",")).map(String::trim).collect(Collectors.toList());
        fillSymbols.addAll(symbols);
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
            if (Objects.equals(VolatilityTypeEnum.LOW.getCode(), gamePlay.getVolatility().getCode())) {
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
        int mapSize = map.size();
        AssertUtil.assertTrue(mapSize > 0, ErrorCode.ERR_INTER, "weight map size is less than 1");
        if (mapSize > 1) {
            int totalWeights = map.values().stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
            int randomNumber = randomService.randomInt(Constants.CONST_1, totalWeights);
            int startVal = 0;
            for (Map.Entry<String, Integer> entry : CollectionUtils.emptyIfNull(map.entrySet())) {
                if (randomNumber > startVal && randomNumber <= startVal + entry.getValue()) {
                    return entry.getKey();
                } else {
                    startVal += entry.getValue();
                }
            }
        } else {
            return map.keySet().stream().findFirst().get();
        }
        throw new BusinessException(ErrorCode.ERR_INTER, "random key generation failed");
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
            log.warn("reach max reward multiple bet. totalRewardAmount: {}, betAmount: {}", totalRewardAmount.get(), gamePlay.getBetAmount());
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