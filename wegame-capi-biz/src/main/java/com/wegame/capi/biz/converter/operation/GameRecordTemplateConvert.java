package com.wegame.capi.biz.converter.operation;

import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.dao.repository.domain.template.GameRecordTemplatePo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameRecordTemplateConvert {

    public static List<GameRecordTemplateDo> poToDoList(List<GameRecordTemplatePo> poList) {
        return Optional.ofNullable(poList).orElse(Collections.emptyList())
                .stream()
                .map(GameRecordTemplateConvert::poToDo)
                .collect(Collectors.toList());
    }

    public static GameRecordTemplateDo poToDo(GameRecordTemplatePo po) {
        if (Objects.isNull(po)) {
            return null;
        }
        GameRecordTemplateDo configRecordDo = new GameRecordTemplateDo();
        configRecordDo.setId(po.getId());
        configRecordDo.setParentId(po.getParentId());
        configRecordDo.setBatchId(po.getBatchId());
        configRecordDo.setBetRecordId(po.getBetRecordId());
        configRecordDo.setIndex(po.getIndex());
        configRecordDo.setGameRecordIds(po.getGameRecordIds());
        configRecordDo.setWheel(po.getWheel());
        configRecordDo.setExtInfo(po.getExtInfo());
        return configRecordDo;
    }

    public static GameRecordTemplatePo doToPo(GameRecordTemplateDo recordDo) {
        if (Objects.isNull(recordDo)) {
            return null;
        }
        GameRecordTemplatePo po = new GameRecordTemplatePo();
        po.setId(recordDo.getId());
        po.setParentId(recordDo.getParentId());
        po.setBatchId(recordDo.getBatchId());
        po.setBetRecordId(recordDo.getBetRecordId());
        po.setIndex(recordDo.getIndex());
        po.setGameRecordIds(recordDo.getGameRecordIds());
        po.setWheel(recordDo.getWheel());
        po.setExtInfo(recordDo.getExtInfo());
        return po;
    }
}
