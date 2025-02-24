package com.wegame.capi.test.biz.module.ibongadarna.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.ibongadarna.IbongAdarnaGameHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.SymbolBreakUpEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import java.math.BigDecimal;

public class RewardTestMocker {


    protected static IbongSymbolDo[][] mockSymbolArr_handler_normal_oneLine_test() {
        IbongSymbolDo[][] symbolArr = new IbongSymbolDo[3][3];
        // 第一列
        symbolArr[0][0] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][0] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][0] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);

        // 第二列
        symbolArr[0][1] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][1] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][1] = buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);

        // 第三列
        symbolArr[0][2] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][2] = buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][2] = buildASymbol(SymbolEnum.Symbol1006, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        return symbolArr;
    }

    protected static IbongSymbolDo[][] mockSymbolArr_handler_normal_twoLine_test() {
        IbongSymbolDo[][] symbolArr = new IbongSymbolDo[3][3];
        // 第一列
        symbolArr[0][0] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][0] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][0] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);

        // 第二列
        symbolArr[0][1] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][1] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][1] = buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);

        // 第三列
        symbolArr[0][2] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][2] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][2] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        return symbolArr;
    }

    protected static IbongSymbolDo[][] mockSymbolArr_handler_normal_wild_twoLine_test() {
        IbongSymbolDo[][] symbolArr = new IbongSymbolDo[3][3];
        // 第一列
        symbolArr[0][0] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][0] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][0] = buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);

        // 第二列
        symbolArr[0][1] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][1] = buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][1] = buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);

        // 第三列
        symbolArr[0][2] = buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][2] = buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][2] = buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolBreakUpEnum.NO_BREAK_UP);
        return symbolArr;
    }

    protected static IbongSymbolDo[][] mockSymbolArr_handler_normal_allWild_test() {
        IbongSymbolDo[][] symbolArr = new IbongSymbolDo[3][3];
        // 第一列
        symbolArr[0][0] = buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][0] = buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][0] = buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolBreakUpEnum.NO_BREAK_UP);

        // 第二列
        symbolArr[0][1] = buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][1] = buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][1] = buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolBreakUpEnum.NO_BREAK_UP);

        // 第三列
        symbolArr[0][2] = buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[1][2] = buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolBreakUpEnum.NO_BREAK_UP);
        symbolArr[2][2] = buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolBreakUpEnum.NO_BREAK_UP);
        return symbolArr;
    }

    protected static IbongSymbolDo buildASymbol(SymbolEnum symbol, SymbolTypeEnum symbolType, SymbolBreakUpEnum symbolBreakUpType) {
        IbongSymbolDo symbolDo = new IbongSymbolDo();
        symbolDo.setId(symbol.getId());
        symbolDo.setName(symbol.getName());
        symbolDo.setSymbolType(symbolType);
        symbolDo.setBreakUp(symbolBreakUpType);
        return symbolDo;
    }


    public static RewardHandleDo<IbongSymbolDo> buildTestRewardHandleDo(BetSubTypeEnum betSubType, IbongSymbolDo[][] symbolArr, long betAmount) {
        return RewardHandleDo.<IbongSymbolDo>builder()
                .gameType(IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO.getGameType())
                .gameInfo(IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(betSubType)
                .betAmount(betAmount)
                .wheel(WheelDo.<IbongSymbolDo>builder()
                        .index(0)
                        .symbolArr(symbolArr).build())
                .currentMultiple(BigDecimal.ZERO)
                .build();
    }

}
