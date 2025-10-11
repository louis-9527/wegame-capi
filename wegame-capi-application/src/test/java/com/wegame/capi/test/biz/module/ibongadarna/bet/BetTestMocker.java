package com.wegame.capi.test.biz.module.ibongadarna.bet;

import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.IbongAdarnaBetDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;

public class BetTestMocker {

    public static BetDo mockGamePlayDo(BetSubTypeEnum betSubType, Long betAmount) {
        return BetDo.builder()
                .gameId(GameTypeEnum.IBONG_ADARNA.getId())
                .gameType(GameTypeEnum.IBONG_ADARNA)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(betSubType)
                .betAmount(100L)
                .payAmount(100L)
                .build();
    }
}
