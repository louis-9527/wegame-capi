package com.wegame.capi.test.biz.module.sirena.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.sirena.SirenaGameHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import java.math.BigDecimal;

public class SirenaRewardHandlerTestMocker {

    protected static SirenaSymbolDo[][] mockSymbolArr_handle_normal_oneLine() {
        SirenaSymbolDo[][] symbolArr = new SirenaSymbolDo[5][5];
        // 第一列
        symbolArr[0][0] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][0] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON);
        symbolArr[2][0] = buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON);
        symbolArr[3][0] = buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON);
        symbolArr[4][0] = buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON);

        // 第二列
        symbolArr[0][1] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][1] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[2][1] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[3][1] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[4][1] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);

        // 第三列
        symbolArr[0][2] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][2] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[2][2] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[3][2] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[4][2] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);

        // 第四列
        symbolArr[0][3] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][3] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[2][3] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[3][3] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);
        symbolArr[4][3] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);

        // 第五列
        symbolArr[0][4] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][4] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[2][4] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[3][4] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);
        symbolArr[4][4] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);
        return symbolArr;
    }


    protected static SirenaSymbolDo[][] mockSymbolArr_handle_normal_smallPearl() {
        SirenaSymbolDo[][] symbolArr = new SirenaSymbolDo[5][5];
        // 第一列
        symbolArr[0][0] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][0] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON);
        symbolArr[2][0] = buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON);
        symbolArr[3][0] = buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON);
        symbolArr[4][0] = buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON);

        // 第二列
        symbolArr[0][1] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][1] = buildASymbol(SymbolEnum.PearlWild, SymbolTypeEnum.WILD,BigDecimal.ONE);
        symbolArr[2][1] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[3][1] = buildASymbol(SymbolEnum.PearlWild, SymbolTypeEnum.WILD,BigDecimal.TEN);
        symbolArr[4][1] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);

        // 第三列
        symbolArr[0][2] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][2] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[2][2] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[3][2] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[4][2] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);

        // 第四列
        symbolArr[0][3] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][3] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[2][3] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[3][3] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);
        symbolArr[4][3] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);

        // 第五列
        symbolArr[0][4] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][4] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[2][4] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[3][4] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);
        symbolArr[4][4] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);
        return symbolArr;
    }

    protected static SirenaSymbolDo buildASymbol(SymbolEnum symbol, SymbolTypeEnum symbolType) {
        return buildASymbol(symbol, symbolType, BigDecimal.ZERO);
    }

    protected static SirenaSymbolDo buildASymbol(SymbolEnum symbol, SymbolTypeEnum symbolType, BigDecimal symbolOdd) {
        SirenaSymbolDo symbolDo = new SirenaSymbolDo();
        symbolDo.setId(symbol.getId());
        symbolDo.setName(symbol.getName());
        symbolDo.setSymbolType(symbolType);
        symbolDo.setSymbolOdd(symbolOdd);
        return symbolDo;
    }

    public static RewardHandleDo<SirenaSymbolDo> buildTestRewardHandleDo(SirenaSymbolDo[][] symbolArr, boolean isFreeFlag, long betAmount) {
        return RewardHandleDo.<SirenaSymbolDo>builder()
                .gameType(SirenaGameHelper.SIRENA_GAME_INFO.getGameType())
                .gamePlayType(GamePlayTypeEnum.NORMAL_PLAY)
                .gameInfo(SirenaGameHelper.SIRENA_GAME_INFO)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .betAmount(betAmount)
                .currentTotalRewardAmount(0L)
                .wheel(WheelDo.<SirenaSymbolDo>builder()
                        .index(0)
                        .symbolArr(symbolArr)
                        .freeFlag(isFreeFlag)
                        .build())
                .currentMultiple(null)
                .build();
    }
}
