package com.wegame.capi.test.biz.module.ibongadarna.game;

import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.IbongSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.ibongadarna.IbongAdarnaGameHelper;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolBreakUpEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;

import java.math.BigDecimal;

public class GameTestMocker {
    public static GamePlayDo mockGamePlayDo(BetSubTypeEnum betSubType, Long betAmount) {
        return GamePlayDo.builder()
                .gameType(GameTypeEnum.IBONG_ADARNA)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(betSubType)
                .betAmount(betAmount)
                .betRecordId(10000L)
                .build();
    }

}
