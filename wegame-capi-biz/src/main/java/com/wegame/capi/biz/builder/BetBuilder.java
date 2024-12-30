package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.client.domain.request.BetRequest;

public class BetBuilder {

    private BetBuilder() {
    }

    public static BetDo buildBetDo(BetRequest request) {
        return BetDo.builder()
                .gameId(request.getGameId())
                .betType(request.getBetType())
                .betDetail(request.getBetDetail())
                .build();
    }
}
