package com.wegame.capi.biz.builder;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.GameBetStatusEnum;

public class BetRecordBuilder {

    public static <T> BetRecordDo build(BetDo betDo, Long betRecordId, Long betAmount, Long payAmount, T detail) {
        return BetRecordDo.builder()
                .id(betRecordId)
                .pId(UserInfoContext.getUserInfo().getPId())
                .channelId(UserInfoContext.getUserInfo().getChannelId())
                .playerId(UserInfoContext.getUserInfo().getPlayerId())
                .userId(Long.valueOf(UserInfoContext.getUserInfo().getId()))
                .playerName(UserInfoContext.getUserInfo().getPlayerName())
                .gameId(betDo.getGameId())
                .betSerialNo(String.valueOf(betRecordId))
                .betType(betDo.getBetType().getCode())
                .betSubType(betDo.getBetSubType().getCode())
                .betAmount(betAmount)
                .payOutAmount(payAmount)
                .status(GameBetStatusEnum.ING.getCode())
                .detail(detail)
                .build();
    }

}
