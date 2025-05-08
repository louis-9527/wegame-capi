package com.wegame.capi.biz.converter.operation;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventMessageDo;
import com.wegame.capi.common.enums.OperationEventKindEnum;
import com.wegame.capi.common.enums.OperationEventStatusEnum;
import com.wegame.capi.common.enums.OperationEventSubStatusEnum;
import com.wegame.capi.common.enums.OperationEventReportStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.repository.domain.operation.OperationEventPo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class OperationEventConvert {

    public static OperationEventDo messageToDo(Long id, OperationEventMessageDo messageDo) {
        long currentMilli = DateUtil.getCurrentMilli();
        OperationEventKindEnum eventKindEnum = OperationEventKindEnum.valueOfCode(messageDo.getEventKind());
        if (Objects.isNull(eventKindEnum)) {
            return null;
        }
        BigDecimal bonus = messageDo.getBonus();
        BigDecimal minBonus = bonus.multiply(new BigDecimal("0.95"));
        OperationEventDo eventDo = new OperationEventDo();
        eventDo.setId(id);
        eventDo.setPlayerName(messageDo.getPlayerName());
        eventDo.setBonus(AmountUtil.toLong(bonus));
        eventDo.setMinBonus(AmountUtil.toLong(minBonus));
        eventDo.setMaxBonus(AmountUtil.toLong(bonus));
        eventDo.setFinishBonus(0L);
        eventDo.setEventStatus(OperationEventStatusEnum.NONE);
        eventDo.setReportStatus(OperationEventReportStatusEnum.NONE);
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

    public static List<OperationEventDo> poToDoList(List<OperationEventPo> poList) {
        return Optional.ofNullable(poList).orElse(Collections.emptyList())
                .stream()
                .map(OperationEventConvert::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static OperationEventDo poToDo(OperationEventPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        OperationEventDo eventDo = new OperationEventDo();
        eventDo.setId(po.getId());
        eventDo.setPlayerName(po.getPlayerName());
        eventDo.setBonus(po.getBonus());
        eventDo.setMinBonus(po.getMinBonus());
        eventDo.setMaxBonus(po.getMaxBonus());
        eventDo.setFinishBonus(po.getFinishBonus());
        eventDo.setEventId(po.getEventId());
        eventDo.setEventKind(OperationEventKindEnum.valueOfCode(po.getEventKind()));
        eventDo.setGameId(po.getGameId());
        eventDo.setChannelId(po.getChannelId());
        eventDo.setEffectedTime(po.getEffectedTime());
        eventDo.setExpiredTime(po.getExpiredTime());
        eventDo.setEventStatus(OperationEventStatusEnum.valueOfCode(po.getEventStatus()));
        eventDo.setEventSubStatus(OperationEventSubStatusEnum.valueOfCode(po.getEventSubStatus()));
        eventDo.setReportStatus(OperationEventReportStatusEnum.valueOfCode(po.getReportStatus()));
        eventDo.setUpdateTime(po.getUpdateTime());
        return eventDo;
    }

    public static OperationEventPo doToPo(OperationEventDo eventDo, OptEnum optEnum) {
        if (Objects.isNull(eventDo)) {
            return null;
        }
        long currentMilli = DateUtil.getCurrentMilli();
        OperationEventPo po = OperationEventPo.builder().build();
        po.setId(eventDo.getId());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setEventStatus(OperationEventStatusEnum.NONE.getCode());
            po.setEventSubStatus(OperationEventSubStatusEnum.NONE.getCode());
            po.setReportStatus(OperationEventReportStatusEnum.NONE.getCode());
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
