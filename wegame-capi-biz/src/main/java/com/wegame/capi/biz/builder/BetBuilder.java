package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.enums.BetTypeEnum;

public class BetBuilder {

    private BetBuilder() {
    }

    public static BetDo buildBetDo(BetRequest request) {
        return BetDo.builder()
                .gameId(request.getGameId())
                .betType(BetTypeEnum.valueOfCode(request.getBetType()))
                .betDetail(request.getBetDetail())
                .build();
    }
}
