package com.wegame.capi.test.biz.module.kapre.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.kapre.KapreGameHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import java.math.BigDecimal;
import java.util.Objects;

public class KapreRewardHandlerTestMocker {

    protected static KapreSymbolDo[][] mockSymbolArr_reward_1001() {
        KapreSymbolDo[][] symbolArr = new KapreSymbolDo[4][5];
        // 第一列
        symbolArr[0][0] = buildCommonSymbol(SymbolEnum.Symbol1001);
        symbolArr[1][0] = buildCommonSymbol(SymbolEnum.Symbol1002);
        symbolArr[2][0] = buildCommonSymbol(SymbolEnum.Symbol1003);
        symbolArr[3][0] = buildCommonSymbol(SymbolEnum.Symbol1004);

        // 第二列
        symbolArr[0][1] = buildCommonSymbol(SymbolEnum.Symbol1001);
        symbolArr[1][1] = buildCommonSymbol(SymbolEnum.Symbol1005);
        symbolArr[2][1] = buildCommonSymbol(SymbolEnum.Symbol1006);
        symbolArr[3][1] = buildCommonSymbol(SymbolEnum.Symbol1007);

        // 第三列
        symbolArr[0][2] = buildCommonSymbol(SymbolEnum.Symbol1001);
        symbolArr[1][2] = buildCommonSymbol(SymbolEnum.Symbol1005);
        symbolArr[2][2] = buildCommonSymbol(SymbolEnum.Symbol1009);
        symbolArr[3][2] = buildCommonSymbol(SymbolEnum.Symbol1007);

        // 第四列
        symbolArr[0][3] = buildCommonSymbol(SymbolEnum.Symbol1005);
        symbolArr[1][3] = buildCommonSymbol(SymbolEnum.Symbol1006);
        symbolArr[2][3] = buildCommonSymbol(SymbolEnum.Symbol1007);
        symbolArr[3][3] = buildCommonSymbol(SymbolEnum.Symbol1008);

        // 第五列
        symbolArr[0][4] = buildCommonSymbol(SymbolEnum.Symbol1009);
        symbolArr[1][4] = buildCommonSymbol(SymbolEnum.Symbol1006);
        symbolArr[2][4] = buildCommonSymbol(SymbolEnum.Symbol1007);
        symbolArr[3][4] = buildCommonSymbol(SymbolEnum.Symbol1008);
        return symbolArr;
    }

    protected static KapreSymbolDo[][] mockSymbolArr_reward_cash() {
        KapreSymbolDo[][] symbolArr = new KapreSymbolDo[4][5];
        // 第一列
        symbolArr[0][0] = buildCashSymbol(BigDecimal.valueOf(5));
        symbolArr[1][0] = buildCommonSymbol(SymbolEnum.Symbol1002);
        symbolArr[2][0] = buildCommonSymbol(SymbolEnum.Symbol1003);
        symbolArr[3][0] = buildCommonSymbol(SymbolEnum.Symbol1004);

        // 第二列
        symbolArr[0][1] = buildCommonSymbol(SymbolEnum.Symbol1001);
        symbolArr[1][1] = buildCommonSymbol(SymbolEnum.Symbol1005);
        symbolArr[2][1] = buildCashSymbol(BigDecimal.valueOf(2));
        symbolArr[3][1] = buildCommonSymbol(SymbolEnum.Symbol1007);

        // 第三列
        symbolArr[0][2] = buildCommonSymbol(SymbolEnum.Symbol1001);
        symbolArr[1][2] = buildCashSymbol(BigDecimal.valueOf(3));
        symbolArr[2][2] = buildCommonSymbol(SymbolEnum.Symbol1009);
        symbolArr[3][2] = buildCommonSymbol(SymbolEnum.Symbol1007);

        // 第四列
        symbolArr[0][3] = buildCommonSymbol(SymbolEnum.Symbol1005);
        symbolArr[1][3] = buildCommonSymbol(SymbolEnum.Symbol1006);
        symbolArr[2][3] = buildCommonSymbol(SymbolEnum.Symbol1007);
        symbolArr[3][3] = buildCommonSymbol(SymbolEnum.Symbol1008);

        // 第五列
        symbolArr[0][4] = buildCommonSymbol(SymbolEnum.Symbol1009);
        symbolArr[1][4] = buildCommonSymbol(SymbolEnum.Symbol1006);
        symbolArr[2][4] = buildCommonSymbol(SymbolEnum.Symbol1007);
        symbolArr[3][4] = buildCommonSymbol(SymbolEnum.Symbol1008);
        return symbolArr;
    }

