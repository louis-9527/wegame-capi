package com.wegame.capi.biz.converter;

import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigPo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DynamicOperationConfigConvert {
    public static List<DynamicOperationConfigDo> convertDos(List<DynamicOperationConfigPo> configPos) {
        if (configPos == null) {
            return Collections.emptyList();
        }
        return configPos.stream()
                .map(DynamicOperationConfigConvert::convertDo) // 复用已有的 convertDo 方法
                .collect(Collectors.toList());

    }

    public static DynamicOperationConfigDo convertDo(DynamicOperationConfigPo configPo) {
        if (Objects.isNull(configPo)) {
            return null;
        }
        DynamicOperationConfigDo configDo = new DynamicOperationConfigDo();
        configDo.setId(configDo.getId());
        configDo.setGameId(configPo.getGameId());
        configDo.setBetType(BetTypeEnum.valueOfCode(configPo.getBetType()));
        configDo.setMultiple(configPo.getMultiple());
        configDo.setDetail(configPo.getDetail());
        return configDo;
    }
}
