package com.wegame.capi.biz.module.atlantis;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class AtlantisSymbolUtil {

    public static SymbolDo[][] copySymbolArr(SymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        SymbolDo[][] newSymbolArr = new SymbolDo[row][col];
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


    public static SymbolDo[][] handleWildSymbolArrNest(SymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        SymbolDo[][] newSymbolArr = new SymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                newSymbolArr[i][j] = symbolArr[i][j].copy();
            }
        }
        return newSymbolArr;
    }


}
