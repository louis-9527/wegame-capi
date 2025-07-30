package com.wegame.capi.biz.module.sirena;

import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


@Slf4j
public class SirenaSymbolUtil {
    public static void print(SirenaSymbolDo[][] symbolArr) {
        for (int row = 0; row < symbolArr.length; row++) {
            for (int column = 0; column < symbolArr[row].length; column++) {
                if (Objects.nonNull(symbolArr[row][column])) {
                    System.out.print(symbolArr[row][column].getId() + " ");
                } else {
                    System.out.print("null   ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

}
