package com.wegame.capi.test.biz.module.ibongadarna.bet;

import com.wegame.capi.biz.domain.biz.bet.BetContextDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;

public class BetTestMocker {

    public static BetContextDo mockGamePlayDo(BetSubTypeEnum betSubType, Long betAmount) {
        return BetContextDo.builder()
                .gameId(GameTypeEnum.IBONG_ADARNA.getId())
                .gameType(GameTypeEnum.IBONG_ADARNA)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(betSubType)
                .betAmount(100L)
                .payAmount(100L)
                .build();
    }
}
