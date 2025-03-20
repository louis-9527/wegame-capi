package com.wegame.capi.biz.converter.operation;

import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.dao.mapper.domain.operation.OperationEventConfigRecordPo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class OperationEventConfigRecordConvert {

    public static List<OperationEventConfigRecordDo> poToDoList(List<OperationEventConfigRecordPo> poList) {
        return Optional.ofNullable(poList).orElse(Collections.emptyList())
                .stream()
                .map(OperationEventConfigRecordConvert::poToDo)
                .collect(Collectors.toList());
    }

    public static OperationEventConfigRecordDo poToDo(OperationEventConfigRecordPo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        OperationEventConfigRecordDo configRecordDo = new OperationEventConfigRecordDo();
        configRecordDo.setId(po.getId());
        configRecordDo.setParentId(po.getParentId());
        configRecordDo.setBatchId(po.getBatchId());
        configRecordDo.setBetRecordId(po.getBetRecordId());
        configRecordDo.setFreeWheelFlag(po.getFreeWheelFlag());
        configRecordDo.setIndex(po.getIndex());
        configRecordDo.setGameRecordIds(po.getGameRecordIds());
        configRecordDo.setWheel(po.getWheel());
        return configRecordDo;
    }

    public static OperationEventConfigRecordPo doToPo(OperationEventConfigRecordDo recordDo) {
        if (Objects.isNull(recordDo)) {
            return null;
        }
        OperationEventConfigRecordPo po = new OperationEventConfigRecordPo();
        po.setId(recordDo.getId());
        po.setParentId(recordDo.getParentId());
        po.setBatchId(recordDo.getBatchId());
        po.setBetRecordId(recordDo.getBetRecordId());
        po.setFreeWheelFlag(recordDo.getFreeWheelFlag());
        po.setIndex(recordDo.getIndex());
        po.setGameRecordIds(recordDo.getGameRecordIds());
        po.setWheel(recordDo.getWheel());
        return po;
    }
}
