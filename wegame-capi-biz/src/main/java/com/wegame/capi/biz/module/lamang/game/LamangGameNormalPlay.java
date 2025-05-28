package com.wegame.capi.biz.module.lamang.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.*;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.biz.module.lamang.LamangProbabilisticUtil;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.Constants;
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
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.base.AmountHelper.reachMaxMultiple;
import static com.wegame.capi.biz.module.lamang.LamangGameHelper.*;
import static com.wegame.capi.biz.module.lamang.LamangSymbolUtil.countIndexSymbol;
import static com.wegame.capi.biz.module.lamang.LamangSymbolUtil.*;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component("lamangGameNormalPlay")
public class LamangGameNormalPlay extends AbstractGame<LamangGameRecordDo> {

    private static final int MAX_SCATTER_NUMBER_IN_REEL = 1;
    private static final int MAX_SCATTER_NUMBER_IN_WHEEL = 3;
    private static final int REWARD_FREE_WHEEL_SCATTER_NUMBER = 3;
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = LAMANG_GAME_INFO;

    private static final int SUPER_WILD_START_ROW_INDEX = 1;
    private static final int SUPER_WILD_START_COLUMN_INDEX = 1;
    private static final int SUPER_WILD_END_ROW_INDEX = 2;
    private static final int SUPER_WILD_END_COLUMN_INDEX = 3;

    @Autowired
    LamangConfig lamangConfig;

    @Autowired
    RewardHandler lamangRewardHandler;

