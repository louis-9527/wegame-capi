package com.wegame.capi.biz.module.atlantis;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
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
                if (symbolArr[i][j].isWildSymbol() && symbolArr[i][j].getNestSymbol() == null) {
                    // wild 元素，给一个嵌套的现金symbol 现金symbol的倍率给0
                    SymbolDo cashSymbol = SymbolDo.buildFromSymbol(AtlantisGameHelper.ATLANTIS_Symbol1001, BigDecimal.ZERO, null);
                    newSymbolArr[i][j] = SymbolDo.buildFromSymbol(symbolArr[i][j], BigDecimal.ZERO, cashSymbol);
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }


}
