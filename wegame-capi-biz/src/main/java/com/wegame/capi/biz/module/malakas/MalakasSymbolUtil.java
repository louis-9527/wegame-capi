package com.wegame.capi.biz.module.malakas;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardSymbolDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.IndexDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolSizeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;
import static com.wegame.capi.common.enums.SymbolEnum.Symbol1000;
import static com.wegame.capi.common.enums.SymbolSizeEnum.S;

public class MalakasSymbolUtil {

    public static MalakasSymbolDo[][] copySymbolArr(MalakasSymbolDo[][] symbolArr, MalakasSymbolDo placeholderSymbol) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        MalakasSymbolDo[][] newSymbolArr = new MalakasSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = null;
                } else if (StringUtils.equals(symbolArr[i][j].getId(), placeholderSymbol.getId())) {
                    newSymbolArr[i][j] = placeholderSymbol;
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }

    /**
     * 重建元素数组：（非空元素按行（第一行）左移，按列下移处理）
     *
     * @param symbolArr
     */
    public static void rebuildSymbolArr(MalakasSymbolDo[][] symbolArr, GameInfoDo gameInfoDo) {
        // 处理第一行：按行[1,row-1]处理(非空元素左移，第一列元素&最后一列元素为空)
        int currentColum = 1;
        for (int column = 1; column < gameInfoDo.getColumn() - 1; column++) {
            if (Objects.nonNull(symbolArr[ONE_ROW_INDEX][column])) {
                if (Objects.equals(currentColum, column)) {
                    // 不做移动
                    // do nothing
                } else {
                    // 移动
                    MalakasSymbolDo temp = symbolArr[ONE_ROW_INDEX][column];
                    symbolArr[ONE_ROW_INDEX][currentColum] = temp;
                    symbolArr[ONE_ROW_INDEX][column] = null;
                }
                currentColum++;
            }
        }
        // 处理非第一行：按列处理（从第二行开始，非空元素下移）
        for (int column = ONE_COLUM_INDEX; column < gameInfoDo.getColumn(); column++) {
            int currentRow = gameInfoDo.getRow() - 1;
            for (int row = gameInfoDo.getRow() - 1; row > ONE_ROW_INDEX; row--) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    if (Objects.equals(currentRow, row)) {
                        // 不做移动
                        // do nothing
                    } else {
                        // 移动
                        MalakasSymbolDo temp = symbolArr[row][column];
                        symbolArr[currentRow][column] = temp;
                        symbolArr[row][column] = null;
                    }
                    currentRow--;
                }
            }
        }
    }

    /**
     * 消除中奖元素 <br/>
     * **** 中奖元素参与多条中奖线，则只做一次消除<br/>
     * 1、scatter元素不做消除<br/>
     * 2、普通元素：<br/>
     * 2.1 带金边，元素变成wild元素<br/>
     * 2.2 不带金边，直接消除<br/>
     * 3、wild元素：消除次数减1，若减一后消除次数为0，则消除。 否则不消除<br/>
     *
     * @param symbolArr
     * @param rewardResult
     */
    public static void clearRewardSymbol(MalakasSymbolDo[][] symbolArr, RewardResultDo<MalakasSymbolDo> rewardResult) {
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
                    // 消除次数大于1，则不做消除, 消除次数-1
                    IndexDo indexDo = rewardSymbol.getIndex();
                    MalakasSymbolDo commonSymbol = symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()];
                    commonSymbol.setSymbolClearTimes(rewardSymbol.getSymbol().getSymbolClearTimes() - 1);
                } else {
                    // 消除次数小于1，则移除
                    removeRewardSymbol(symbolArr, rewardSymbol);
                }
            } else {
                // 普通元素消除
                IndexDo indexDo = rewardSymbol.getIndex();
                MalakasSymbolDo commonSymbol = symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()];
                if (commonSymbol.isSymbolGoldBorder()) {
                    // 带金边普通元素，转换为Wild元素（元素大小转换为消除次数）
                    MalakasSymbolDo wildSymbol = MalakasSymbolDo.builder()
                            .id(SymbolEnum.Wild.getId())
                            .name(SymbolEnum.Wild.getName())
                            .symbolType(SymbolTypeEnum.WILD)
                            .symbolSize(commonSymbol.getSymbolSize())
                            .symbolGoldBorder(false)
                            .symbolClearTimes(SymbolSizeEnum.getSymbolClearTimes(commonSymbol.getSymbolSize()))
                            .build();
                    symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = wildSymbol;
                } else {
                    // 不带金边普通元素，直接移除
                    removeRewardSymbol(symbolArr, rewardSymbol);
                }
            }
        }
    }

    /**
     * 移除元素
     *
     * @param symbolArr
     * @param rewardSymbol
     */
    public static void removeRewardSymbol(MalakasSymbolDo[][] symbolArr, RewardSymbolDo<MalakasSymbolDo> rewardSymbol) {
        IndexDo indexDo = rewardSymbol.getIndex();
        if (Objects.equals(indexDo.getRowIndex(), ONE_ROW_INDEX)) {
            // 第一行元素：直接移除
            symbolArr[indexDo.getRowIndex()][indexDo.getColumnIndex()] = null;
        } else {
            // 非第一行元素：按照元素大小进行移除
            int columSpanSiz = rewardSymbol.getSymbol().getSymbolSize().getRowSpan();
            for (int columSpan = 0; columSpan < columSpanSiz; columSpan++) {
                symbolArr[indexDo.getRowIndex() + columSpan][indexDo.getColumnIndex()] = null;
            }
        }
    }

    public static void handlePlaceholderSymbol(MalakasSymbolDo[][] symbolArr, int row, int column, SymbolSizeEnum symbolSize, MalakasSymbolDo placeholderSymbol) {
        switch (symbolSize) {
            case S:
                break;
            case M:
                symbolArr[row + 1][column] = placeholderSymbol;
                break;
            case L:
                symbolArr[row + 1][column] = placeholderSymbol;
                symbolArr[row + 2][column] = placeholderSymbol;
                break;
            case XL:
                symbolArr[row + 1][column] = placeholderSymbol;
                symbolArr[row + 2][column] = placeholderSymbol;
                symbolArr[row + 3][column] = placeholderSymbol;
                break;
        }
    }

    public static int getMaxRow(MalakasSymbolDo[][] symbolArr, int currentRow, int currentColum) {
        int maxRow = currentRow;
        while (maxRow < symbolArr.length - 1 && Objects.isNull(symbolArr[maxRow - 1][currentColum])) {
            maxRow++;
        }
        return maxRow;
    }

    public static int countIndexSymbol(MalakasSymbolDo[][] symbolArr, SymbolEnum symbol) {
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && StringUtils.equals(symbolArr[row][col].getId(), symbol.getId())) {
                    count++;
                }
            }
        }
        return count;
    }

    public static MalakasSymbolDo getPlaceholderSymbol() {
        return MalakasSymbolDo.builder()
                .id(Symbol1000.getId())
                .name(Symbol1000.getName())
                .symbolType(SymbolTypeEnum.COMMON)
                .symbolSize(S)
                .symbolGoldBorder(false)
                .build();
    }

    public static void print(MalakasSymbolDo[][] symbolArr, String scene) {
        System.out.println("symbolArr: " + scene);
        for (int row = 0; row < symbolArr.length; row++) {
            for (int column = 0; column < symbolArr[row].length; column++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    System.out.print(symbolArr[row][column].getId() + " " + symbolArr[row][column].getSymbolSize().getRowSpan() + " ");
                } else {
                    System.out.print("null   ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
