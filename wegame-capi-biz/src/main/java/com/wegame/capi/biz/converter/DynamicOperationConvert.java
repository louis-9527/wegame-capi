package com.wegame.capi.biz.converter;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationPo;

import java.util.Objects;

public class DynamicOperationConvert {

    public static DynamicOperationPo convertPo(DynamicOperationEventDo msgDo, OptEnum optEnum) {
        if (Objects.isNull(msgDo)) {
            return null;
        }
        DynamicOperationPo po = DynamicOperationPo.builder().build();
        po.setBonusBalance(msgDo.getBonus());
        po.setUpdateTime(DateUtil.getCurrentMilli());
        po.setId(msgDo.getId());
        if (OptEnum.ADD.equals(optEnum)) {
            long currentMilli = DateUtil.getCurrentMilli();
            po.setEventStatus(DynamicOperationEventStatusEnum.ING.getCode());
            po.setEffectedTime(msgDo.getEffectedTime());
            po.setExpiredTime(msgDo.getEffectedTime());
            po.setPlayerName(msgDo.getPlayerName());
            po.setEventId(msgDo.getEventId());
            po.setEventKind(msgDo.getEventKind());
            po.setBonus(msgDo.getBonus());
            po.setChannelId(msgDo.getChannelId());
            po.setOperatorGameId(msgDo.getOperatorGameId());
            po.setEventMsgDetail(JSON.toJSONString(msgDo));
            po.setCreateTime(currentMilli);
        } else {
            po.setEventStatus(msgDo.getEventStatus() == null ? null : msgDo.getEventStatus().getCode());
        }
        return po;
    }

    public static DynamicOperationEventDo convertDo(DynamicOperationPo eventPo) {
        if (Objects.isNull(eventPo)) {
            return null;
        }
        DynamicOperationEventDo eventDo = new DynamicOperationEventDo();
        eventDo.setId(eventPo.getId());
        eventDo.setPlayerName(eventPo.getPlayerName());
        eventDo.setBonus(eventPo.getBonus());
        eventDo.setEventId(eventPo.getEventId());
        eventDo.setEventKind(eventPo.getEventKind());
        eventDo.setOperatorGameId(eventPo.getOperatorGameId());
        eventDo.setChannelId(eventPo.getChannelId());
        eventDo.setEffectedTime(eventPo.getEffectedTime());
        eventDo.setExpiredTime(eventPo.getExpiredTime());
        return eventDo;
    }
}
