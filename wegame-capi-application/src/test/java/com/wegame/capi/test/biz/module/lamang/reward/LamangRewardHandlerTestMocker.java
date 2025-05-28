package com.wegame.capi.test.biz.module.lamang.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.lamang.LamangGameHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import java.math.BigDecimal;

public class LamangRewardHandlerTestMocker {

    protected static LamangSymbolDo[][] mockSymbolArr_handle_normal_oneLine() {
        LamangSymbolDo[][] symbolArr = new LamangSymbolDo[4][5];
        // 第一列
        symbolArr[0][0] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][0] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON);
        symbolArr[2][0] = buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON);
        symbolArr[3][0] = buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON);

        // 第二列
        symbolArr[0][1] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][1] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[2][1] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[3][1] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);

        // 第三列
        symbolArr[0][2] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][2] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[2][2] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[3][2] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);

        // 第四列
        symbolArr[0][3] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][3] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[2][3] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[3][3] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);

        // 第五列
        symbolArr[0][4] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][4] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[2][4] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[3][4] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);
        return symbolArr;
    }

    protected static LamangSymbolDo[][] mockSymbolArr_handle_normal_bigWild_oneLine() {
        LamangSymbolDo[][] symbolArr = new LamangSymbolDo[4][5];
        // 第一列
        symbolArr[0][0] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][0] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON);
        symbolArr[2][0] = buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON);
        symbolArr[3][0] = buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON);

        // 第二列
        symbolArr[0][1] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][1] = buildASymbol(SymbolEnum.BigWild, SymbolTypeEnum.WILD);// big wild
        symbolArr[2][1] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[3][1] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);

        // 第三列
        symbolArr[0][2] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][2] = buildASymbol(SymbolEnum.Symbol1000, SymbolTypeEnum.WILD); // 占位元素
        symbolArr[2][2] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[3][2] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);

        // 第四列
        symbolArr[0][3] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][3] = buildASymbol(SymbolEnum.Symbol1000, SymbolTypeEnum.WILD);// 占位元素
        symbolArr[2][3] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[3][3] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);

        // 第五列
        symbolArr[0][4] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][4] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[2][4] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[3][4] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);
        return symbolArr;
    }

    protected static LamangSymbolDo[][] mockSymbolArr_handle_normal_superWild_oneLine() {
        LamangSymbolDo[][] symbolArr = new LamangSymbolDo[4][5];
        // 第一列
        symbolArr[0][0] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON);
        symbolArr[1][0] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON);
        symbolArr[2][0] = buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON);
        symbolArr[3][0] = buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON);

        // 第二列
        symbolArr[0][1] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][1] = buildASymbol(SymbolEnum.SuperWild, SymbolTypeEnum.WILD);// super wild
        symbolArr[2][1] = buildASymbol(SymbolEnum.Symbol1000, SymbolTypeEnum.WILD);// 占位元素
        symbolArr[3][1] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);

        // 第三列
        symbolArr[0][2] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][2] = buildASymbol(SymbolEnum.Symbol1000, SymbolTypeEnum.WILD);// 占位元素
        symbolArr[2][2] = buildASymbol(SymbolEnum.Symbol1000, SymbolTypeEnum.WILD);// 占位元素
        symbolArr[3][2] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);

        // 第四列
        symbolArr[0][3] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][3] = buildASymbol(SymbolEnum.Symbol1000, SymbolTypeEnum.WILD);// 占位元素
        symbolArr[2][3] = buildASymbol(SymbolEnum.Symbol1000, SymbolTypeEnum.WILD);// 占位元素
        symbolArr[3][3] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);

        // 第五列
        symbolArr[0][4] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON);
        symbolArr[1][4] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON);
        symbolArr[2][4] = buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON);
        symbolArr[3][4] = buildASymbol(SymbolEnum.Symbol1008, SymbolTypeEnum.COMMON);
        return symbolArr;
    }


    protected static LamangSymbolDo buildASymbol(SymbolEnum symbol, SymbolTypeEnum symbolType) {
        return buildASymbol(symbol, symbolType, false, false, false);
    }

    protected static LamangSymbolDo buildASymbol(SymbolEnum symbol, SymbolTypeEnum symbolType,
                                                 boolean symbolGoldBorder,
                                                 boolean jackpotDrawTimesAdded,
                                                 boolean freeWheelAdded) {
        LamangSymbolDo symbolDo = new LamangSymbolDo();
        symbolDo.setId(symbol.getId());
        symbolDo.setName(symbol.getName());
        symbolDo.setSymbolType(symbolType);
        symbolDo.setSymbolGoldBorder(symbolGoldBorder);
        symbolDo.setJackpotDrawTimesAdded(jackpotDrawTimesAdded);
        symbolDo.setFreeWheelAdded(freeWheelAdded);
        return symbolDo;
    }

    public static RewardHandleDo<LamangSymbolDo> buildTestRewardHandleDo(LamangSymbolDo[][] symbolArr, int wheelIndex, boolean isFreeFlag, long betAmount, BigDecimal currentMultiple) {
        return RewardHandleDo.<LamangSymbolDo>builder()
                .gameType(LamangGameHelper.LAMANG_GAME_INFO.getGameType())
                .gameInfo(LamangGameHelper.LAMANG_GAME_INFO)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .betAmount(betAmount)
                .wheel(WheelDo.<LamangSymbolDo>builder()
                        .index(wheelIndex)
                        .symbolArr(symbolArr)
                        .freeFlag(isFreeFlag)
                        .build())
                .currentMultiple(currentMultiple)
                .build();
    }
}
