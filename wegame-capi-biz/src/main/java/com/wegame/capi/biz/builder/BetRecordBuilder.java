package com.wegame.capi.biz.builder;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.enums.BetStatusEnum;

import java.util.List;

public class BetRecordBuilder {

    public static <T> BetRecordDo<T> build(Long betRecordId,
                                           BetDo betDo,
                                           Long payOutAmount,
                                           Boolean reachMaxMultiple,
                                           List<T> gameRecordList) {
        Operator operator = OperatorContext.getOperator();
        AssertUtil.assertNotNull(operator, ErrorCode.ERR_PARAM);
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        long currentMilli = DateUtil.getCurrentMilli();
        return BetRecordDo.<T>builder()
                .id(betRecordId)
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerId(userInfo.getPlayerId())
                .userId(Long.valueOf(userInfo.getId()))
                .playerName(userInfo.getPlayerName())
                .gameId(betDo.getGameId())
                .betSerialNo(String.valueOf(betRecordId))
                .betType(betDo.getBetType().getCode())
                .betSubType(betDo.getBetSubType().getCode())
                .betAmount(betDo.getBetAmount())
                .payAmount(betDo.getPayAmount())
                .payOutAmount(payOutAmount)
                .reachMaxMultiple(reachMaxMultiple)
                .status(BetStatusEnum.ING.getCode())
                .creator(operator.getName())
                .updater(operator.getName())
                .createTime(currentMilli)
                .updateTime(currentMilli)
                .gameRecordList(gameRecordList)
                .build();
    }

}
