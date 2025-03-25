package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.client.enums.GamePlayTypeEnum;

public class GamePlayBuilder {

    public static GamePlayDo build(GamePlayTypeEnum gamePlayType, BetDo betDo, long betRecordId) {
        return GamePlayDo.builder()
                .gamePlayType(gamePlayType)
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetAmount())
                .payAmount(betDo.getPayAmount())
                .betRecordId(betRecordId)
                .build();
    }
}
