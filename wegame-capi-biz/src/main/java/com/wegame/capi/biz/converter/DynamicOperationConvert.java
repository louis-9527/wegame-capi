package com.wegame.capi.biz.converter;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventMessageDo;
import com.wegame.capi.common.enums.OperationEventKindEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventSubStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationEventPo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class DynamicOperationConvert {


    public static DynamicOperationEventDo messageToDo(OperationEventMessageDo messageDo) {
        long currentMilli = DateUtil.getCurrentMilli();
        OperationEventKindEnum eventKindEnum = OperationEventKindEnum.valueOfCode(messageDo.getEventKind());
        if (Objects.isNull(eventKindEnum)) {
            return null;
        }
        BigDecimal bonus = messageDo.getBonus();
        BigDecimal minBonus = bonus.multiply(new BigDecimal("0.95"));
        DynamicOperationEventDo eventDo = new DynamicOperationEventDo();
        eventDo.setPlayerName(messageDo.getPlayerName());
        eventDo.setBonus(AmountUtil.toLong(bonus));
        eventDo.setMinBonus(AmountUtil.toLong(minBonus));
        eventDo.setMaxBonus(AmountUtil.toLong(bonus));
        eventDo.setFinishBonus(0L);
        eventDo.setEventStatus(DynamicOperationEventStatusEnum.NONE);
        eventDo.setReportStatus(DynamicOperationReportStatusEnum.NONE);
        eventDo.setEventId(messageDo.getEventId());
        eventDo.setEventKind(eventKindEnum);
        eventDo.setGameId(messageDo.getGameId());
        eventDo.setChannelId(messageDo.getChannelId());
        eventDo.setEffectedTime(DateUtil.utcStringToEpochMilli(messageDo.getEffectedTime()));
        eventDo.setExpiredTime(messageDo.getExpiredTime() == null ? DateUtil.getMaxMilli() : DateUtil.utcStringToEpochMilli(messageDo.getExpiredTime()));
        eventDo.setEventMsgDetail(JSON.toJSONString(messageDo));
        eventDo.setCreateTime(currentMilli);
        eventDo.setUpdateTime(currentMilli);
        return eventDo;
    }

    public static List<DynamicOperationEventDo> toDoList(List<DynamicOperationEventPo> poList) {
        return Optional.ofNullable(poList).orElse(Collections.emptyList())
                .stream()
                .map(DynamicOperationConvert::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static DynamicOperationEventDo poToDo(DynamicOperationEventPo eventPo) {
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
        eventDo.setEventKind(OperationEventKindEnum.valueOfCode(eventPo.getEventKind()));
        eventDo.setGameId(eventPo.getGameId());
        eventDo.setChannelId(eventPo.getChannelId());
        eventDo.setEffectedTime(eventPo.getEffectedTime());
        eventDo.setExpiredTime(eventPo.getExpiredTime());
        eventDo.setEventStatus(DynamicOperationEventStatusEnum.valueOfCode(eventPo.getEventStatus()));
        eventDo.setEventSubStatus(DynamicOperationEventSubStatusEnum.valueOfCode(eventPo.getEventSubStatus()));
        eventDo.setReportStatus(DynamicOperationReportStatusEnum.valueOfCode(eventPo.getReportStatus()));
        return eventDo;
    }

    public static DynamicOperationEventPo doToPo(DynamicOperationEventDo eventDo, OptEnum optEnum) {
        if (Objects.isNull(eventDo)) {
            return null;
        }
        long currentMilli = DateUtil.getCurrentMilli();
        DynamicOperationEventPo po = DynamicOperationEventPo.builder().build();
        po.setId(eventDo.getId());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setEventStatus(DynamicOperationEventStatusEnum.NONE.getCode());
            po.setEventSubStatus(DynamicOperationEventSubStatusEnum.NONE.getCode());
            po.setReportStatus(DynamicOperationReportStatusEnum.NONE.getCode());
            po.setEffectedTime(eventDo.getEffectedTime());
            po.setExpiredTime(eventDo.getExpiredTime());
            po.setPlayerName(eventDo.getPlayerName());
            po.setEventId(eventDo.getEventId());
            po.setEventKind(eventDo.getEventKind().getCode());
            po.setMinBonus(eventDo.getMinBonus());
            po.setMaxBonus(eventDo.getMaxBonus());
            po.setBonus(eventDo.getBonus());
            po.setFinishBonus(0L);
            po.setChannelId(eventDo.getChannelId());
            po.setGameId(eventDo.getGameId());
            po.setEventMsgDetail(eventDo.getEventMsgDetail());
            po.setCreateTime(currentMilli);
            po.setUpdateTime(currentMilli);
        } else {
            po.setEventStatus(Objects.isNull(eventDo.getEventStatus()) ? null : eventDo.getEventStatus().getCode());
            po.setEventSubStatus(Objects.isNull(eventDo.getEventSubStatus()) ? null : eventDo.getEventSubStatus().getCode());
            po.setReportStatus(Objects.isNull(eventDo.getReportStatus()) ? null : eventDo.getReportStatus().getCode());
            po.setUpdateTime(currentMilli);
        }
        return po;
    }
}
