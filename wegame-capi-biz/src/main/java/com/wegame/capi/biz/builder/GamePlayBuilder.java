package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GameOperationEventDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.client.enums.GamePlayTypeEnum;

import java.util.Objects;

public class GamePlayBuilder {

    public static GamePlayDo build(GamePlayTypeEnum gamePlayType, BetDo betDo, long betRecordId, GameOperationEventDo gameEventDo) {
        AssertUtil.assertNotNull(gamePlayType, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betDo, ErrorCode.ERR_PARAM);
        GamePlayDo build = GamePlayDo.builder()
                .gamePlayType(gamePlayType)
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetAmount())
                .payAmount(betDo.getPayAmount())
                .betRecordId(betRecordId)
                .operationEventConfigBetRecordId(Objects.nonNull(gameEventDo) ? gameEventDo.getConfigId() : null)
                .gameEventDo(gameEventDo)
                .volatility(betDo.getVolatility())
                .build();
        if(betDo.getParentBetRecordId() !=null){
            build.setContinuePlayContext(GamePlayDo.ContinuePlayContext.builder()
                    .parentBetRecordId(betDo.getParentBetRecordId())
                    .build());
        }
        return build;
    }
}
