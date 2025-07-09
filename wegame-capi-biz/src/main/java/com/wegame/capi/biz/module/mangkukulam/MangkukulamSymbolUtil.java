package com.wegame.capi.biz.module.mangkukulam;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexFactory;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper.*;
import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
public class MangkukulamSymbolUtil {


    public static MangkukulamSymbolDo[][] copySymbolArrWithPlaceHolder(MangkukulamSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        MangkukulamSymbolDo[][] newSymbolArr = new MangkukulamSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = MANGKUKULAM_SYMBOL_PLACEHOLDER;
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }

    public static int countScatterInWheel(MangkukulamSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        int scatterCount = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.nonNull(symbolArr[i][j]) && symbolArr[i][j].isScatterSymbol()) {
                    scatterCount++;
                }
            }
        }
        return scatterCount;
    }

    public static int countFireBorderInWheel(MangkukulamSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        int scatterCount = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.nonNull(symbolArr[i][j]) && symbolArr[i][j].isSymbolFireBorder()) {
                    scatterCount++;
                }
            }
        }
        return scatterCount;
    }

    public static void postProcessPlaceholderSymbol(List<MangkukulamGameRecordDo> gameRecordList) {
        if (CollectionUtils.isEmpty(gameRecordList)) {
            return;
        }
        long startTime = System.currentTimeMillis();
        gameRecordList.parallelStream().forEach(gameRecord -> {
            MangkukulamSymbolDo[][] symbolArr = gameRecord.getWheel().getSymbolArr();
            for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
                for (int column = ONE_COLUM_INDEX; column < symbolArr[row].length; column++) {
                    if (Objects.nonNull(symbolArr[row][column])) {
                        if (symbolArr[row][column].isPlaceholderSymbol()) {
                            symbolArr[row][column] = null;
                        }
                    }
                }
            }
        });
        long endTime = System.currentTimeMillis();
        log.info("postPlaceholderSymbol cost time: {} ms", (endTime - startTime));
    }

    public static MangkukulamSymbolDo[][] copySymbolArr(MangkukulamSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        MangkukulamSymbolDo[][] newSymbolArr = new MangkukulamSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = null;
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }

    public static List<RewardSymbolDo<MangkukulamSymbolDo>> getRewardSymbolDoList(RewardResultDo<MangkukulamSymbolDo> rewardResult) {
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(rewardResult.getRewardLineDetailList(), ErrorCode.ERR_INTER);
        return rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo::getRewardSymbolList).flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * 移除specialWild元素 : 移除该列所有元素
     */
    public static void removeSpecialWild(MangkukulamSymbolDo[][] symbolArr, RewardSymbolDo<MangkukulamSymbolDo> rewardSymbol) {
        IndexDo indexDo = rewardSymbol.getIndex();
        int column = indexDo.getColumnIndex();
        for (int i = 0; i < symbolArr.length; i++) {
            symbolArr[i][column] = null;
        }
    }

    /**
     * 消除元素
     */
    public static void clearIndexRewardSymbol(MangkukulamSymbolDo[][] symbolArr, RewardSymbolDo<MangkukulamSymbolDo> rewardSymbol) {
        MangkukulamSymbolDo currentSymbol = rewardSymbol.getSymbol();
        if (currentSymbol.isScatterSymbol()) {
            // scatter 元素， 不做消除
        } else if (currentSymbol.isSpecialWild()) {
            // 特殊百搭 消除次数> 1 减少消除次数。 <=1 移除该列所有元素
            if (currentSymbol.getRemainClearTimes() > 1) {
                // 消除次数大于1，则不做消除, 消除次数-1
                IndexDo indexDo = rewardSymbol.getIndex();
                MangkukulamSymbolDo wildSymbol = symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()];
                wildSymbol.setRemainClearTimes(currentSymbol.getRemainClearTimes() - 1);
            } else {
                // 消除次数小于等于1，则移除该列元素
                removeSpecialWild(symbolArr, rewardSymbol);
            }
        } else if (currentSymbol.isWildSymbol()) {
            // 普通wild 直接移除
            removeSymbol(symbolArr, rewardSymbol);
        } else {
            // 普通元素消除
            IndexDo indexDo = rewardSymbol.getIndex();
            MangkukulamSymbolDo commonSymbol = symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()];
            // 带火框的普通元素消除，变成带火框的wild
            if (commonSymbol.isSymbolFireBorder()) {
                MangkukulamSymbolDo current = MangkukulamSymbolDo.buildFromSymbol(MANGKUKULAM_Wild, true);
                symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = current;
            } else {
                // 不带金边普通元素，直接移除
                removeSymbol(symbolArr, rewardSymbol);
            }
        }
    }

    /**
     * 移除元素
     */
    public static void removeSymbol(MangkukulamSymbolDo[][] symbolArr, RewardSymbolDo<MangkukulamSymbolDo> rewardSymbol) {
        IndexDo indexDo = rewardSymbol.getIndex();
        // 直接移除
        symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = null;
    }

    /**
     * 消除中奖元素
     * return 带火框的wild坐标
     */
    public static List<IndexDo> clearRewardSymbol(MangkukulamSymbolDo[][] symbolArr, RewardResultDo<MangkukulamSymbolDo> rewardResult) {
        List<RewardSymbolDo<MangkukulamSymbolDo>> rewardSymbolDoList = getRewardSymbolDoList(rewardResult);
        // 去重map
        Map<IndexDo, RewardSymbolDo<MangkukulamSymbolDo>> map = rewardSymbolDoList.stream()
                .collect(Collectors.toMap(RewardSymbolDo::getIndex,
                        r -> r,
                        (a, b) -> a));
        List<RewardSymbolDo<MangkukulamSymbolDo>> distinctList = new ArrayList<>(map.values());
        if (CollectionUtils.isNotEmpty(distinctList)) {
            distinctList.parallelStream().forEach(rewardSymbol -> clearIndexRewardSymbol(symbolArr, rewardSymbol));
        }
        return distinctList.stream()
                .filter(r -> (r.getSymbol().isNormalWild() && r.getSymbol().isSymbolFireBorder()))
                .map(RewardSymbolDo::getIndex)
                .collect(Collectors.toList());
    }

    /**
     * 下落
     * 将每一列的非空元素下移到最底部，类似重力效果
     *
     * @param symbolArr 符号数组
     */
    public static void fallSymbols(MangkukulamSymbolDo[][] symbolArr) {
        // 对每一列进行处理
        for (int column = ONE_COLUM_INDEX; column < MANGKUKULAM_GAME_INFO.getColumn(); column++) {
            // 先收集当前列所有非空元素
            List<MangkukulamSymbolDo> nonNullSymbols = new ArrayList<>();
            for (int row = ONE_ROW_INDEX; row < MANGKUKULAM_GAME_INFO.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    nonNullSymbols.add(symbolArr[row][column]);
                }
            }

            // 清空当前列
            for (int row = ONE_ROW_INDEX; row < MANGKUKULAM_GAME_INFO.getRow(); row++) {
                symbolArr[row][column] = null;
            }

            // 从底部开始填充非空元素
            int currentRow = MANGKUKULAM_GAME_INFO.getRow() - 1;
            for (int i = nonNullSymbols.size() - 1; i >= 0; i--) {
                symbolArr[currentRow][column] = nonNullSymbols.get(i);
                currentRow--;
            }
        }
    }

    public static boolean hasNormalWildInColumn(MangkukulamSymbolDo[][] symbolArr, int column) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
            if (Objects.nonNull(symbolArr[row][column]) && symbolArr[row][column].isNormalWild()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSpecialWildInColumn(MangkukulamSymbolDo[][] symbolArr, int column) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
            if (Objects.nonNull(symbolArr[row][column]) && symbolArr[row][column].isSpecialWild()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasScatterInColumn(MangkukulamSymbolDo[][] symbolArr, int column) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
            if (Objects.nonNull(symbolArr[row][column]) && symbolArr[row][column].isScatterSymbol()) {
                return true;
            }
        }
        return false;
    }

    public static List<IndexDo> listAvailableMoveWildIndexes(MangkukulamSymbolDo[][] symbolArr, List<Integer> availableColumns) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int row = symbolArr.length;
        List<IndexDo> availableIndexes = new ArrayList<>();
        for (int i = 0; i < row; i++) {
            for (int j : availableColumns) {
                MangkukulamSymbolDo symbol = symbolArr[i][j];
                if (Objects.isNull(symbol)) {
                    availableIndexes.add(IndexFactory.buildDo(i, j));
                    continue;
                }
                if (symbol.isNormalWild() || symbol.isSpecialWild() || symbol.isSymbolFireBorder() || symbol.isScatterSymbol() || symbol.isPlaceholderSymbol()) {
                    continue;
                }
                availableIndexes.add(IndexFactory.buildDo(i, j));
            }
        }
        Collections.shuffle(availableIndexes, new Random());
        return availableIndexes;
    }

    public static List<IndexDo> getAvailableFireBorderIndexes(MangkukulamSymbolDo[][] symbolArr, List<Integer> availableColumns) {
        List<IndexDo> canAddFireBorderIndexes = new ArrayList<>();
        int row = symbolArr.length;
        for (Integer column : availableColumns) {
            for (int rowNum = 0; rowNum < row; rowNum++) {
                MangkukulamSymbolDo symbol = symbolArr[rowNum][column];
                if (symbol.isPlaceholderSymbol() || symbol.isWildSymbol() || symbol.isSymbolFireBorder() || symbol.isScatterSymbol()) {
                    continue;
                }
                canAddFireBorderIndexes.add(new IndexDo(rowNum, column));
            }
        }
        Collections.shuffle(canAddFireBorderIndexes, new Random());
        return canAddFireBorderIndexes;
    }

    public static void convertSpecialWild(MangkukulamSymbolDo[][] symbolArr, List<Integer> superWildColumnList) {
        if (CollectionUtils.isEmpty(superWildColumnList)) {
            return;
        }
        int row = symbolArr.length;
        for (Integer column : superWildColumnList) {
            for (int rowNum = 0; rowNum < row; rowNum++) {
                if (Objects.nonNull(symbolArr[rowNum][column]) && !symbolArr[rowNum][column].isSpecialWild()) {
                    symbolArr[rowNum][column] = MANGKUKULAM_SYMBOL_PLACEHOLDER;
                }
            }
        }
    }
}
