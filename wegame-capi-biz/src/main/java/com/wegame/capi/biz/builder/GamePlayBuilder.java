package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;

import java.util.Objects;

public class GamePlayBuilder {

    public static GamePlayDo build(BetDo betDo, long betRecordId) {
        AssertUtil.assertNotNull(betDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betDo.getGamePlayType(), ErrorCode.ERR_PARAM);
        return GamePlayDo.builder()
                .gamePlayType(betDo.getGamePlayType())
                .gameType(betDo.getGameType())
                .betType(betDo.getBetType())
                .betSubType(betDo.getBetSubType())
                .betAmount(betDo.getBetAmount())
                .payAmount(betDo.getPayAmount())
                .betRecordId(betRecordId)
                .operationEventConfigBetRecordId(Objects.nonNull(betDo.getGameOperationEvent()) ? betDo.getGameOperationEvent().getConfigId() : null)
                .gameEventDo(betDo.getGameOperationEvent())
                .volatility(betDo.getVolatility())
                .continuePlayContext(Objects.nonNull(betDo.getParentBetRecordId()) ? GamePlayDo.ContinuePlayContext.builder()
                        .parentBetRecordId(betDo.getParentBetRecordId())
                        .build() : null)
                .betPromotionType(betDo.getBetPromotionType())
                .build();
    }
}
