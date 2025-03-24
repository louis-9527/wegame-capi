package com.wegame.capi.biz.builder.operation;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.game.GameEventDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDetailDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.enums.BetStatusEnum;

public class OperationEventDetailBuilder {

    public static OperationEventDetailDo build(Long eventDetailId, BetRecordDo betRecordDo, GameEventDo eventDo) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        OperationEventDetailDo detailDo = new OperationEventDetailDo();
        detailDo.setId(eventDetailId);
        detailDo.setGameId(eventDo.getDynamicOperationEvent().getGameId());
        detailDo.setChannelId(eventDo.getDynamicOperationEvent().getChannelId());
        detailDo.setEventRecordId(eventDo.getDynamicOperationEvent().getId());
        detailDo.setEventId(eventDo.getDynamicOperationEvent().getEventId());
        detailDo.setConfigId(eventDo.getConfigId());
        detailDo.setBetRecordId(betRecordDo.getId());
        detailDo.setBetAmount(betRecordDo.getPayAmount());
        detailDo.setPayOutAmount(betRecordDo.getPayOutAmount());
        detailDo.setEventAmount(betRecordDo.getPayOutAmount() - betRecordDo.getPayAmount());
        detailDo.setBetTime(betRecordDo.getCreateTime());
        detailDo.setPayOutTime(betRecordDo.getCreateTime());
        detailDo.setPlayerName(userInfo.getPlayerName());
        detailDo.setPlayerId(userInfo.getPlayerId());
        detailDo.setUserId(userInfo.getId());
        long currentMilli = DateUtil.getCurrentMilli();
        detailDo.setCreateTime(currentMilli);
        detailDo.setUpdateTime(currentMilli);
        detailDo.setBetStatus(BetStatusEnum.ING); // todo 改成干预状态
        return detailDo;
    }
}
