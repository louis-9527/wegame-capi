package com.wegame.capi.biz.converter;

import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigRecordDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.OperationEventConfigPo;
import com.wegame.capi.dao.mapper.domain.business.OperationEventConfigRecordPo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DynamicOperationConfigConvert {
    public static List<DynamicOperationConfigDo> convertDos(List<OperationEventConfigPo> configPos) {
        if (configPos == null) {
            return Collections.emptyList();
        }
        return configPos.stream()
                .map(DynamicOperationConfigConvert::convertDo) // 复用已有的 convertDo 方法
                .collect(Collectors.toList());

    }

    public static DynamicOperationConfigDo convertDo(OperationEventConfigPo configPo) {
        if (Objects.isNull(configPo)) {
            return null;
        }
        DynamicOperationConfigDo configDo = new DynamicOperationConfigDo();
        configDo.setId(configPo.getId());
        configDo.setGameId(configPo.getGameId());
        configDo.setBetType(BetTypeEnum.valueOfCode(configPo.getBetType()));
        configDo.setMultiple(configPo.getMultiple());
        // configDo.setDetail(configPo.getDetail());
        return configDo;
    }


    public static List<DynamicOperationConfigRecordDo> convertRecordDos(List<OperationEventConfigRecordPo> configPos) {
        if (configPos == null) {
            return Collections.emptyList();
        }
        return configPos.stream()
                .map(DynamicOperationConfigConvert::convertRecordDo) // 复用已有的 convertDo 方法
                .collect(Collectors.toList());

    }

    public static DynamicOperationConfigRecordDo convertRecordDo(OperationEventConfigRecordPo configRecordPo) {
        if (Objects.isNull(configRecordPo)) {
            return null;
        }
        DynamicOperationConfigRecordDo configRecordDo = new DynamicOperationConfigRecordDo();
        configRecordDo.setId(configRecordPo.getId());
        configRecordDo.setParentId(configRecordPo.getParentId());
        configRecordDo.setBatchId(configRecordPo.getBatchId());
        configRecordDo.setBetRecordId(configRecordPo.getBetRecordId());
        configRecordDo.setIndex(configRecordPo.getIndex());
        configRecordDo.setGameRecordIds(configRecordPo.getGameRecordIds());
        configRecordDo.setWheel(configRecordPo.getWheel());

        return configRecordDo;
    }
}
