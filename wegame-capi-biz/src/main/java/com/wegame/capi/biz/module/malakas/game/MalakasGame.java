package com.wegame.capi.biz.module.malakas.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.RewardHandlerBuilder;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.base.game.AbstractGame;
import com.wegame.capi.biz.module.base.reward.RewardHandler;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;
import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_SYMBOL_PLACEHOLDER;
import static com.wegame.capi.common.Constants.CONST_10000;
import static com.wegame.capi.common.Constants.CONST_1000000;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
@Component
public class MalakasGame extends AbstractGame<MalakasGameRecordDo> {

    private static final int MAX_REWARD_MULTIPLE = 2500;
    private static final int MAX_SCATTER_NUMBER_IN_REEL = 2;
    private static final int MAX_SCATTER_NUMBER_IN_WHEEL = 6;

    // ONE_COLUM_INDEX,THREE_COLUM_INDEX,FIVE_COLUM_INDEX顺序不可更改
    private static final List<Integer> SCATTER_COLUM_INDEX_LIST = Arrays.asList(
            ONE_COLUM_INDEX,
            THREE_COLUM_INDEX,
            FIVE_COLUM_INDEX);
    private static final Integer WHEEL_DEFAULT_INDEX = 0;
    @Autowired
    MalakasConfig malakasConfig;
    @Autowired
    RewardHandler malakasRewardHandler;

    @Override
    public GameInfoDo getGameInfo() {
        return MALAKAS_GAME_INFO;
    }

    @Override
    public List<MalakasGameRecordDo> play(GamePlayDo gamePlay) {
        // todo 购买投注
        // 正常投注
        return normalPlay(gamePlay);
    }

