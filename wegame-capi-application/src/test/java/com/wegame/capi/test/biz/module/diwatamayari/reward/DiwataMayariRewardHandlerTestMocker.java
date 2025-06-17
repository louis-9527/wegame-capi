package com.wegame.capi.test.biz.module.diwatamayari.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Map;

public class DiwataMayariRewardHandlerTestMocker {

    private static final Map<String, SymbolDo> diwataMayariSymbolMap = DiwataMayariGameHelper.DIWATA_MAYARI_GAME_INFO.getSymbolMap();

    public static DiwataMayariSymbolDo[][] buildSymbolArr_zeroLine_test() {
        DiwataMayariSymbolDo[][] symbolArr = new DiwataMayariSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][4] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位
        symbolArr[0][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        // 第二列
        symbolArr[1][0] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][1] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][2] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][3] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][4] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        // 第三列
        symbolArr[2][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][4] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][5] = buildSymbolDo(SymbolEnum.Symbol1001);

        // 第四列
        symbolArr[3][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][4] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][5] = buildSymbolDo(SymbolEnum.Symbol1001);

        // 第五列
        symbolArr[4][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[4][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[4][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[4][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[4][4] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[4][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        // 第六列
        symbolArr[5][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[5][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[5][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[5][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[5][4] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位
        symbolArr[5][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        return symbolArr;
    }

    /**
     * 一条线，1001-4连
     *
     * @return
     */
    public static DiwataMayariSymbolDo[][] buildSymbolArr_oneLine_test() {
        DiwataMayariSymbolDo[][] symbolArr = new DiwataMayariSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][1] = buildSymbolDo(SymbolEnum.Symbol1002);
        symbolArr[0][2] = buildSymbolDo(SymbolEnum.Symbol1002);
        symbolArr[0][3] = buildSymbolDo(SymbolEnum.Symbol1002);
        symbolArr[0][4] = buildSymbolDo(SymbolEnum.Symbol1002); // 占位
        symbolArr[0][5] = buildSymbolDo(SymbolEnum.Symbol1002); // 占位

        // 第二列
        symbolArr[1][0] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][1] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][2] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[1][4] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        // 第三列
        symbolArr[2][0] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[2][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][2] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[2][3] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[2][4] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[2][5] = buildSymbolDo(SymbolEnum.Symbol1004);

        // 第四列
        symbolArr[3][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][1] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[3][2] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[3][3] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[3][4] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[3][5] = buildSymbolDo(SymbolEnum.Symbol1004);

        // 第五列
        symbolArr[4][0] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[4][1] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[4][2] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[4][3] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[4][4] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[4][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        // 第六列
        symbolArr[5][0] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[5][1] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[5][2] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[5][3] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[5][4] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位
        symbolArr[5][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        return symbolArr;
    }

    /**
     * 8条线，1001-3连
     *
     * @return
     */
    public static DiwataMayariSymbolDo[][] buildSymbolArr_eightLine_test() {
        DiwataMayariSymbolDo[][] symbolArr = new DiwataMayariSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][2] = buildSymbolDo(SymbolEnum.Symbol1002);
        symbolArr[0][3] = buildSymbolDo(SymbolEnum.Symbol1002);
        symbolArr[0][4] = buildSymbolDo(SymbolEnum.Symbol1002); // 占位
        symbolArr[0][5] = buildSymbolDo(SymbolEnum.Symbol1002); // 占位

        // 第二列
        symbolArr[1][0] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][1] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[1][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[1][4] = buildSymbolDo(SymbolEnum.Symbol1003);
        symbolArr[1][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        // 第三列
        symbolArr[2][0] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[2][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][3] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[2][4] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[2][5] = buildSymbolDo(SymbolEnum.Symbol1004);

        // 第四列
        symbolArr[3][0] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[3][1] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[3][2] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[3][3] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[3][4] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[3][5] = buildSymbolDo(SymbolEnum.Symbol1004);

        // 第五列
        symbolArr[4][0] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[4][1] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[4][2] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[4][3] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[4][4] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[4][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        // 第六列
        symbolArr[5][0] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[5][1] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[5][2] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[5][3] = buildSymbolDo(SymbolEnum.Symbol1004);
        symbolArr[5][4] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位
        symbolArr[5][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        return symbolArr;
    }

    public static DiwataMayariSymbolDo[][] buildSymbolArr_allLine_test() {
        DiwataMayariSymbolDo[][] symbolArr = new DiwataMayariSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[0][4] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位
        symbolArr[0][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        // 第二列
        symbolArr[1][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[1][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[1][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[1][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[1][4] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[1][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        // 第三列
        symbolArr[2][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][4] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[2][5] = buildSymbolDo(SymbolEnum.Symbol1001);

        // 第四列
        symbolArr[3][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][4] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[3][5] = buildSymbolDo(SymbolEnum.Symbol1001);

        // 第五列
        symbolArr[4][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[4][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[4][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[4][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[4][4] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[4][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        // 第六列
        symbolArr[5][0] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[5][1] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[5][2] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[5][3] = buildSymbolDo(SymbolEnum.Symbol1001);
        symbolArr[5][4] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位
        symbolArr[5][5] = buildSymbolDo(SymbolEnum.Symbol1000); // 占位

        return symbolArr;
    }


    public static RewardHandleDo<DiwataMayariSymbolDo> buildRewardHandleDo(DiwataMayariSymbolDo[][] symbolDoArr,
                                                                    long betAmount,
                                                                    int wheelIndex,
                                                                    boolean freeFlag,
                                                                    BigDecimal currentMultiple) {
        return RewardHandleDo.<DiwataMayariSymbolDo>builder()
                .gameType(GameTypeEnum.DIWATA_MAYARI)
                .gameInfo(DiwataMayariGameHelper.DIWATA_MAYARI_GAME_INFO)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .gamePlayType(GamePlayTypeEnum.NORMAL_PLAY)
                .betAmount(betAmount)
                .currentMultiple(currentMultiple)
                .currentTotalRewardAmount(100000L)
                .wheel(WheelDo.<DiwataMayariSymbolDo>builder().symbolArr(symbolDoArr).index(wheelIndex).freeFlag(freeFlag).build())
                .build();
    }


    private static DiwataMayariSymbolDo buildSymbolDo(SymbolEnum symbol) {
        SymbolDo symbolDo = diwataMayariSymbolMap.get(symbol.getId());
        Assert.assertNotNull(symbolDo);
        return DiwataMayariSymbolDo.builder().id(symbolDo.getId()).name(symbolDo.getName()).symbolType(symbolDo.getSymbolType()).build();
    }


}
