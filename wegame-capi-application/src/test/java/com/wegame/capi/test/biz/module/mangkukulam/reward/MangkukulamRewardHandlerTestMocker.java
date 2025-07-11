package com.wegame.capi.test.biz.module.mangkukulam.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamGameHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class MangkukulamRewardHandlerTestMocker {

    protected static MangkukulamSymbolDo[][] mock_one_special_wild(int times) {
        MangkukulamSymbolDo[][] symbolArr = new MangkukulamSymbolDo[5][5];
        // 第一列
        symbolArr[0][0] = buildNormalSymbol(SymbolEnum.SymbolH1);
        symbolArr[1][0] = buildNormalSymbol(SymbolEnum.SymbolH2);
        symbolArr[2][0] = buildNormalSymbol(SymbolEnum.SymbolH3);
        symbolArr[3][0] = buildNormalSymbol(SymbolEnum.SymbolH4);
        symbolArr[4][0] = MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;

        // 第二列
        symbolArr[0][1] = MangkukulamSymbolDo.buildPlaceholderWild();
        symbolArr[1][1] = MangkukulamSymbolDo.buildPlaceholderWild();
        symbolArr[2][1] = buildSpecialWildSymbol(times);
        symbolArr[3][1] = MangkukulamSymbolDo.buildPlaceholderWild();
        symbolArr[4][1] = MangkukulamSymbolDo.buildPlaceholderWild();

        // 第三列
        symbolArr[0][2] = buildNormalSymbol(SymbolEnum.SymbolH1);
        symbolArr[1][2] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[2][2] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[3][2] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[4][2] = buildNormalSymbol(SymbolEnum.SymbolL1);

        // 第四列
        symbolArr[0][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[1][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[2][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[3][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[4][3] = buildNormalSymbol(SymbolEnum.SymbolL1);

        // 第五列
        symbolArr[0][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[1][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[2][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[3][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[4][4] = MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;
        return symbolArr;
    }

    protected static MangkukulamSymbolDo[][] mock_two_special_wild(int times) {
        MangkukulamSymbolDo[][] symbolArr = new MangkukulamSymbolDo[5][5];
        // 第一列
        symbolArr[0][0] = buildNormalSymbol(SymbolEnum.SymbolH1);
        symbolArr[1][0] = buildNormalSymbol(SymbolEnum.SymbolH2);
        symbolArr[2][0] = buildNormalSymbol(SymbolEnum.SymbolH3);
        symbolArr[3][0] = buildNormalSymbol(SymbolEnum.SymbolH4);
        symbolArr[4][0] = MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;

        // 第二列
        symbolArr[0][1] = MangkukulamSymbolDo.buildPlaceholderWild();
        symbolArr[1][1] = MangkukulamSymbolDo.buildPlaceholderWild();
        symbolArr[2][1] = buildSpecialWildSymbol(times);
        symbolArr[3][1] = MangkukulamSymbolDo.buildPlaceholderWild();
        symbolArr[4][1] = MangkukulamSymbolDo.buildPlaceholderWild();

        // 第三列
        symbolArr[0][2] = MangkukulamSymbolDo.buildPlaceholderWild();
        symbolArr[1][2] = MangkukulamSymbolDo.buildPlaceholderWild();
        symbolArr[2][2] = buildSpecialWildSymbol(times);
        symbolArr[3][2] = MangkukulamSymbolDo.buildPlaceholderWild();
        symbolArr[4][2] = MangkukulamSymbolDo.buildPlaceholderWild();

        // 第四列
        symbolArr[0][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[1][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[2][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[3][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[4][3] = buildNormalSymbol(SymbolEnum.SymbolL1);

        // 第五列
        symbolArr[0][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[1][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[2][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[3][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[4][4] = MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;
        return symbolArr;
    }

    protected static MangkukulamSymbolDo[][] mock_reward_H1() {
        MangkukulamSymbolDo[][] symbolArr = new MangkukulamSymbolDo[5][5];
        // 第一列
        symbolArr[0][0] = buildNormalSymbol(SymbolEnum.SymbolH1);
        symbolArr[1][0] = buildNormalSymbol(SymbolEnum.SymbolH3);
        symbolArr[2][0] = buildNormalSymbol(SymbolEnum.SymbolH3);
        symbolArr[3][0] = buildNormalSymbol(SymbolEnum.SymbolH3);
        symbolArr[4][0] = MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;

        // 第二列
        symbolArr[0][1] = buildNormalSymbol(SymbolEnum.SymbolH1);
        symbolArr[1][1] = buildNormalSymbol(SymbolEnum.SymbolH2);
        symbolArr[2][1] = buildNormalSymbol(SymbolEnum.SymbolH2);
        symbolArr[3][1] = buildNormalSymbol(SymbolEnum.SymbolH2);
        symbolArr[4][1] = buildNormalSymbol(SymbolEnum.SymbolH2);

        // 第三列
        symbolArr[0][2] = buildNormalSymbol(SymbolEnum.SymbolH1);
        symbolArr[1][2] = buildNormalSymbol(SymbolEnum.SymbolH2);
        symbolArr[2][2] = buildNormalSymbol(SymbolEnum.SymbolH2);
        symbolArr[3][2] = buildNormalSymbol(SymbolEnum.SymbolH2);
        symbolArr[4][2] = buildNormalSymbol(SymbolEnum.SymbolH2);

        // 第四列
        symbolArr[0][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[1][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[2][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[3][3] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[4][3] = buildNormalSymbol(SymbolEnum.SymbolL1);

        // 第五列
        symbolArr[0][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[1][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[2][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[3][4] = buildNormalSymbol(SymbolEnum.SymbolL1);
        symbolArr[4][4] = MangkukulamGameHelper.MANGKUKULAM_SYMBOL_PLACEHOLDER;
        return symbolArr;
    }


    private static MangkukulamSymbolDo buildSpecialWildSymbol(int remainTimes) {
        List<BigDecimal> bigDecimals = Arrays.asList(BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4));
        MangkukulamSymbolDo symbolDo = MangkukulamSymbolDo.buildSpecialWild(remainTimes);
        symbolDo.setMultiples(bigDecimals);
        return symbolDo;
    }

    protected static MangkukulamSymbolDo buildNormalSymbol(SymbolEnum symbol) {
        return MangkukulamSymbolDo.buildFromSymbol(MangkukulamGameHelper.MANGKUKULAM_GAME_INFO.getSymbolById(symbol.getId()));
    }

    public static RewardHandleDo<MangkukulamSymbolDo> buildTestRewardHandleDo(MangkukulamSymbolDo[][] symbolArr, int wheelIndex, boolean isFreeFlag, long betAmount, BigDecimal currentMultiple) {
        return RewardHandleDo.<MangkukulamSymbolDo>builder()
                .gameType(MangkukulamGameHelper.MANGKUKULAM_GAME_INFO.getGameType())
                .gameInfo(MangkukulamGameHelper.MANGKUKULAM_GAME_INFO)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .betAmount(betAmount)
                .wheel(WheelDo.<MangkukulamSymbolDo>builder()
                        .index(wheelIndex)
                        .symbolArr(symbolArr)
                        .freeFlag(isFreeFlag)
                        .build())
                .currentMultiple(currentMultiple)
                .build();
    }
}
