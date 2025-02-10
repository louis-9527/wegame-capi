package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.domain.vo.MalakasBetResultVo;

import java.util.Arrays;
import java.util.Objects;

public class SymbolBuilder {

    public static String[][] build(SymbolDo[][] symbolArr) {
        if (Objects.isNull(symbolArr)
                || Objects.equals(symbolArr.length, 0)
                || Objects.isNull(symbolArr[0])) {
            return null;
        }
//        return Arrays.stream(symbolArr)
//                .map(innerArr -> Arrays.stream(innerArr)
//                        .map(item -> Objects.isNull(item) ? null : item.getId()))
//                .toArray(String[][]::new);

        // todo delete
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


    public static MalakasBetResultVo.MalakasSymbolVo[][] build(MalakasSymbolDo[][] symbolArr) {
        int rows = symbolArr.length;
        int cols = symbolArr[0].length;
        MalakasBetResultVo.MalakasSymbolVo[][] integerArray = new MalakasBetResultVo.MalakasSymbolVo[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                MalakasSymbolDo malakasSymbolDo = symbolArr[i][j];
                if (malakasSymbolDo == null) {
                    integerArray[i][j] = null;
                } else {
                    MalakasBetResultVo.MalakasSymbolVo malakasSymbolVo = new MalakasBetResultVo.MalakasSymbolVo();
                    malakasSymbolVo.setSymbolId(malakasSymbolDo.getId());
                    malakasSymbolVo.setSymbolSize(malakasSymbolDo.getSymbolSize().getCode());
                    malakasSymbolVo.setSymbolClearTimes(malakasSymbolDo.getSymbolClearTimes());
                    malakasSymbolVo.setSymbolGoldBorder(malakasSymbolDo.isSymbolGoldBorder());
                    integerArray[i][j] = malakasSymbolVo;
                }
            }
        }
        return integerArray;
    }
}
