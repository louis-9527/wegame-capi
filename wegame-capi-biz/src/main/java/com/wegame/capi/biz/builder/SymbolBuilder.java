package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.symbol.IbongAdarnaSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.domain.vo.ibongadarna.IbongAdarnaSymbolVo;
import com.wegame.capi.common.enums.SymbolBreakUpEnum;

import java.util.Objects;
import java.util.Optional;

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


    public static IbongAdarnaSymbolVo[][] build(IbongAdarnaSymbolDo[][] symbolArr) {
        if (Objects.isNull(symbolArr)
                || Objects.equals(symbolArr.length, 0)
                || Objects.isNull(symbolArr[0])) {
            return null;
        }
        int rows = symbolArr.length;
        int cols = symbolArr[0].length;
        IbongAdarnaSymbolVo[][] integerArray = new IbongAdarnaSymbolVo[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                IbongAdarnaSymbolVo symbolVo = IbongAdarnaSymbolVo.builder()
                        .id(symbolArr[i][j].getId())
                        .breakTimes(Optional.ofNullable(symbolArr[i][j].getBreakUp()).map(SymbolBreakUpEnum::getTimes).orElse(SymbolBreakUpEnum.NO_BREAK_UP.getTimes()))
                        .build();
                integerArray[i][j] = symbolVo;
            }
        }
        return integerArray;
    }
}
