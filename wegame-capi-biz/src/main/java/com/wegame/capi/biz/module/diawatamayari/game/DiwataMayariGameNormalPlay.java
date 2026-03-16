package com.wegame.capi.biz.module.diawatamayari.game;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.DiwataMayariGameRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.*;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariSymbolUtil;
import com.wegame.capi.biz.module.diawatamayari.reward.DiwataMayariRewardHandler;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.utils.TtlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component("diwataMayariGameNormalPlay")
public class DiwataMayariGameNormalPlay extends AbstractGame<DiwataMayariGameRecordDo> {
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    private static final Long RECORD_DEFAULT_PARENT_ID = 0L;
    private static final GameInfoDo GAME_INFO = DiwataMayariGameHelper.DIWATA_MAYARI_GAME_INFO;
    private static final int SWORD_STICK = 3;
    public static final int CONST_1 = 1;


    @Autowired
    DiwataMayariConfig diwataMayariConfig;
    @Autowired
    DiwataMayariRewardHandler diwataMayariRewardHandler;

    @Override
    public List<DiwataMayariGameRecordDo> play(BetDo betDo, GamePlayDo gamePlay) {
        List<DiwataMayariGameRecordDo> ret = new ArrayList<>();
        // 生成-游戏滚轮
        WheelDo<DiwataMayariSymbolDo> wheel;
        long gameRecordId = generateGameRecordId();
        switch (gamePlay.getBetType()) {
            // 正常投注
            case NORMAL_BET:
                wheel = generateNormalWheel(false);
                break;
            // 购买投注
            case BUY_BET:
                wheel = null;
                while (wheel == null) {
                    wheel = generateRewardFreeWheel(false);
                }
                break;
            default:
                throw new BusinessException(ErrorCode.ERR_INTER, "NOT SUPPORT");
        }
        // 判奖-根据游戏滚轮和中奖线算法判断
        AtomicLong totalRewardAmount = new AtomicLong(0L);
        // 计算中奖线
        RewardHandleDo<DiwataMayariSymbolDo> buildRewardReq = RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, BigDecimal.ZERO, totalRewardAmount.get());
        RewardResultDo<DiwataMayariSymbolDo> rewardResult = diwataMayariRewardHandler.handle(buildRewardReq);
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        // 组装-游戏记录
        DiwataMayariGameRecordDo gameRecord = buildGameRecord(gameRecordId, RECORD_DEFAULT_PARENT_ID, gamePlay.getBetRecordId(), gameRecordId, wheel, rewardResult);
        ret.add(gameRecord);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        // 判断是否中奖或达到最大倍数
        if (rewardResult.getAccumulateReachMaxMultiple()) {
            return this.handlerResultDo(ret);
        }
        // 判断首轮是否中奖
        // 定义一个队列 用于临时存储免费游戏
        Queue<Long> freeWheelList = new LinkedList<>();
        if (this.canContinuePlayOnPreWheel(rewardResult, totalRewardAmount)) {
            // 处理消除轮
            loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList);
        } else {
            if (rewardResult.isHasRewardFreeWheel()) {
                // 中免费轮
                for (int times = 1; times <= rewardResult.getRewardFreeWheelTimes(); times++) {
                    freeWheelList.offer(gameRecordId);
                }
            }
        }
        //  处理免费游戏
        if (CollectionUtils.isNotEmpty(freeWheelList)) {
            handleFreeWheelGamePlay(gamePlay, ret, totalRewardAmount, freeWheelList);
        }

        return this.handlerResultDo(ret);
    }

    private List<DiwataMayariGameRecordDo> handlerResultDo(List<DiwataMayariGameRecordDo> ret) {
        if (CollectionUtils.isNotEmpty(ret)) {
            ret.get(ret.size() - 1).setLastGameFlag(true);
        }
        DiwataMayariSymbolUtil.postProcessPlaceholderSymbol(ret);
        return ret;
    }

    /**
     * 生成首轮：生成逻辑无任何限制
     *
     * @param freeWheelFlag 是否免费轮
     * @return 滚轮
     */
    private WheelDo<DiwataMayariSymbolDo> generateNormalWheel(boolean freeWheelFlag) {
        // 设置占位元素
        DiwataMayariSymbolDo[][] symbolArr = getPlaceholderElements();
        // 生成首轮
        return generateCommonNormalWheel(
                symbolArr,
                true,
                WHEEL_DEFAULT_INDEX,
                freeWheelFlag);
    }

    /**
     * 设置占位元素
     * 第一列的最后两行，最后一列的最后两行，第二列的最后一行，倒数第二列的最后一行
     */
    private DiwataMayariSymbolDo[][] getPlaceholderElements() {
        DiwataMayariSymbolDo[][] symbolArr = new DiwataMayariSymbolDo[GAME_INFO.getRow()][GAME_INFO.getColumn()];
        int lastRow = GAME_INFO.getRow() - 1;
        int secondLastRow = GAME_INFO.getRow() - 2;
        int lastCol = GAME_INFO.getColumn() - 1;
        int secondLastCol = GAME_INFO.getColumn() - 2;

        // 第一列的最后两行
        symbolArr[lastRow][ONE_COLUM_INDEX] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        symbolArr[secondLastRow][ONE_COLUM_INDEX] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;

        // 第二列的最后一行
        symbolArr[lastRow][TWO_COLUM_INDEX] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;

        // 倒数第二列的最后一行
        symbolArr[lastRow][secondLastCol] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;

        // 最后一列的最后两行
        symbolArr[lastRow][lastCol] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        symbolArr[secondLastRow][lastCol] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
        return symbolArr;
    }

    /**
     * 生成正常滚轮：生成逻辑无任何限制
     *
     * @param symbolArr  游戏轮盘
     * @param first      是否首轮
     * @param wheelIndex 滚轮序号
     * @param freeFlag   收否免费轮
     * @return
     */

    private WheelDo<DiwataMayariSymbolDo> generateCommonNormalWheel(DiwataMayariSymbolDo[][] symbolArr,
                                                                    boolean first,
                                                                    int wheelIndex,
                                                                    boolean freeFlag) {
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(CONST_1, CONST_1000000, GAME_INFO.getRow() * GAME_INFO.getColumn());
        List<List<Integer>> partitionRandom = Lists.partition(randomNumberList, GAME_INFO.getRow());
        TreeMap<String, List<BigDecimal>> commonPlayProbabilisticConfig = this.getStringListTreeMap(first, freeFlag);
        //  生成-行列概率
        for (int column = ONE_COLUM_INDEX; column < GAME_INFO.getColumn(); column++) {
            TreeMap<String, Integer> columProbabilisticMap = super.buildColumProbabilisticMap(column, commonPlayProbabilisticConfig);
            this.buildRowSymbol(symbolArr, column, columProbabilisticMap, partitionRandom.get(column));
        }
        // 返回：滚轮
        return WheelDo.<DiwataMayariSymbolDo>builder()
                .index(wheelIndex)
                .freeFlag(freeFlag)
                .symbolArr(symbolArr)
                .build();
    }

    /**
     * 获取-行列概率
     *
     * @param first
     * @param freeFlag
     * @return
     */
    private TreeMap<String, List<BigDecimal>> getStringListTreeMap(boolean first, boolean freeFlag) {
        TreeMap<String, List<BigDecimal>> commonPlayProbabilisticConfig;
        if (first) {
            if (freeFlag) {
                commonPlayProbabilisticConfig = diwataMayariConfig.getProbabilisticConfig().getFreePlayFirstSpinProbabilisticConfig();
            } else {
                commonPlayProbabilisticConfig = diwataMayariConfig.getProbabilisticConfig().getCommonPlayFirstSpinProbabilisticConfig();
            }
        } else {
            if (freeFlag) {
                commonPlayProbabilisticConfig = diwataMayariConfig.getProbabilisticConfig().getFreePlayRoundProbabilisticConfig();
            } else {
                commonPlayProbabilisticConfig = diwataMayariConfig.getProbabilisticConfig().getCommonPlayRoundProbabilisticConfig();
            }
        }
        return commonPlayProbabilisticConfig;
    }

    /**
     * 生成中奖免费轮滚轮: 需要满足3-6个Scatter
     *
     * @return 滚轮
     */
    private WheelDo<DiwataMayariSymbolDo> generateRewardFreeWheel(boolean freeWheelFlag) {
        DiwataMayariSymbolDo[][] symbolArr = getPlaceholderElements();

        WheelDo<DiwataMayariSymbolDo> wheel = generateCommonNormalWheel(symbolArr, true, WHEEL_DEFAULT_INDEX, freeWheelFlag);
        // 计算scatter数量
        int scatterSymbolCount = DiwataMayariSymbolUtil.countIndexSymbol(wheel.getSymbolArr(), SymbolEnum.Scatter);
        if (scatterSymbolCount >= 3) {
            return wheel;
        }
        int randomScatterSymbolCount = randomService.randomInt(3, 6);
        int needAddCount = randomScatterSymbolCount - scatterSymbolCount;
        if (needAddCount <= 0) {
            return wheel;
        }

        // 提取有效坐标集合
        List<IndexDo> indexList = new ArrayList<>();
        for (int row = ONE_ROW_INDEX; row < GAME_INFO.getRow(); row++) {
            for (int column = ONE_COLUM_INDEX; column < GAME_INFO.getColumn(); column++) {
                DiwataMayariSymbolDo symbol = wheel.getSymbolArr()[row][column];
                if (Objects.nonNull(symbol)) {
                    if (!symbol.isPlaceholderSymbol() && !symbol.isScatterSymbol() && !symbol.isWildSymbol()) {
                        indexList.add(IndexFactory.buildDo(row, column));
                    }
                }
            }
        }
        if (indexList.size() < needAddCount) {
            return null;
        }
        // indexList 中随机取 needAddCount 个坐标 替换为scatter
        Collections.shuffle(indexList, new Random());
        for (int i = 0; i < needAddCount; i++) {
            IndexDo indexDo = indexList.get(i);
            DiwataMayariSymbolDo[][] symbolArr1 = wheel.getSymbolArr();
            symbolArr1[indexDo.getRowIndex()][indexDo.getColumnIndex()] = DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariGameHelper.DIWATA_MAYARI_SCATTER);
        }
        return wheel;
    }


    public SymbolDo randomNonWildSymbol(Integer randomNumber, TreeMap<String, Integer> columnProbabilisticMap) {
        Integer tempRandomNumber = randomNumber;
        while (true) {
            SymbolEnum symbol = randomOneSymbol(columnProbabilisticMap, tempRandomNumber);
            SymbolDo symbolDo = GAME_INFO.getSymbolById(symbol.getId());
            DiwataMayariSymbolDo diwataMayariSymbolDo = DiwataMayariSymbolDo.buildFromSymbol(symbolDo);

            if (!diwataMayariSymbolDo.isSword() && !diwataMayariSymbolDo.isStick()) {
                return symbolDo;
            }
            tempRandomNumber = randomService.randomInt(1, Constants.CONST_1000000);
        }
    }

    /**
     * 构建行元素
     *
     * @param symbolArr
     * @param column
     */
    private void buildRowSymbol(DiwataMayariSymbolDo[][] symbolArr, int column, TreeMap<String, Integer> columProbabilisticMap, List<Integer> randomNumberList) {
        // 判断这一列是否有scatter元素
        boolean isScatter = this.isScatter(symbolArr, column);
        for (int row = ONE_ROW_INDEX; row < GAME_INFO.getRow(); row++) {
            if (Objects.nonNull(symbolArr[row][column])) {
                continue;
            }
            Integer randomNumber = randomNumberList.get(row);
            // 随机生成滚轮游戏
            SymbolDo symbolDo = this.randomNormalSymbol(randomNumber, GAME_INFO, columProbabilisticMap);
            DiwataMayariSymbolDo diwataMayariSymbolDo = DiwataMayariSymbolDo.buildFromSymbol(symbolDo);
            // 元素赋值
            symbolArr[row][column] = diwataMayariSymbolDo;
            if (diwataMayariSymbolDo.isScatterSymbol()) {
                //scatter 元素
                isScatter = true;
                continue;
            }
            // 棒子元素
            if (diwataMayariSymbolDo.isStick()) {
                boolean converted = this.convertStick(symbolArr, column, row);
                if (!converted) {
                    // 重新生成非 棒子元素
                    SymbolDo symbolDoNoSword = this.randomNonWildSymbol(randomNumber, columProbabilisticMap);
                    symbolArr[row][column] = DiwataMayariSymbolDo.buildFromSymbol(symbolDoNoSword);
                } else {
                    symbolArr[row][column].setMain(true);
                }
                continue;
            }
            // 大宝剑元素
            if (diwataMayariSymbolDo.isSword()) {
                if (!isScatter) {
                    this.convertSword(symbolArr, column);
                    symbolArr[row][column].setMain(true);
                    break;
                } else {
                    // 重新生成非 大宝剑元素
                    SymbolDo symbolDoNoStick = this.randomNonWildSymbol(randomNumber, columProbabilisticMap);
                    symbolArr[row][column] = DiwataMayariSymbolDo.buildFromSymbol(symbolDoNoStick);
                }
            }
        }
    }

    private boolean isScatter(DiwataMayariSymbolDo[][] symbolArr, int column) {
        for (int row = ONE_ROW_INDEX; row < GAME_INFO.getRow(); row++) {
            DiwataMayariSymbolDo diwataMayariSymbolDo = symbolArr[row][column];
            if (diwataMayariSymbolDo != null && diwataMayariSymbolDo.isScatterSymbol()) {
                return true;
            }
        }
        return false;
    }


    /**
     * 将colum 中所有元素转换为wild
     *
     * @param symbolArr
     * @param column
     */
    private void convertSword(DiwataMayariSymbolDo[][] symbolArr, int column) {
        for (int row = ONE_ROW_INDEX; row < GAME_INFO.getRow(); row++) {
            if (symbolArr[row][column] == null) {
                symbolArr[row][column] = DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariGameHelper.DIWATA_MAYARI_WILD_SWORD);
            } else {
                DiwataMayariSymbolDo diwataMayariSymbolDo = symbolArr[row][column];
                // main 元素 && 占位元素不处理
                if (diwataMayariSymbolDo.isPlaceholderSymbol() || diwataMayariSymbolDo.isMainSymbol()) {
                    continue;
                } else if (diwataMayariSymbolDo.isStick()) {
                    // 如果已经是大宝剑元素,做特殊标记
                    symbolArr[row][column] = DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariGameHelper.DIWATA_MAYARI_WILD_COVER);
                } else {
                    symbolArr[row][column] = DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariGameHelper.DIWATA_MAYARI_WILD_SWORD);
                }
            }
        }
    }

    /**
     * 棒子周围元素生成
     * false:极限情况,只有3个位置,并且其中几个是scatter
     *
     * @param symbolArr
     * @param column
     * @param row
     */
    private boolean convertStick(DiwataMayariSymbolDo[][] symbolArr, int column, int row) {
        List<int[]> randomSurroundingElements = this.getRandomSurroundingElements(symbolArr, column, row);
        Map<int[], DiwataMayariSymbolDo> replaceMap = new HashMap<>();
        for (int[] pos : randomSurroundingElements) {
            if (replaceMap.size() >= SWORD_STICK) {
                break;
            }
            int newRow = pos[0];
            int newColumn = pos[1];
            DiwataMayariSymbolDo diwataMayariSymbolDo = symbolArr[newRow][newColumn];
            if (diwataMayariSymbolDo == null || diwataMayariSymbolDo.isCommonSymbol()) {
                replaceMap.put(pos, DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariGameHelper.DIWATA_MAYARI_WILD_STICK)));
                continue;
            }
            // 如果是main元素，则不覆盖
            if (diwataMayariSymbolDo.isMainSymbol()) {
                continue;
            }
            if (diwataMayariSymbolDo.isSword()) {
                // 如果已经是宝剑元素
                replaceMap.put(pos, DiwataMayariSymbolDo.buildFromSymbol(DiwataMayariGameHelper.DIWATA_MAYARI_WILD_COVER));
            }
        }
        if (replaceMap.size() < SWORD_STICK) {
            return false;
        }
        // 替换元素
        replaceMap.forEach((key, value) -> symbolArr[key[0]][key[1]] = value);
        return true;
    }

    /**
     * 获取周边随机元素坐标,排除自己和占位元素，且不能在第一列
     *
     * @param symbolArr 符号数组
     * @param column    列索引
     * @param row       行索引
     * @return 周边有效位置列表
     */
    private List<int[]> getRandomSurroundingElements(DiwataMayariSymbolDo[][] symbolArr, int column, int row) {
        // 收集所有有效位置(上下左右、对角线)，排除中心位置和第一列
        List<int[]> positions = new ArrayList<>();

        // 遍历3x3区域
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = column - 1; j <= column + 1; j++) {
                // 排除中心位置
                if (i == row && j == column) {
                    continue;
                }
                // 排除第一列
                if (j == 0) {
                    continue;
                }
                // 检查边界
                if (i >= 0 && i < GAME_INFO.getRow() && j >= 0 && j < GAME_INFO.getColumn()) {
                    // 排除占位元素
                    DiwataMayariSymbolDo symbol = symbolArr[i][j];
                    if (symbol == null || !symbol.isPlaceholderSymbol()) {
                        positions.add(new int[]{i, j});
                    }
                }
            }
        }

        // 打乱顺序
        Collections.shuffle(positions, new Random());
        return positions;
    }

    /**
     * 处理免费轮
     *
     * @param gamePlay
     * @param ret
     * @param totalRewardAmount
     * @param freeWheelList
     */
    private void handleFreeWheelGamePlay(GamePlayDo gamePlay,
                                         List<DiwataMayariGameRecordDo> ret,
                                         AtomicLong totalRewardAmount,
                                         Queue<Long> freeWheelList) {
        BigDecimal totalMultipleAmount = new BigDecimal("0");
        boolean maxTotalMultiple = false;
        while (!freeWheelList.isEmpty() && !maxTotalMultiple) {
            long reeWheel = freeWheelList.poll();
            // 获取免费轮的父投注记录
            DiwataMayariGameRecordDo parentGameRecord = ret.stream().filter(item -> Objects.equals(reeWheel, item.getId())).findFirst().orElse(null);
            AssertUtil.assertNotNull(parentGameRecord, ErrorCode.ERR_INTER);
            // 生成免费轮首轮元素
            WheelDo<DiwataMayariSymbolDo> firstWheelDo = this.generateNormalWheel(true);
            firstWheelDo.setFreeFlag(true);
            // 判奖-根据游戏滚轮和中奖线算法判断
            RewardHandleDo<DiwataMayariSymbolDo> buildRewardReq = RewardHandlerBuilder.build(GAME_INFO, gamePlay, firstWheelDo, totalMultipleAmount, totalRewardAmount.get());
            RewardResultDo<DiwataMayariSymbolDo> rewardResult = diwataMayariRewardHandler.handle(buildRewardReq);
            AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
            // 组装-游戏记录
            long gameRecordId = generateGameRecordId();
            DiwataMayariGameRecordDo gameRecord = this.buildGameRecord(gameRecordId, reeWheel, gamePlay.getBetRecordId(), gameRecordId, firstWheelDo, rewardResult);
            ret.add(gameRecord);
            totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
            if (CollectionUtils.isEmpty(parentGameRecord.getGameRecordIds())) {
                parentGameRecord.setGameRecordIds(new ArrayList<>());
            }
            parentGameRecord.getGameRecordIds().add(gameRecordId);
            // 判断是否中奖或达到最大倍数
            if (rewardResult.getAccumulateReachMaxMultiple()) {
                return;
            }
            // 判断是否中奖
            if (this.canContinuePlayOnPreWheel(rewardResult, totalRewardAmount)) {
                // 处理当前消除
                maxTotalMultiple = loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList);
            } else {
                // 未中奖计算是否有免费轮
                if (rewardResult.isHasRewardFreeWheel()) {
                    // 中免费轮
                    for (int times = 1; times <= rewardResult.getRewardFreeWheelTimes(); times++) {
                        freeWheelList.offer(gameRecordId);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(ret)) {
                totalMultipleAmount = ret.get(ret.size() - 1).getRewardResult().getTotalMultiple();
            }
        }
    }

    /**
     * 计算所有消除滚轮结果(一回合,消除后未中奖或达到最大倍数就退出)
     * <p>
     * true:达到最大中奖倍数
     * false:未达到最大中奖倍数
     */
    private boolean loopPlayOnPreGameRecord(GamePlayDo gamePlay,
                                            List<DiwataMayariGameRecordDo> ret,
                                            AtomicLong totalRewardAmount,
                                            DiwataMayariGameRecordDo preGameRecord,
                                            Queue<Long> freeWheelList) {

        // 根据上一轮滚轮生成新的滚轮
        // 上一轮中奖元素
        List<RewardSymbolDo<DiwataMayariSymbolDo>> rewardSymbolList = preGameRecord.getRewardResult().getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo<DiwataMayariSymbolDo>::getRewardSymbolList).flatMap(List::stream).collect(Collectors.toList());
        // 生成新的滚轮
        WheelDo<DiwataMayariSymbolDo> wheel = this.generateNormalWheelFromPreWheel(preGameRecord.getWheel(), rewardSymbolList, preGameRecord.getFreeWheelFlag());
        // 计算中奖结果
        RewardHandleDo<DiwataMayariSymbolDo> buildRewardReq = RewardHandlerBuilder.build(GAME_INFO, gamePlay, wheel, preGameRecord.getRewardResult().getTotalMultiple(), totalRewardAmount.get());
        RewardResultDo<DiwataMayariSymbolDo> rewardResult = diwataMayariRewardHandler.handle(buildRewardReq);
        // 构建投注记录
        long gameRecordId = generateGameRecordId();
        DiwataMayariGameRecordDo gameRecord = this.buildGameRecord(gameRecordId, preGameRecord.getId(), gamePlay.getBetRecordId(), preGameRecord.getBatchId(), wheel, rewardResult);
        ret.add(gameRecord);
        totalRewardAmount.addAndGet(rewardResult.getTotalRewardAmount());
        // 判断是否中奖或达到最大倍数
        if (rewardResult.getAccumulateReachMaxMultiple()) {
            return true;
        }
        if (canContinuePlayOnPreWheel(rewardResult, totalRewardAmount)) {
            // 中奖之后继续消除
            return loopPlayOnPreGameRecord(gamePlay, ret, totalRewardAmount, gameRecord, freeWheelList);
        } else {
            // 未中奖计算是否有免费轮
            if (rewardResult.isHasRewardFreeWheel()) {
                // 中免费轮
                for (int times = 1; times <= rewardResult.getRewardFreeWheelTimes(); times++) {
                    freeWheelList.offer(gameRecordId);
                }
            }
        }
        return false;
    }

    /**
     * 上次滚轮重新生成新的滚轮
     * preWheel :  上次滚轮
     * preRewardResult :  上次中奖结果
     * freeWheelFlag : 上次是否命中免费轮
     *
     * @return 新的滚轮
     */
    private WheelDo<DiwataMayariSymbolDo> generateNormalWheelFromPreWheel(WheelDo<DiwataMayariSymbolDo> preWheel,
                                                                          List<RewardSymbolDo<DiwataMayariSymbolDo>> preRewardResult,
                                                                          boolean freeWheelFlag) {
        // 复制：元素数组
        DiwataMayariSymbolDo[][] symbolArr = DiwataMayariSymbolUtil.copySymbolArr(preWheel.getSymbolArr());
        // 消除：中奖元素
        DiwataMayariSymbolUtil.clearRewardSymbol(symbolArr, preRewardResult);
        // 重建：元素数组（非空元素按列下落处理）
        DiwataMayariSymbolUtil.rebuildSymbolArr(symbolArr);
        // 生产：缺失元素(填补剩余元素)
        return generateCommonNormalWheel(
                symbolArr,
                false,
                preWheel.getIndex() + 1,
                freeWheelFlag);
    }

    // 判断是否中奖
    private boolean canContinuePlayOnPreWheel(RewardResultDo<DiwataMayariSymbolDo> rewardResult, AtomicLong totalRewardAmount) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return false;
        }

        return CollectionUtils.isNotEmpty(rewardResult.getRewardLineDetailList());
    }

    private DiwataMayariGameRecordDo buildGameRecord(Long id,
                                                     Long parentId,
                                                     Long betRecordId,
                                                     Long batchId,
                                                     WheelDo<DiwataMayariSymbolDo> wheel,
                                                     RewardResultDo<DiwataMayariSymbolDo> rewardResult) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        return DiwataMayariGameRecordDo.builder()
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

}