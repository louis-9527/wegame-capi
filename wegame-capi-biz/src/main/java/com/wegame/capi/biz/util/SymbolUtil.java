package com.wegame.capi.biz.util;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;

import java.util.Objects;

public class SymbolUtil {

    public static String[][] convert(SymbolDo[][] symbolArr) {
        if (Objects.isNull(symbolArr)) {
            return null;
        }
        int rows = symbolArr.length;
        int cols = symbolArr[0].length;
        String[][] integerArray = new String[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                integerArray[i][j] = symbolArr[i][j].getId();
            }
        }
        return integerArray;
    }
}