    protected static KapreSymbolDo[][] mockSymbolArr_reward_cash_wild() {
        KapreSymbolDo[][] symbolArr = new KapreSymbolDo[4][5];
        // 第一列
        symbolArr[0][0] = buildCashSymbol(BigDecimal.valueOf(5));
        symbolArr[1][0] = buildCommonSymbol(SymbolEnum.Symbol1002);
        symbolArr[2][0] = buildCommonSymbol(SymbolEnum.Symbol1003);
        symbolArr[3][0] = buildCommonSymbol(SymbolEnum.Symbol1004);

        // 第二列
        symbolArr[0][1] = buildCommonSymbol(SymbolEnum.Symbol1001);
        symbolArr[1][1] = buildCommonSymbol(SymbolEnum.Symbol1005);
        symbolArr[2][1] = buildWildSymbol(BigDecimal.valueOf(10));
        symbolArr[3][1] = buildCommonSymbol(SymbolEnum.Symbol1007);

        // 第三列
        symbolArr[0][2] = buildCommonSymbol(SymbolEnum.Symbol1001);
        symbolArr[1][2] = buildCashSymbol(BigDecimal.valueOf(5));
        symbolArr[2][2] = buildCommonSymbol(SymbolEnum.Symbol1009);
        symbolArr[3][2] = buildCommonSymbol(SymbolEnum.Symbol1007);

        // 第四列
        symbolArr[0][3] = buildCommonSymbol(SymbolEnum.Symbol1005);
        symbolArr[1][3] = buildCommonSymbol(SymbolEnum.Symbol1006);
        symbolArr[2][3] = buildCommonSymbol(SymbolEnum.Symbol1007);
        symbolArr[3][3] = buildCommonSymbol(SymbolEnum.Symbol1008);

        // 第五列
        symbolArr[0][4] = buildCommonSymbol(SymbolEnum.Symbol1009);
        symbolArr[1][4] = buildCommonSymbol(SymbolEnum.Symbol1006);
        symbolArr[2][4] = buildCommonSymbol(SymbolEnum.Symbol1007);
        symbolArr[3][4] = buildCommonSymbol(SymbolEnum.Symbol1008);
        return symbolArr;
    }

    protected static KapreSymbolDo buildWildSymbol(BigDecimal multi) {
        KapreSymbolDo symbolDo = new KapreSymbolDo();
        symbolDo.setId(SymbolEnum.Wild.getId());
        symbolDo.setName(SymbolEnum.Wild.getName());
        symbolDo.setSymbolType(SymbolTypeEnum.WILD);
        if (Objects.nonNull(multi)) {
            KapreSymbolDo nested = new KapreSymbolDo();
            nested.setId(SymbolEnum.Symbol1010.getId());
            nested.setName(SymbolEnum.Symbol1010.getName());
            nested.setSymbolType(SymbolTypeEnum.COMMON);
            symbolDo.setNestSymbol(nested);
        }
        return symbolDo;
    }

    protected static KapreSymbolDo buildCommonSymbol(SymbolEnum symbol) {
        KapreSymbolDo symbolDo = new KapreSymbolDo();
        symbolDo.setId(symbol.getId());
        symbolDo.setName(symbol.getName());
        symbolDo.setSymbolType(SymbolTypeEnum.COMMON);
        return symbolDo;
    }

    protected static KapreSymbolDo buildCashSymbol(BigDecimal multi) {
        KapreSymbolDo symbolDo = new KapreSymbolDo();
        symbolDo.setId(SymbolEnum.Symbol1010.getId());
        symbolDo.setName(SymbolEnum.Symbol1010.getId());
        symbolDo.setSymbolMultiple(multi);
        symbolDo.setSymbolType(SymbolTypeEnum.COMMON);
        return symbolDo;
    }

    public static RewardHandleDo<KapreSymbolDo> buildTestRewardHandleDo(KapreSymbolDo[][] symbolArr, int wheelIndex, boolean isFreeFlag, long betAmount, BigDecimal currentMultiple) {
        return RewardHandleDo.<KapreSymbolDo>builder()
                .gameType(KapreGameHelper.KAPRE_GAME_INFO.getGameType())
                .gameInfo(KapreGameHelper.KAPRE_GAME_INFO)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .betAmount(betAmount)
                .wheel(WheelDo.<KapreSymbolDo>builder()
                        .index(wheelIndex)
                        .symbolArr(symbolArr)
                        .freeFlag(isFreeFlag)
                        .build())
                .currentMultiple(currentMultiple)
                .build();
    }
}
