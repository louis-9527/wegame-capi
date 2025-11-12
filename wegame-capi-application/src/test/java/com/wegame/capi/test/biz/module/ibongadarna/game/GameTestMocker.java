package com.wegame.capi.test.biz.module.ibongadarna.game;

import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;

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