    public List<MalakasGameRecordDo> normalPlay(GamePlayDo gamePlay) {
        List<MalakasGameRecordDo> ret = new ArrayList<>();
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        Long totalRewardAmount = 0L;
        // 生成-游戏滚轮
        WheelDo<MalakasSymbolDo> wheel = generateNormalWheel(gameInfo, getWheelProbabilisticConfig(gamePlay));
        // 组装&返回-游戏记录列表
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel));
        MalakasGameRecordDo record = buildGameRecord(wheel, rewardResult);
        if (Objects.nonNull(record)) {
            ret.add(record);
            totalRewardAmount += rewardResult.getTotalRewardAmount();
        }
        while (canContinuePlay(rewardResult, totalRewardAmount)) {
            if (rewardResult.isHasRewardFreeSpin()) {
                // todo fix 生产免费轮
                wheel = generateNormalWheelFromPreWheel(gameInfo, wheel, getWheelProbabilisticConfig(gamePlay), rewardResult);
            } else {
                wheel = generateNormalWheelFromPreWheel(gameInfo, wheel, getWheelProbabilisticConfig(gamePlay), rewardResult);
            }
            rewardResult = malakasRewardHandler.handle(RewardHandlerBuilder.build(gameInfo, gamePlay, wheel));
            MalakasGameRecordDo malakasGameRecord = buildGameRecord(wheel, rewardResult);
            if (Objects.nonNull(malakasGameRecord)) {
                ret.add(malakasGameRecord);
                totalRewardAmount += rewardResult.getTotalRewardAmount();
            }
        }
        // 返回-游戏记录列表
        return ret;
    }

    private WheelDo<MalakasSymbolDo> generateNormalWheelFromPreWheel(GameInfoDo gameInfo,
                                                                     WheelDo<MalakasSymbolDo> preWheel,
                                                                     Map<String, List<BigDecimal>> probabilisticConfig,
                                                                     RewardResultDo<MalakasSymbolDo> rewardResult) {
        // 复制：元素数组
        MalakasSymbolDo[][] symbolArr = copySymbolArr(preWheel.getSymbolArr());
        // 消除：中奖元素
        symbolArr = clearRewardSymbol(symbolArr, rewardResult);
        // 重建：元素数组（非空元素按列下落处理：第1-6Reel非空元素向下移动，第7Reel非空元素向左移动）
        symbolArr = rebuildSymbolArr(symbolArr, gameInfo);
        // 生产：缺失元素(填补剩余元素)
        return generateCommonNormalWheel(gameInfo, symbolArr, probabilisticConfig, preWheel.getIndex() + 1, WHEEL_DEFAULT_INDEX);
    }

    /**
     * 重建元素数组：（非空元素按行（第一行）左移，按列下移处理）
     *
     * @param symbolArr
     * @return
     */
    private MalakasSymbolDo[][] rebuildSymbolArr(MalakasSymbolDo[][] symbolArr, GameInfoDo gameInfoDo) {
        // 处理第一行：按行处理(非空元素左移，第一列元素&最后一列元素为空)
        int currentColum = 1;
        for (int colum = 1; colum < gameInfoDo.getColumn() - 1; colum++) {
            if (Objects.nonNull(symbolArr[ONE_ROW_INDEX][colum])) {
                MalakasSymbolDo temp = symbolArr[ONE_ROW_INDEX][colum];
                symbolArr[ONE_ROW_INDEX][currentColum] = temp;
                if (!Objects.equals(currentColum, colum)) {
                    symbolArr[ONE_ROW_INDEX][colum] = null;
                }
            }
        }
        // 处理非第一行：按列处理（非空元素下移）
        for (int colum = ONE_COLUM_INDEX; colum < gameInfoDo.getColumn(); colum++) {
            int currentRow = gameInfoDo.getRow() - 1;
            for (int row = gameInfoDo.getRow() - 1; row > 0; row--) {
                if (Objects.nonNull(symbolArr[row][colum])) {
                    MalakasSymbolDo temp = symbolArr[row][colum];
                    symbolArr[currentRow][colum] = temp;
                    if (!Objects.equals(currentRow, row)) {
                        symbolArr[currentRow][colum] = null;
                    }
                }
            }
        }
        return symbolArr;
    }

    /**
     * 消除中奖元素 <br/>
     * **** 中奖元素参与多条中奖线，则只做一次消除<br/>
     * 1、scatter元素不做消除<br/>
     * 2、普通元素：<br/>
     *    2.1 带金边，元素变成wild元素<br/>
     *    2.2 不带金边，直接消除<br/>
     * 3、wild元素：消除次数减1，若减一后消除次数为0，则消除。 否则不消除<br/>
     *
     * @param symbolArr
     * @param rewardResult
     * @return
     */
    private MalakasSymbolDo[][] clearRewardSymbol(MalakasSymbolDo[][] symbolArr, RewardResultDo<MalakasSymbolDo> rewardResult) {
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(rewardResult.getRewardLineDetailList(), ErrorCode.ERR_INTER);
        List<RewardSymbolDo<MalakasSymbolDo>> rewardSymbolList = rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo<MalakasSymbolDo>::getRewardSymbolList).flatMap(List::stream).collect(Collectors.toList());
        for (RewardSymbolDo<MalakasSymbolDo> rewardSymbol : rewardSymbolList) {
            if (rewardSymbol.getSymbol().isScatterSymbol()) {
                // scatter 元素， 不做消除
                // do nothing;
            } else if (rewardSymbol.getSymbol().isWildSymbol()) {
                // wild 元素
                if (rewardSymbol.getSymbol().getSymbolClearTimes() > 1) {
                    // 消除次数大于1，则不做消除
                    // do nothing;
                } else {
                    // 消除次数小于1，则移除
                    removeRewardSymbol(symbolArr, rewardSymbol);
                }
            } else {
                // 普通元素，直接移除
                removeRewardSymbol(symbolArr, rewardSymbol);
            }
        }
        return symbolArr;
    }

    /**
     * 移除元素
     * @param symbolArr
     * @param rewardSymbol
     */
    public void removeRewardSymbol(MalakasSymbolDo[][] symbolArr, RewardSymbolDo<MalakasSymbolDo> rewardSymbol) {
        IndexDo indexDo = rewardSymbol.getIndex();
        if (Objects.equals(indexDo.getRowIndex(), ONE_ROW_INDEX)) {
            // 第一行元素：直接移除
            symbolArr[indexDo.getRowIndex()][indexDo.getColumIndex()] = null;
        } else {
            // 非第一行元素：按照元素大小进行移除
            int columSpanSiz = rewardSymbol.getSymbol().getSymbolSize().getColumSpan();
            for (int columSpan = 0; columSpan < columSpanSiz; columSpan++) {
                symbolArr[indexDo.getRowIndex() + columSpan][indexDo.getColumIndex()] = null;
            }
        }
    }

    public MalakasSymbolDo[][] copySymbolArr(MalakasSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        MalakasSymbolDo[][] newSymbolArr = new MalakasSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                newSymbolArr[i][j] = symbolArr[i][j].copy();
            }
        }
        return newSymbolArr;
    }

    /**
     * 正常滚轮生成：生成逻辑无任何限制
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

    private WheelDo<MalakasSymbolDo> generateCommonNormalWheel(GameInfoDo gameInfo,
                                                               MalakasSymbolDo[][] symbolArr,
                                                               Map<String, List<BigDecimal>> probabilisticConfig,
                                                               int wheelIndex,
                                                               int parentIndex) {
        // 生成整盘随机数(row*column)
        List<Integer> randomNumberList = randomService.randomIntList(1, CONST_1000000, gameInfo.getRow() * gameInfo.getColumn());
        int randomNumberIndex = 0;
        int scatterNumberInWheel = 0;
        for (int colum = ONE_COLUM_INDEX; colum < gameInfo.getColumn(); colum++) {
            // 一列只能包含两个scatter
            int scatterNumberInReel = 0;
            Map<String, Integer> columProbabilisticMap = buildColumProbabilisticMap(colum, probabilisticConfig);
            for (int row = ONE_ROW_INDEX; row < gameInfo.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][colum])) {
                    // 当前元素非空，则跳过
                    continue;
                }
                Integer randomNumber = randomNumberList.get(randomNumberIndex);
                if (scatterNumberInReel >= MAX_SCATTER_NUMBER_IN_REEL || scatterNumberInWheel >= MAX_SCATTER_NUMBER_IN_WHEEL || Objects.equals(row, ONE_ROW_INDEX)) {
                    // 第一行 || 列scatter达到上限 || 滚轮scatter达到上限，则生产非scatter元素
                    // 生成非scatter
                    SymbolDo symbolDo = randomNonScatterSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(row, getMaxRow(symbolArr, row, colum), colum, gameInfo);
                    placeholderSymbolHandle(symbolArr, row, colum, symbolSize);
                    // 构建元素金边
                    boolean symbolGoldBorder = buildSymbolGoldBorder(gameInfo, row, colum);
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbol(symbolDo, symbolSize, symbolGoldBorder);
                    symbolArr[row][colum] = malakasSymbolDo;
                } else {
                    // 随机生成
                    SymbolDo symbolDo = randomNormalSymbol(randomNumber, gameInfo, columProbabilisticMap);
                    SymbolSizeEnum symbolSize = generateSymbolSize(row, getMaxRow(symbolArr, row, colum), colum, gameInfo);
                    placeholderSymbolHandle(symbolArr, row, colum, symbolSize);
                    // 构建元素金边
                    boolean symbolGoldBorder = buildSymbolGoldBorder(gameInfo, row, colum);
                    MalakasSymbolDo malakasSymbolDo = MalakasSymbolDo.buildFromSymbol(symbolDo, symbolSize, symbolGoldBorder);
                    symbolArr[row][colum] = malakasSymbolDo;
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

    public int getMaxRow(MalakasSymbolDo[][] symbolArr, int currentRow, int currentColum) {
        int maxRow = currentRow;
        while (maxRow <= symbolArr.length && symbolArr[maxRow][currentColum] != null) {
            maxRow ++;
        }
        return maxRow;
    }

    private boolean canContinuePlay(RewardResultDo<MalakasSymbolDo> rewardResult, Long totalRewardAmount) {
        if (Objects.isNull(rewardResult)) {
            return false;
        }
        if (Objects.equals(rewardResult.getRewardType(), RewardTypeEnum.NONE)) {
            return false;
        }
        if (totalRewardAmount / rewardResult.getBetAmount() >= MAX_REWARD_MULTIPLE) {
            log.warn("reach max reward multiple of {} bet. totalRewardAmount: {}, betAmount: {}", MAX_REWARD_MULTIPLE, totalRewardAmount, rewardResult.getBetAmount());
            return false;
        }
        return true;
    }

    private SymbolSizeEnum generateSymbolSize(int currentRow, int maxRow, int currentColum, GameInfoDo gameInfo) {
        if (Objects.equals(currentRow, ONE_ROW_INDEX)
                || Objects.equals(currentColum, ONE_COLUM_INDEX)
                || Objects.equals(currentColum, gameInfo.getColumn() - 1)) {
            // 第一行，第一列，最后一列：只能为小元素
            return SymbolSizeEnum.S;
        }
        Map<String, List<BigDecimal>> symbolSizeProbabilisticConfig = malakasConfig.getProbabilisticConfig().getSymbolSizeProbabilisticConfig();
        Map<String, Integer> map = buildColumSymboleSizeProbabilisticMap(currentColum, maxRow - currentRow + 1, symbolSizeProbabilisticConfig);
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_INTER);
        return randomSymbolSize(map, randomService.randomInt(1, map.values()
                .stream()
                .reduce(0, Integer::sum)));
    }


    public Map<String, Integer> buildColumSymboleSizeProbabilisticMap(int currentColumNo,
                                                                      int maxColumSpan,
                                                                      Map<String, List<BigDecimal>> sizeProbabilisticConfigMap) {
        AssertUtil.assertTrue(MapUtils.isNotEmpty(sizeProbabilisticConfigMap), ErrorCode.ERR_INTER);
        Map<String, Integer> map = new HashMap<>();
        sizeProbabilisticConfigMap.entrySet().forEach(item -> {
            SymbolSizeEnum symbolSize = SymbolSizeEnum.valueOfCode(item.getKey());
            int val = (item.getValue().get(currentColumNo).multiply(new BigDecimal(Constants.CONST_10000))).intValue();
            if (val > 0 && maxColumSpan >= symbolSize.getColumSpan()) {
                map.put(item.getKey(), val);
            }
        });
        return map;
    }

    private void placeholderSymbolHandle(MalakasSymbolDo[][] symbolArr, int row, int colum, SymbolSizeEnum symbolSize) {
        switch (symbolSize) {
            case S:
                break;
            case M:
                symbolArr[row + 1][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                break;
            case L:
                symbolArr[row + 1][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                symbolArr[row + 2][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                break;
            case XL:
                symbolArr[row + 1][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                symbolArr[row + 2][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                symbolArr[row + 3][colum] = MALAKAS_SYMBOL_PLACEHOLDER;
                break;
        }
    }

    private Map<String, List<BigDecimal>> getWheelProbabilisticConfig(GamePlayDo gamePlay) {
        if (gamePlay.isFreeSpinsFlag()) {
            return malakasConfig.getProbabilisticConfig().getFreePlayWheelProbabilisticConfig();
        } else {
            switch (gamePlay.getBetSubType()) {
                case NORMAL_BET:
                case BUY_BET:
                    return malakasConfig.getProbabilisticConfig().getCommonPlayWheelProbabilisticConfig();
                case FREE_SPINS_NORMAL_BET:
                case FREE_SPINS_BUY_BET:
                    return malakasConfig.getProbabilisticConfig().getFreePlayWheelProbabilisticConfig();
                default:
                    throw new BusinessException(ErrorCode.ERR_PARAM, "NOT SUPPORTED");
            }
        }
    }

    private MalakasGameRecordDo buildGameRecord(WheelDo<MalakasSymbolDo> wheel, RewardResultDo<MalakasSymbolDo> rewardResult) {
        return MalakasGameRecordDo.builder()
                .index(wheel.getIndex())
                .wheel(wheel)
                .rewardResult(rewardResult)
                .build();
    }

    private boolean buildSymbolGoldBorder(GameInfoDo gameInfo, int row, int colum) {
        List<BigDecimal> goldBoarderProbabilisticConfig = malakasConfig.getProbabilisticConfig().getGoldBoarderProbabilisticConfig();
        BigDecimal goldBoarderProbabilistic = null;
        if (Objects.equals(row, ONE_ROW_INDEX)) {
            goldBoarderProbabilistic = goldBoarderProbabilisticConfig.get(gameInfo.getColumn() - 1);
        } else {
            goldBoarderProbabilistic = goldBoarderProbabilisticConfig.get(colum);
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
}