    @Override
    public List<LamangGameRecordDo> play(GamePlayDo gamePlay) {
        //todo 添加抽奖逻辑
        List<LamangGameRecordDo> gameRecordResult = new ArrayList<>();
        LinkedList<FreeWheelHandleDo> freeWheelList = new LinkedList<>();
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        switch (gamePlay.getBetType()) {
            // 正常投注
            case NORMAL_BET:
                generateFirstBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList, false);
                break;
            // 购买投注
            case BUY_BET:
                generateFirstBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList, true);
                break;
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }

        //生成免费轮
        if (CollectionUtils.isNotEmpty(freeWheelList)) {
            generateFreeBatch(gamePlay, gameRecordResult, totalRewardAmount, freeWheelList);
        }
        // 设置结束
        if (CollectionUtils.isNotEmpty(gameRecordResult)) {
            gameRecordResult.get(gameRecordResult.size() - 1).setLastGameFlag(true);
        }
        // 清理前一轮的占位符
        postProcessPlaceholderSymbol(gameRecordResult);
        return gameRecordResult;
    }



    /**
     * 填充滚轮
     */
    private void fillWheel(GameInfoDo gameInfo,
                           LamangSymbolDo[][] symbolArr,
                           LamangConfig.PlayProbabilisticConfig playProbabilisticConfig,
                           boolean mustRewardFreeWheel) {

        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        // 最大
        int scatterNumberInWheel = countIndexSymbol(symbolArr, SymbolEnum.Scatter);
        // 如果必须中免费轮，需要维持scatter数量
        if (mustRewardFreeWheel) {
            holdScatterCount(symbolArr, REWARD_FREE_WHEEL_SCATTER_NUMBER, scatterNumberInWheel);
            scatterNumberInWheel = REWARD_FREE_WHEEL_SCATTER_NUMBER;
        }
        int randomNumberIndex = 0;
        for (int column = ONE_COLUM_INDEX; column < gameInfo.getColumn(); column++) {
            // 一个reel只能包含一个reel
            int scatterNumberInColumn = countIndexSymbol(symbolArr, SymbolEnum.Scatter, column);
            TreeMap<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(column, playProbabilisticConfig.getWheelProbabilisticConfig());
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    // 当前元素非空，则不生成
                    randomNumberIndex = randomNumberIndex + 1;
                    continue;
                }

                AssertUtil.assertTrue(MapUtils.isNotEmpty(columProbabilisticMap), ErrorCode.ERR_INTER);
                Integer randomNumber = randomNumberList.get(randomNumberIndex);
                SymbolDo symbolDo = null;
                if (scatterNumberInColumn >= MAX_SCATTER_NUMBER_IN_REEL || scatterNumberInWheel >= MAX_SCATTER_NUMBER_IN_WHEEL) {
                    // 列scatter达到上限 || 滚轮scatter达到上限，则生产非scatter元素
                    // 生成元素：非scatter元素
                    symbolDo = randomNonScatterSymbol(randomNumber, gameInfo, columProbabilisticMap);
                } else {
                    // 生成元素：随机生成
                    symbolDo = randomNormalSymbol(randomNumber, gameInfo, columProbabilisticMap);
                }

                // 构建元素金边
                boolean symbolGoldBorder = generateSymbolGoldBorder(playProbabilisticConfig, row, column);
                LamangSymbolDo lamangSymbolDo = LamangSymbolDo.buildFromSymbol(symbolDo, symbolGoldBorder);
                symbolArr[row][column] = lamangSymbolDo;
                if (symbolDo.isScatterSymbol()) {
                    scatterNumberInColumn = scatterNumberInColumn + 1;
                    scatterNumberInWheel = scatterNumberInWheel + 1;
                }
                randomNumberIndex = randomNumberIndex + 1;
            }
        }
    }

    /**
     * 维持wheel中的scatter数量
     * @param symbolArr
     * @param needScatterNumber
     * @param scatterNumberInWheel
     */
    private void holdScatterCount(LamangSymbolDo[][] symbolArr, int needScatterNumber, int scatterNumberInWheel) {
        int needAddScatterNumber = needScatterNumber - scatterNumberInWheel;
        // 如果需要加入scatter
        if (needAddScatterNumber > 0) {
            // 将没有元素的位置加入列表
            List<IndexDo> indexList = new ArrayList<>();
            for (int row = 1; row < GAME_INFO.getRow(); row++) {
                for (int column = 0; column < GAME_INFO.getColumn(); column++) {
                    LamangSymbolDo symbol = symbolArr[row][column];
                    if (Objects.isNull(symbol)) {
                        indexList.add(IndexFactory.buildDo(row, column));
                    }
                }
            }
            int tempCount = 0;
            while (tempCount < needAddScatterNumber && !indexList.isEmpty()) {
                int randomIndex = randomService.randomInt(0, indexList.size() - 1);
                IndexDo indexDo = indexList.get(randomIndex);
                int row = indexDo.getRowIndex();
                int column = indexDo.getColumnIndex();
                AssertUtil.assertNull(symbolArr[row][column], ErrorCode.ERR_INTER);

                int columnScatterNum = countIndexSymbol(symbolArr, SymbolEnum.Scatter, column);
                if (columnScatterNum >= MAX_SCATTER_NUMBER_IN_REEL) {
                    // 不符合的坐标 直接移除
                    indexList.remove(randomIndex);
                    continue;
                }
                symbolArr[row][column] = LamangSymbolDo.buildFromSymbol(LAMANG_SCATTER, false);
                tempCount = tempCount + 1;
                indexList.remove(randomIndex);
            }
        }
    }

    private WheelDo<LamangSymbolDo> generateFirstWheel(LamangConfig.PlayProbabilisticConfig config,
                                                       boolean mustRewardFreeWheel, boolean freeWheel){
        LamangSymbolDo[][] symbolArr = new LamangSymbolDo[LamangGameNormalPlay.GAME_INFO.getRow()][LamangGameNormalPlay.GAME_INFO.getColumn()];

        fillWheel(LamangGameNormalPlay.GAME_INFO, symbolArr, config, mustRewardFreeWheel);

        return WheelDo.<LamangSymbolDo>builder()
                .index(WHEEL_DEFAULT_INDEX)
                .freeFlag(freeWheel)
                .symbolArr(symbolArr)
                .build();
    }

    private WheelDo<LamangSymbolDo> generateFromPreWheel(LamangGameRecordDo preGameRecord,
                                                         LamangConfig.PlayProbabilisticConfig config,
                                                         boolean mustRewardFreeWheel,
                                                         boolean freeWheel){

        WheelDo<LamangSymbolDo> preWheel = preGameRecord.getWheel();
        RewardResultDo<LamangSymbolDo> preRewardResult = preGameRecord.getRewardResult();
        // 复制：元素数组
        LamangSymbolDo[][] symbolArr = copySymbolArr(preWheel.getSymbolArr());
        // 消除：中奖元素
        clearRewardSymbol(symbolArr, preRewardResult);

        // 如果有超级wild就全部重新生成
        if (hasSuperWild(symbolArr)) {
            // 生成一个 中间元素（1，1）为超级wild 2、3行的2-4列 添加占位符
            symbolArr = initSuperWildWheel();
            LamangConfig.PlayProbabilisticConfig superWild = lamangConfig.getProbabilisticConfig().getSupperWildRefreshProbabilisticConfig();
            // 用超级wild配置表填充wheel
            fillWheel(GAME_INFO, symbolArr, superWild, mustRewardFreeWheel);
        } else {
            //下落
            rebuildSymbolArr(symbolArr, GAME_INFO);
            //填充
            fillWheel(GAME_INFO, symbolArr, config, mustRewardFreeWheel);
            // 使用 占位符 覆盖bigWild的2-4列
            holdBigWild(symbolArr);
        }
        //todo 使用占位符

        return WheelDo.<LamangSymbolDo>builder()
                .index(preWheel.getIndex() + 1)
                .freeFlag(freeWheel)
                .symbolArr(symbolArr)
                .build();
    }

    private LamangSymbolDo[][] initSuperWildWheel() {
        LamangSymbolDo[][] newSymbolArr = new LamangSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];
        for (int row = SUPER_WILD_START_ROW_INDEX; row <= SUPER_WILD_END_ROW_INDEX; row++) {
            for (int column = SUPER_WILD_START_COLUMN_INDEX; column <= SUPER_WILD_END_COLUMN_INDEX; column++) {
                if (row == SUPER_WILD_START_ROW_INDEX && column == SUPER_WILD_START_COLUMN_INDEX) {
                    newSymbolArr[row][column] = LamangSymbolDo.buildFromSymbol(LAMANG_SUPER_WILD, false);
                } else {
                    newSymbolArr[row][column] = LamangSymbolDo.buildFromSymbol(LAMANG_SYMBOL_PLACEHOLDER, false);
                }
            }
        }
        return newSymbolArr;
    }

    /**
     * 根据前一条游戏记录
     */
    private void loopPlayOnPreGameRecord(GamePlayDo gamePlay,
                                         List<LamangGameRecordDo> gameRecordResult,
                                         AtomicLong totalRewardAmount,
                                         LamangGameRecordDo preGameRecord,
                                         LinkedList<FreeWheelHandleDo> freeWheelList,
                                         LamangConfig.PlayProbabilisticConfig config,
                                         boolean freeWheelFlag,
                                         boolean mustRewardFreeWheel) {

        //计算scatter+1免费轮
        if (freeWheelFlag) {
            int freeWheelCount = countAndReleaseScatterFreeWheel(preGameRecord.getWheel().getSymbolArr());
            addFreeWheelTimes(freeWheelList, freeWheelCount, preGameRecord.getId());
        }
        if (canContinuePlayOnPreWheel(gamePlay, preGameRecord.getRewardResult(), totalRewardAmount)) {
            WheelDo<LamangSymbolDo> wheel = generateFromPreWheel(
                    preGameRecord,
                    config,
                    mustRewardFreeWheel,
                    freeWheelFlag);
            //后续生成wheel根据前一轮的倍率
            RewardResultDo<LamangSymbolDo> rewardResult = lamangRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, preGameRecord.getRewardResult().getTotalMultiple()));
            long gameRecordId = generateGameRecordId();
            //todo 计算抽奖次数
            int drawTimes = countAndReleaseScatterDrawTimes(wheel.getSymbolArr());
            LamangGameRecordDo gameRecord = buildGameRecord(gameRecordId, preGameRecord.getId(),
                    gamePlay.getBetRecordId(), preGameRecord.getBatchId(), wheel, rewardResult, drawTimes);
            gameRecordResult.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            loopPlayOnPreGameRecord(gamePlay, gameRecordResult, totalRewardAmount, gameRecord, freeWheelList, config, freeWheelFlag, mustRewardFreeWheel);
        }else if (preGameRecord.getRewardResult().isHasRewardFreeWheel()) {
            if (!reachMaxMultiple(totalRewardAmount.get(), gamePlay.getBetAmount(), lamangConfig.getRewardMaxMultiple())) {
                addFreeWheelTimes(freeWheelList, preGameRecord.getRewardResult().getRewardFreeWheelTimes(), preGameRecord.getId());
            } else {
                // help gc
                freeWheelList.clear();
            }
        }
    }

    /**
     * 增加免费轮次数
     * @param freeWheelList
     * @param addTimes
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

    private LamangGameRecordDo buildGameRecord(Long id,
                                                Long parentId,
                                                Long betRecordId,
                                                Long batchId,
                                                WheelDo<LamangSymbolDo> wheel,
                                                RewardResultDo<LamangSymbolDo> rewardResult,
                                                int jackpotDrawTimes) {
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
                .jackpotDrawTimes(jackpotDrawTimes)
                .build();
    }

    /**
     * 生成首轮
     */
    private void generateFirstBatch(GamePlayDo gamePlay, List<LamangGameRecordDo> result, AtomicLong totalRewardAmount,
                                    LinkedList<FreeWheelHandleDo> freeWheelList, boolean mustRewardFreeWheel) {
        LamangConfig.PlayProbabilisticConfig firstBatchConfig = lamangConfig.getProbabilisticConfig().getCommonPlaySymbolProbabilisticConfig();

        WheelDo<LamangSymbolDo> wheel = generateFirstWheel(firstBatchConfig, mustRewardFreeWheel,false);
        //首轮不穿入倍率
        RewardResultDo<LamangSymbolDo> rewardResult = lamangRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, null));
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 组装-游戏记录
        long gameRecordId = generateGameRecordId();
        //todo 计算抽奖次数
        int drawTimes = countAndReleaseScatterDrawTimes(wheel.getSymbolArr());
        LamangGameRecordDo gameRecord = buildGameRecord(gameRecordId, RECORD_DEFAULT_PARENT_ID, gamePlay.getBetRecordId(),
                gameRecordId, wheel, rewardResult, drawTimes);
        result.add(gameRecord);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        loopPlayOnPreGameRecord(gamePlay, result, totalRewardAmount, gameRecord, freeWheelList, firstBatchConfig, false, mustRewardFreeWheel);
    }

    /**
     * 生成免费轮
     */
    private void generateFreeBatch(GamePlayDo gamePlay, List<LamangGameRecordDo> result, AtomicLong totalRewardAmount, LinkedList<FreeWheelHandleDo> freeWheelList) {
        while (!freeWheelList.isEmpty()) {
            FreeWheelHandleDo freeWheel = freeWheelList.removeFirst();
            LamangGameRecordDo parentGameRecord = result.stream().filter(item -> Objects.equals(freeWheel.getParentGameRecordId(), item.getId())).findFirst().orElse(null);
            AssertUtil.assertNotNull(parentGameRecord, ErrorCode.ERR_INTER);
            LamangConfig.PlayProbabilisticConfig freeWheelConfig = lamangConfig.getProbabilisticConfig().getFreePlaySymbolProbabilisticConfig();
            WheelDo<LamangSymbolDo> wheel = generateFirstWheel(freeWheelConfig, false, true);
            wheel.setFreeFlag(true);
            // 首轮不传入倍率
            RewardResultDo<LamangSymbolDo> rewardResult = lamangRewardHandler.handle(RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, null));
            AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
            // 组装-游戏记录
            long gameRecordId = generateGameRecordId();
            //todo 计算抽奖次数
            int drawTimes = countAndReleaseScatterDrawTimes(wheel.getSymbolArr());
            LamangGameRecordDo gameRecord = buildGameRecord(gameRecordId, freeWheel.getParentGameRecordId(),
                    gamePlay.getBetRecordId(), gameRecordId, wheel, rewardResult, drawTimes);
            result.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            if (CollectionUtils.isEmpty(parentGameRecord.getGameRecordIds())) {
                parentGameRecord.setGameRecordIds(new ArrayList<>());
            }
            parentGameRecord.getGameRecordIds().add(gameRecordId);
            loopPlayOnPreGameRecord(gamePlay, result, totalRewardAmount, gameRecord, freeWheelList, freeWheelConfig, true, false);
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



    private boolean generateSymbolGoldBorder(LamangConfig.PlayProbabilisticConfig playProbabilisticConfig, int row, int column) {
        List<BigDecimal> goldBoarderProbabilisticConfig = playProbabilisticConfig.getWheelGoldProbabilisticConfig();
        BigDecimal goldBoarderProbabilistic = goldBoarderProbabilisticConfig.get(column);

        AssertUtil.assertNotNull(goldBoarderProbabilistic, ErrorCode.ERR_INTER);
        int probabilisticVal = goldBoarderProbabilistic.multiply(new BigDecimal(100)).intValue();
        if (Objects.equals(probabilisticVal, 0)) {
            return false;
        } else {
            int randomVal = randomService.randomInt(1, CONST_10000);
            return randomVal > 0 && randomVal <= probabilisticVal;
        }
    }

    /**
     * 消除中奖元素 <br/>
     * **** 中奖元素参与多条中奖线，则只做一次消除<br/>
     * 1、scatter元素不做消除<br/>
     * 2、普通元素：<br/>
     * 2.1 带金边，随机变成 wild ｜ bigWild | superWild元素<br/>
     * 2.2 不带金边，直接消除<br/>
     *
     * @param symbolArr
     * @param rewardResult
     */
    public void clearRewardSymbol(LamangSymbolDo[][] symbolArr, RewardResultDo<LamangSymbolDo> rewardResult) {
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(rewardResult.getRewardLineDetailList(), ErrorCode.ERR_INTER);
        List<RewardSymbolDo<LamangSymbolDo>> rewardSymbolList = rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo<LamangSymbolDo>::getRewardSymbolList).flatMap(List::stream).collect(Collectors.toList());
        rewardSymbolList.parallelStream().forEach(rewardSymbol -> clearIndexRewardSymbol(symbolArr, rewardSymbol));
    }

    private void clearIndexRewardSymbol(LamangSymbolDo[][] symbolArr, RewardSymbolDo<LamangSymbolDo> rewardSymbol) {
        if (rewardSymbol.getSymbol().isScatterSymbol()) {
            // scatter 元素， 不做消除
        } else if (rewardSymbol.getSymbol().isWildSymbol() || rewardSymbol.getSymbol().isPlaceholderSymbol()) {
            // wild 和 占位符 元素,直接移除
            removeSymbol(symbolArr, rewardSymbol);
        }else {
            // 普通元素消除
            IndexDo indexDo = rewardSymbol.getIndex();
            LamangSymbolDo commonSymbol = symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()];
            if (commonSymbol.isSymbolGoldBorder()) {
                // 带金边普通元素，根据概率表 转换为Wild元素
                int randomInt = randomService.randomInt(1, Constants.CONST_1000000);
                SymbolDo wildSymbol = randomNormalSymbol(randomInt, GAME_INFO,
                        LamangProbabilisticUtil.buildWildProbabilisticMap(lamangConfig.getProbabilisticConfig().getWildTypeProbabilisticConfig()));
                LamangSymbolDo current = LamangSymbolDo.buildFromSymbol(wildSymbol, false);
                symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = current;
            } else {
                // 不带金边普通元素，直接移除
                removeSymbol(symbolArr, rewardSymbol);
            }
        }
    }

    /**
     * 移除元素
     *
     * @param symbolArr
     * @param rewardSymbol
     */
    public void removeSymbol(LamangSymbolDo[][] symbolArr, RewardSymbolDo<LamangSymbolDo> rewardSymbol) {
        IndexDo indexDo = rewardSymbol.getIndex();
        // 直接移除
        symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = null;
    }

    /**
     * 重建元素数组：（按列下移处理）
     * 将每一列的非空元素下移到最底部，类似重力效果
     * 
     * @param symbolArr 符号数组
     * @param gameInfoDo 游戏信息
     */
    public void rebuildSymbolArr(LamangSymbolDo[][] symbolArr, GameInfoDo gameInfoDo) {
        // 对每一列进行处理
        for (int column = ONE_COLUM_INDEX; column < gameInfoDo.getColumn(); column++) {
            // 先收集当前列所有非空元素
            List<LamangSymbolDo> nonNullSymbols = new ArrayList<>();
            for (int row = ONE_ROW_INDEX; row < gameInfoDo.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    nonNullSymbols.add(symbolArr[row][column]);
                }
            }
            
            // 清空当前列
            for (int row = ONE_ROW_INDEX; row < gameInfoDo.getRow(); row++) {
                symbolArr[row][column] = null;
            }
            
            // 从底部开始填充非空元素
            int currentRow = gameInfoDo.getRow() - 1;
            for (int i = nonNullSymbols.size() - 1; i >= 0; i--) {
                symbolArr[currentRow][column] = nonNullSymbols.get(i);
                currentRow--;
            }
        }
    }


    @Data
    @SuperBuilder
    private static class FreeWheelHandleDo {
        private long parentGameRecordId;
    }
}