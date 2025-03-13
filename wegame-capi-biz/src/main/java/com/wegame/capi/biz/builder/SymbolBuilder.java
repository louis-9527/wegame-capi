package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.domain.vo.malakas.MalakasSymbolVo;

import java.util.Objects;

public class SymbolBuilder {

    public static String[][] build(SymbolDo[][] symbolArr) {
        if (Objects.isNull(symbolArr)
                || Objects.equals(symbolArr.length, 0)
                || Objects.isNull(symbolArr[0])) {
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


    public static MalakasSymbolVo[][] build(MalakasSymbolDo[][] symbolArr) {
        int rows = symbolArr.length;
        int cols = symbolArr[0].length;
        MalakasSymbolVo[][] integerArray = new MalakasSymbolVo[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                MalakasSymbolDo malakasSymbol = symbolArr[row][col];
                integerArray[row][col] = Objects.isNull(malakasSymbol) ? null : MalakasSymbolVo.builder()
                        .symbolId(malakasSymbol.getId())
                        .symbolSize(malakasSymbol.getSymbolSize().getCode())
                        .symbolClearTimes(malakasSymbol.getSymbolClearTimes())
                        .symbolGoldBorder(malakasSymbol.isSymbolGoldBorder())
                        .build();
            }
        }
        return integerArray;
    }
}
