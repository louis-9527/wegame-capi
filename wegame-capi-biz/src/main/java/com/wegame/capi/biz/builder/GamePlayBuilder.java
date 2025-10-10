package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetContextDo;
import com.wegame.capi.biz.domain.biz.game.GameOperationEventDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.client.enums.GamePlayTypeEnum;

import java.util.Objects;

public class GamePlayBuilder {

    public static GamePlayDo build(BetContextDo betContextDo, long betRecordId, GameOperationEventDo gameEventDo) {
        AssertUtil.assertNotNull(betContextDo, ErrorCode.ERR_PARAM);
        return GamePlayDo.builder()
                .gamePlayType(betContextDo.getGamePlayType())
                .gameType(betContextDo.getGameType())
                .betType(betContextDo.getBetType())
                .betSubType(betContextDo.getBetSubType())
                .betAmount(betContextDo.getBetAmount())
                .payAmount(betContextDo.getPayAmount())
                .betRecordId(betRecordId)
                .operationEventConfigBetRecordId(Objects.nonNull(gameEventDo) ? gameEventDo.getConfigId() : null)
                .gameEventDo(gameEventDo)
                .build();
    }
}
