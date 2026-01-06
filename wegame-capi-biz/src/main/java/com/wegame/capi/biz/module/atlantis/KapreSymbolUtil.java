package com.wegame.capi.biz.module.atlantis;

import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;


@Slf4j
public class KapreSymbolUtil {

    public static KapreSymbolDo[][] handleWildSymbolArrNest(KapreSymbolDo[][] symbolArr) {
        int row = symbolArr.length;
        int col = symbolArr[0].length;
        KapreSymbolDo[][] newSymbolArr = new KapreSymbolDo[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (symbolArr[i][j].isWildSymbol() && symbolArr[i][j].getNestSymbol() == null) {
                    // wild 元素，给一个嵌套的现金symbol 现金symbol的倍率给0
                    KapreSymbolDo cashSymbol = KapreSymbolDo.buildFromSymbol(KapreGameHelper.KAPRE_CASH_SYMBOL, BigDecimal.ZERO, null);
                    newSymbolArr[i][j] = KapreSymbolDo.buildFromSymbol(symbolArr[i][j], BigDecimal.ZERO, cashSymbol);
                } else {
                    newSymbolArr[i][j] = symbolArr[i][j].copy();
                }
            }
        }
        return newSymbolArr;
    }


}
