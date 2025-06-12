package com.wegame.capi.biz.module.diawatamayari;

import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.wegame.capi.common.constants.GameConstants.ONE_COLUM_INDEX;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
public class DiwataMayariSymbolUtil {

    public static DiwataMayariSymbolDo[][] copySymbolArr(DiwataMayariSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        DiwataMayariSymbolDo[][] newSymbolArr = new DiwataMayariSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = null;
                } else if (symbolArr[i][j].isPlaceholderSymbol()) {
                    newSymbolArr[i][j] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }


    public static DiwataMayariSymbolDo[][] handleSymbolNullArr(DiwataMayariSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        DiwataMayariSymbolDo[][] newSymbolArr = new DiwataMayariSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = DiwataMayariGameHelper.DIWATA_MAYARI_SYMBOL_PLACEHOLDER;
                }else {
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
    public static void rebuildSymbolArr(DiwataMayariSymbolDo[][] symbolArr, GameInfoDo gameInfoDo) {
        // 处理第一行：按行[1,row-1]处理(非空元素左移，第一列元素&最后一列元素为空)
        int currentColum = 1;
        for (int column = 1; column < gameInfoDo.getColumn() - 1; column++) {
            if (Objects.nonNull(symbolArr[ONE_ROW_INDEX][column])) {
                if (Objects.equals(currentColum, column)) {
                    // 不做移动
                    // do nothing
                } else {
                    // 移动
                    DiwataMayariSymbolDo temp = symbolArr[ONE_ROW_INDEX][column];
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
                        DiwataMayariSymbolDo temp = symbolArr[row][column];
                        symbolArr[currentRow][column] = temp;
                        symbolArr[row][column] = null;
                    }
                    currentRow--;
                }
            }
        }
    }
}
