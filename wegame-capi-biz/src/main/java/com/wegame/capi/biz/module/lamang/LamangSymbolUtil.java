package com.wegame.capi.biz.module.lamang;

import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.common.enums.SymbolEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

import static com.wegame.capi.biz.module.lamang.LamangGameHelper.*;
import static com.wegame.capi.common.constants.GameConstants.ONE_ROW_INDEX;

@Slf4j
public class LamangSymbolUtil {


    /**
     * 是否有超级wild
     * @param symbolArr
     * @return
     */
    public static boolean hasSuperWild(LamangSymbolDo[][] symbolArr) {
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && StringUtils.equals(symbolArr[row][col].getId(), LAMANG_SUPER_WILD.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理大wild
     * @param symbolArr
     */
    public static void holdBigWild(LamangSymbolDo[][] symbolArr) {
        //todo 用占位符覆盖bigWild所在1-3列
        // 按行处理
        for (int row = 0; row < symbolArr.length; row++) {
            boolean hasWild = false;
            for (int col = 0; col < symbolArr[row].length; col++) {
                LamangSymbolDo current = symbolArr[row][col];
                if (Objects.nonNull(current) && current.getId().equals(LAMANG_BIG_WILD.getId())) {
                    hasWild = true;
                }
            }
            if (hasWild) {
                for (int col = 1; col < 4; col++) { // 修改第2、3、4列（索引为1、2、3）
                    LamangSymbolDo current = symbolArr[row][col];
                    //如果是bigwild，不处理，不是bigwild则换位占位符
                    if (Objects.nonNull(current) && current.getId().equals(LAMANG_BIG_WILD.getId())) {
                        //不处理
                    } else {
                        symbolArr[row][col] = LamangSymbolDo.buildFromSymbol(LAMANG_SYMBOL_PLACEHOLDER, false);
                    }
                }
            }
        }
    }

    /**
     * 计算中奖次数 (新增scatter & scatter+1数量) 并更新元素状态
     * @param symbolArr
     * @return
     */
    //todo 优化
    public static int countAndReleaseScatterDrawTimes(LamangSymbolDo[][] symbolArr) {
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                LamangSymbolDo current = symbolArr[row][col];
                if (Objects.nonNull(current) && current.isScatterSymbol() && !current.isJackpotDrawTimesAdded()) {
                    count = count + 1;
                    current.setJackpotDrawTimesAdded(true);
                }
            }
        }
        return count;
    }

    /**
     * 计算scatter+1赠送免费轮次数，并更新元素状态
     * @param symbolArr
     * @return
     */
    //todo 优化
    public static int countAndReleaseScatterFreeWheel(LamangSymbolDo[][] symbolArr) {
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                LamangSymbolDo current = symbolArr[row][col];
                if (Objects.nonNull(current) && current.getId().equals(LAMANG_SCATTER1.getId()) && !current.isFreeWheelAdded()) {
                    count = count + 1;
                    current.setFreeWheelAdded(true);
                }
            }
        }
        return count;
    }

    public static int countIndexSymbol(LamangSymbolDo[][] symbolArr, SymbolEnum symbol) {
        int count = 0;
        for (int row = 0; row < symbolArr.length; row++) {
            for (int col = 0; col < symbolArr[row].length; col++) {
                if (Objects.nonNull(symbolArr[row][col]) && StringUtils.equals(symbolArr[row][col].getId(), symbol.getId())) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    public static int countIndexSymbol(LamangSymbolDo[][] symbolArr, SymbolEnum symbol, int column) {
        int count = 0;
        for (int row = ONE_ROW_INDEX; row < symbolArr.length; row++) {
            if (Objects.nonNull(symbolArr[row][column]) && StringUtils.equals(symbolArr[row][column].getId(), symbol.getId())) {
                count = count + 1;
            }
        }
        return count;
    }

    public static LamangSymbolDo[][] copySymbolArr(LamangSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        LamangSymbolDo[][] newSymbolArr = new LamangSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = null;
                } else if (symbolArr[i][j].isPlaceholderSymbol()) {
                    newSymbolArr[i][j] = LAMANG_SYMBOL_PLACEHOLDER;
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }


}
