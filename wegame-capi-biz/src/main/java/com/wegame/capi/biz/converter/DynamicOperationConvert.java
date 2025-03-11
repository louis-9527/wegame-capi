package com.wegame.capi.biz.converter;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.common.enums.DynamicOperationEventKindEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventSubStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationPo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DynamicOperationConvert {

    public static DynamicOperationPo convertPo(DynamicOperationEventDo msgDo, OptEnum optEnum) {
        if (Objects.isNull(msgDo)) {
            return null;
        }
        DynamicOperationPo po = DynamicOperationPo.builder().build();
        po.setUpdateTime(DateUtil.getCurrentMilli());
        po.setId(msgDo.getId());
        if (OptEnum.ADD.equals(optEnum)) {
            long currentMilli = DateUtil.getCurrentMilli();
            po.setEventStatus(DynamicOperationEventStatusEnum.NONE.getCode());
            po.setEventSubStatus(DynamicOperationEventSubStatusEnum.NONE.getCode());
            po.setReportStatus(DynamicOperationReportStatusEnum.NONE.getCode());
            po.setEffectedTime(msgDo.getEffectedTime());
            po.setExpiredTime(msgDo.getExpiredTime());
            po.setPlayerName(msgDo.getPlayerName());
            po.setEventId(msgDo.getEventId());
            po.setEventKind(msgDo.getEventKind().getCode());
            po.setBetType(msgDo.getBetType().getCode());
            po.setMinBonus(msgDo.getMinBonus());
            po.setMaxBonus(msgDo.getMaxBonus());
            po.setBonus(msgDo.getBonus());
            po.setFinishBonus(0L);
            po.setChannelId(msgDo.getChannelId());
            po.setGameId(msgDo.getGameId());
            po.setEventMsgDetail(msgDo.getEventMsgDetail());
            po.setCreateTime(currentMilli);
        } else {
            po.setEventStatus(msgDo.getEventStatus() == null ? null : msgDo.getEventStatus().getCode());
            po.setEventSubStatus(msgDo.getEventSubStatus() == null ? null : msgDo.getEventSubStatus().getCode());
            po.setReportStatus(msgDo.getReportStatus() == null ? null : msgDo.getReportStatus().getCode());
        }
        return po;
    }


    public static List<DynamicOperationEventDo> convertDos(List<DynamicOperationPo> eventPos) {
        if (eventPos == null) {
            return Collections.emptyList();
        }
        return eventPos.stream()
                .map(DynamicOperationConvert::convertDo) // 复用已有的 convertDo 方法
                .collect(Collectors.toList());

    }

    public static DynamicOperationEventDo convertDo(DynamicOperationPo eventPo) {
        if (Objects.isNull(eventPo)) {
            return null;
        }
        DynamicOperationEventDo eventDo = new DynamicOperationEventDo();
        eventDo.setId(eventPo.getId());
        eventDo.setPlayerName(eventPo.getPlayerName());
        eventDo.setBonus(eventPo.getBonus());
        eventDo.setMinBonus(eventPo.getMinBonus());
        eventDo.setMaxBonus(eventPo.getMaxBonus());
        eventDo.setFinishBonus(eventPo.getFinishBonus());
        eventDo.setEventId(eventPo.getEventId());
        eventDo.setEventKind(DynamicOperationEventKindEnum.valueOfCode(eventPo.getEventKind()));
        eventDo.setGameId(eventPo.getGameId());
        eventDo.setChannelId(eventPo.getChannelId());
        eventDo.setEffectedTime(eventPo.getEffectedTime());
        eventDo.setExpiredTime(eventPo.getExpiredTime());
        eventDo.setEventStatus(DynamicOperationEventStatusEnum.valueOfCode(eventPo.getEventStatus()));
        eventDo.setEventSubStatus(DynamicOperationEventSubStatusEnum.valueOfCode(eventPo.getEventSubStatus()));
        eventDo.setReportStatus(DynamicOperationReportStatusEnum.valueOfCode(eventPo.getReportStatus()));
        return eventDo;
    }
}
