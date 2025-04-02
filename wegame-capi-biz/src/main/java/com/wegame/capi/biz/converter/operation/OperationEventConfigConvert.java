package com.wegame.capi.biz.converter.operation;

import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.dao.reposity.domain.operation.OperationEventConfigPo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class OperationEventConfigConvert {

    public static List<OperationEventConfigDo> poToDoList(List<OperationEventConfigPo> poList) {
        return Optional.ofNullable(poList).orElse(Collections.emptyList())
                .stream()
                .map(OperationEventConfigConvert::poToDo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static OperationEventConfigDo poToDo(OperationEventConfigPo configPo) {
        if (Objects.isNull(configPo)) {
            return null;
        }
        OperationEventConfigDo configDo = new OperationEventConfigDo();
        configDo.setId(configPo.getId());
        configDo.setGameId(configPo.getGameId());
        configDo.setBetType(BetTypeEnum.valueOfCode(configPo.getBetType()));
        configDo.setMultiple(configPo.getMultiple());
        return configDo;
    }

    public static OperationEventConfigPo doToPo(OperationEventConfigDo configDo) {
        if (Objects.isNull(configDo)) {
            return null;
        }
        OperationEventConfigPo configPo = new OperationEventConfigPo();
        configPo.setId(configPo.getId());
        configPo.setGameId(configPo.getGameId());
        configPo.setBetType(Objects.nonNull(configDo.getBetType()) ? configDo.getBetType().getCode() : "");
        configPo.setMultiple(configPo.getMultiple());
        return configPo;
    }
}
