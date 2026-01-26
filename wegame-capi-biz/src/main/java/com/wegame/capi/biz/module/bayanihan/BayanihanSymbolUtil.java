package com.wegame.capi.biz.module.bayanihan;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.BayanihanGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.BayanihanSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
public class BayanihanSymbolUtil {

    public static BayanihanSymbolDo[][] copySymbolArr(BayanihanSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        BayanihanSymbolDo[][] newSymbolArr = new BayanihanSymbolDo[row][col];
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

    public static int countScatterInWheel(BayanihanSymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && symbolArr[row][col].isScatterSymbol()) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    public static int countScatterInColumn(BayanihanSymbolDo[][] symbolArr, int column) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int count = 0;
        for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
            if (Objects.nonNull(symbolArr[row][column]) && symbolArr[row][column].isScatterSymbol()) {
                count = count + 1;
            }
        }
        return count;
    }

    public static int countNewSymbolInColumn(BayanihanSymbolDo[][] symbolArr, String symbolId) {
        AssertUtil.assertNotNull(symbolArr, ErrorCode.ERR_PARAM);
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && symbolArr[row][col].isNewSymbol() && Objects.equals(symbolId, symbolArr[row][col].getId())) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    /**
     * 下落
     * 将每一列的非空元素下移到最底部，类似重力效果
     *
     * @param symbolArr 符号数组
     */
    public static void fallSymbols(BayanihanSymbolDo[][] symbolArr) {
        // 对每一列进行处理
        for (int column = ONE_COLUM_INDEX; column < BayanihanGameHelper.BAYANIHAN_GAME_INFO.getColumn(); column++) {
            // 先收集当前列所有非空元素
            List<BayanihanSymbolDo> nonNullSymbols = new ArrayList<>();
            for (int row = ONE_ROW_INDEX; row < BayanihanGameHelper.BAYANIHAN_GAME_INFO.getRow(); row++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    nonNullSymbols.add(symbolArr[row][column]);
                }
            }

            // 清空当前列
            for (int row = ONE_ROW_INDEX; row < BayanihanGameHelper.BAYANIHAN_GAME_INFO.getRow(); row++) {
                symbolArr[row][column] = null;
            }

            // 从底部开始填充非空元素
            int currentRow = BayanihanGameHelper.BAYANIHAN_GAME_INFO.getRow() - 1;
            for (int i = nonNullSymbols.size() - 1; i >= 0; i--) {
                symbolArr[currentRow][column] = nonNullSymbols.get(i);
                currentRow--;
            }
        }
    }

    /**
     * 消除中奖元素 <br/>
     */
    public static void clearRewardSymbol(BayanihanSymbolDo[][] symbolArr, RewardResultDo<BayanihanSymbolDo> rewardResult) {
        List<RewardSymbolDo<BayanihanSymbolDo>> rewardSymbolDoList = getRewardSymbolDoList(rewardResult);
        if (CollectionUtils.isNotEmpty(rewardSymbolDoList)) {
            rewardSymbolDoList.parallelStream().forEach(rewardSymbol -> clearIndexRewardSymbol(symbolArr, rewardSymbol));
        }
    }

    public static List<RewardSymbolDo<BayanihanSymbolDo>> getRewardSymbolDoList(RewardResultDo<BayanihanSymbolDo> rewardResult) {
        AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(rewardResult.getRewardLineDetailList(), ErrorCode.ERR_INTER);
        return rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo<BayanihanSymbolDo>::getRewardSymbolList).flatMap(List::stream).collect(Collectors.toList());
    }

    private static void clearIndexRewardSymbol(BayanihanSymbolDo[][] symbolArr, RewardSymbolDo<BayanihanSymbolDo> rewardSymbol) {
        if (rewardSymbol.getSymbol().isScatterSymbol()) {
            // scatter 元素， 不做消除
        } else if (rewardSymbol.getSymbol().isWildSymbol()) {
            //todo 如果有次数，则减少次数，否则移除
            if (rewardSymbol.getSymbol().getSymbolClearTimes() > 1) {
                IndexDo indexDo = rewardSymbol.getIndex();
                BayanihanSymbolDo wildSymbol = symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()];
                wildSymbol.setSymbolClearTimes(rewardSymbol.getSymbol().getSymbolClearTimes() - 1);
            } else {
                removeSymbol(symbolArr, rewardSymbol);
            }
        } else {
            // 普通元素消除
            removeSymbol(symbolArr, rewardSymbol);
        }
    }

    /**
     * 移除元素
     */
    private static void removeSymbol(BayanihanSymbolDo[][] symbolArr, RewardSymbolDo<BayanihanSymbolDo> rewardSymbol) {
        IndexDo indexDo = rewardSymbol.getIndex();
        // 直接移除
        symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = null;
    }

    public static void print(BayanihanGameRecordDo recordDo) {
        if (Objects.nonNull(recordDo.getCollectionInfo())) {
            System.out.println("print gameRecord: collectionInfoSnapShot : c:" + recordDo.getCollectionInfo().getCurrentMultiple() +  " r:" +recordDo.getCollectionInfo().getRemainCollectCount());
        }
        print(recordDo.getWheel().getSymbolArr(), recordDo.getWheel().getIndex());
    }

    /**
     * 用于单元测试输出
     *
     * @param symbolArr
     * @param currentIndex
     */
    public static void print(BayanihanSymbolDo[][] symbolArr, int currentIndex) {
        System.out.println("symbolArr index :" + currentIndex);
        for (int row = 0; row < symbolArr.length; row++) {
            for (int column = 0; column < symbolArr[row].length; column++) {
                BayanihanSymbolDo current = symbolArr[row][column];
                if (Objects.nonNull(current)) {
                    int times = Objects.nonNull(current.getSymbolClearTimes()) ? current.getSymbolClearTimes() : 0;
                    if (!current.isNewSymbol()) {
                        if (current.isWildSymbol()) {
                            System.out.print(current.getId() + ":" + times + "\t");
                        } else {
                            System.out.print(current.getId() + ":" + times + "\t");
                        }
                    } else {
                        if (current.isWildSymbol()) {
                            System.out.print("[" + current.getId() + ":" + times + "]\t");
                        } else {
                            System.out.print("[" + current.getId() + ":" + times + "]\t");
                        }
                    }
                }
            }
            System.out.println();
        }
        System.out.println();
    }

}
