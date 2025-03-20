package com.wegame.capi.biz.converter;

import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.OperationEventDetailPo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class OperationEventDetailConvert {

    public static OperationEventDetailPo toPo(DynamicOperationEventDetailDo detailDo, OptEnum optEnum) {
        if (Objects.isNull(detailDo)) {
            return null;
        }
        OperationEventDetailPo po = new OperationEventDetailPo();
        po.setId(detailDo.getId());
        if (OptEnum.ADD.equals(optEnum)) {
            po.setEventId(detailDo.getEventId());
            po.setOperatorRecordId(detailDo.getEventRecordId());
            po.setOperatorGameId(detailDo.getOperatorGameId());
            po.setBetTime(detailDo.getBetTime());
            po.setBetAmount(detailDo.getBetAmount());
            po.setEventAmount(detailDo.getEventAmount());
            po.setPlayerId(detailDo.getPlayerId());
            po.setPlayerName(detailDo.getPlayerName());
            po.setUserId(detailDo.getUserId());
            po.setPayOutAmount(detailDo.getPayOutAmount());
            po.setPayOutTime(detailDo.getPayOutTime());
            po.setBetId(detailDo.getBetId());
            po.setChannelId(detailDo.getChannelId());
            po.setGameId(detailDo.getGameId());
            po.setBetRecordId(detailDo.getBetRecordId());
            po.setBetStatus(detailDo.getBetStatus().getCode());
            po.setCreateTime(detailDo.getCreateTime());
            po.setUpdateTime(detailDo.getUpdateTime());
        } else {
            po.setUpdateTime(DateUtil.getCurrentMilli());
        }
        return po;
    }

    public static DynamicOperationEventDetailDo toDo(OperationEventDetailPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        DynamicOperationEventDetailDo detailDo = new DynamicOperationEventDetailDo();
        detailDo.setId(po.getId());
        detailDo.setEventRecordId(po.getOperatorRecordId());
        detailDo.setEventId(po.getEventId());
        detailDo.setOperatorGameId(po.getOperatorGameId());
        detailDo.setPlayerId(po.getPlayerId());
        detailDo.setBetAmount(po.getBetAmount());
        detailDo.setBetTime(po.getBetTime());
        detailDo.setEventAmount(po.getEventAmount());
        detailDo.setPlayerName(po.getPlayerName());
        detailDo.setUserId(po.getUserId());
        detailDo.setPayOutAmount(po.getPayOutAmount());
        detailDo.setPayOutTime(po.getPayOutTime());
        detailDo.setBetId(po.getBetId());
        detailDo.setBetRecordId(po.getBetRecordId());
        detailDo.setBetStatus(BetStatusEnum.valueOfCode(po.getBetStatus()));
        detailDo.setCreateTime(po.getCreateTime());
        detailDo.setUpdateTime(po.getUpdateTime());
        return detailDo;
    }

    public static List<DynamicOperationEventDetailDo> toDoList(List<OperationEventDetailPo> poList) {
        return Optional.ofNullable(poList).orElse(Collections.emptyList())
                .stream()
                .map(OperationEventDetailConvert::toDo)
                .collect(Collectors.toList());
    }
}
