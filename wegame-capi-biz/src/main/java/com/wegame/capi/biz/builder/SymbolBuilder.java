package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.symbol.*;
import com.wegame.capi.client.domain.vo.diwatamayari.DiwataMayariSymbolVo;
import com.wegame.capi.client.domain.vo.ibongadarna.IbongAdarnaSymbolVo;
import com.wegame.capi.client.domain.vo.kapre.KapreSymbolVo;
import com.wegame.capi.client.domain.vo.lamang.LamangSymbolVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasSymbolVo;
import com.wegame.capi.client.domain.vo.mangkukulam.MangkukulamSymbolVo;
import com.wegame.capi.client.domain.vo.sirena.SirenaSymbolVo;
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

    public static LamangSymbolVo[][] build(LamangSymbolDo[][] symbolArr) {
        int rows = symbolArr.length;
        int cols = symbolArr[0].length;
        LamangSymbolVo[][] integerArray = new LamangSymbolVo[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                LamangSymbolDo lamangSymbol = symbolArr[row][col];
                integerArray[row][col] = Objects.isNull(lamangSymbol) ? null : LamangSymbolVo.builder()
                        .symbolId(lamangSymbol.getId())
                        .symbolGoldBorder(lamangSymbol.isSymbolGoldBorder())
                        .build();
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


    public static DiwataMayariSymbolVo[][] build(DiwataMayariSymbolDo[][] symbolArr) {
        int rows = symbolArr.length;
        int cols = symbolArr[0].length;
        DiwataMayariSymbolVo[][] integerArray = new DiwataMayariSymbolVo[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                DiwataMayariSymbolDo diwataMayariSymbol = symbolArr[row][col];
                integerArray[row][col] = Objects.isNull(diwataMayariSymbol) ? null : DiwataMayariSymbolVo.builder()
                        .symbolId(diwataMayariSymbol.getId())
                        .build();
            }
        }
        return integerArray;
    }


    public static SirenaSymbolVo[][] build(SirenaSymbolDo[][] symbolArr) {
        int rows = symbolArr.length;
        int cols = symbolArr[0].length;
        SirenaSymbolVo[][] integerArray = new SirenaSymbolVo[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                SirenaSymbolDo sirenaSymbol = symbolArr[row][col];
                integerArray[row][col] = Objects.isNull(sirenaSymbol) ? null : SirenaSymbolVo.builder()
                        .symbolId(sirenaSymbol.getId())
                        .symbolOdd(sirenaSymbol.getSymbolOdd())
                        .nestSymbol(Objects.isNull(sirenaSymbol.getNestSymbol()) ? null :
                                SirenaSymbolVo.builder()
                                        .symbolId(sirenaSymbol.getNestSymbol().getId())
                                        .symbolOdd(sirenaSymbol.getNestSymbol().getSymbolOdd())
                                        .nestSymbol(null)
                                        .build())
                        .build();
            }
        }
        return integerArray;
    }

    public static KapreSymbolVo[][] build(KapreSymbolDo[][] symbolArr) {
        int rows = symbolArr.length;
        int cols = symbolArr[0].length;
        KapreSymbolVo[][] integerArray = new KapreSymbolVo[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                KapreSymbolDo kapreSymbol = symbolArr[row][col];
                integerArray[row][col] = Objects.isNull(kapreSymbol) ? null : KapreSymbolVo.builder()
                        .symbolId(kapreSymbol.getId())
                        .symbolOdd(kapreSymbol.getSymbolOdd())
                        .nestSymbol(Objects.isNull(kapreSymbol.getNestSymbol()) ? null :
                                KapreSymbolVo.builder()
                                        .symbolId(kapreSymbol.getNestSymbol().getId())
                                        .symbolOdd(kapreSymbol.getNestSymbol().getSymbolOdd())
                                        .nestSymbol(null)
                                        .build())
                        .build();
            }
        }
        return integerArray;
    }

    public static MangkukulamSymbolVo[][] build(MangkukulamSymbolDo[][] symbolArr) {
        int rows = symbolArr.length;
        int cols = symbolArr[0].length;
        MangkukulamSymbolVo[][] integerArray = new MangkukulamSymbolVo[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                MangkukulamSymbolDo mangkukulamSymbolDo = symbolArr[row][col];
                integerArray[row][col] = Objects.isNull(mangkukulamSymbolDo) ? null : MangkukulamSymbolVo.builder()
                        .symbolId(mangkukulamSymbolDo.getId())
                        .symbolFireBorder(mangkukulamSymbolDo.isSymbolFireBorder())
                        .remainClearTimes(mangkukulamSymbolDo.getRemainClearTimes())
                        .multiples(mangkukulamSymbolDo.getMultiples())
                        .build();
            }
        }
        return integerArray;
    }

}
