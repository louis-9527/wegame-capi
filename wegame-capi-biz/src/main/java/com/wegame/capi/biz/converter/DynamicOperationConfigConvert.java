package com.wegame.capi.biz.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.capi.biz.domain.biz.game.GameMultipleRecordDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigPo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigRecordPo;

import java.util.ArrayList;
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
        configDo.setId(configPo.getId());
        configDo.setGameId(configPo.getGameId());
        configDo.setBetType(BetTypeEnum.valueOfCode(configPo.getBetType()));
        configDo.setMultiple(configPo.getMultiple());
        // configDo.setDetail(configPo.getDetail());
        return configDo;
    }


    public static <WD extends SymbolDo> List<GameMultipleRecordDo<WD>> convertRecordDos(List<DynamicOperationConfigRecordPo> configPos) {
        if (configPos == null) {
            return Collections.emptyList();
        }
        List<GameMultipleRecordDo<WD>> recordDos = new ArrayList<>();
        for (DynamicOperationConfigRecordPo config : configPos) {
            recordDos.add(convertRecordDo(config));
        }
        return recordDos;

    }

    public static <WD extends SymbolDo> GameMultipleRecordDo<WD> convertRecordDo(DynamicOperationConfigRecordPo configRecordPo) {
        if (Objects.isNull(configRecordPo)) {
            return null;
        }
        GameMultipleRecordDo<WD> configRecordDo = new GameMultipleRecordDo<WD>();
        configRecordDo.setId(configRecordPo.getId());
        configRecordDo.setParentId(configRecordPo.getParentId());
        configRecordDo.setBatchId(configRecordPo.getBatchId());
        configRecordDo.setBetRecordId(configRecordPo.getBetRecordId());
        configRecordDo.setIndex(configRecordPo.getIndex());
        configRecordDo.setFreeWheelFlag(configRecordPo.getFreeWheelFlag());
        configRecordDo.setGameRecordIds(configRecordPo.getGameRecordIds());
        configRecordDo.setWheelDo(JSON.parseObject(JSON.toJSONString(configRecordPo.getWheel()), new TypeReference<WheelDo<WD>>() {
        }));

        return configRecordDo;
    }
}
