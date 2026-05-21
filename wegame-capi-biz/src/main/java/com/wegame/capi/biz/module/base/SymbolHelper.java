package com.wegame.capi.biz.module.base;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.Objects;

@Slf4j
public class SymbolHelper {

    public static <T extends SymbolDo> T[][] handleSymbolNullArr(T[][] symbolArr, T symbolHolder, Class<T> clazz) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        @SuppressWarnings("unchecked")
        T[][] newSymbolArr = (T[][]) Array.newInstance(clazz, row, col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (Objects.isNull(symbolArr[i][j])) {
                    newSymbolArr[i][j] = symbolHolder;
                } else {
                    newSymbolArr[i][j] = (T) symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }
}