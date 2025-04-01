package com.wegame.capi.biz.converter.operation;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.StatusOfEventsResultDo;
import com.wegame.capi.client.domain.vo.StatusOfEventsResultVo;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatusOfEventsConverter {

    public static List<StatusOfEventsResultVo> doToVoList(List<StatusOfEventsResultDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(StatusOfEventsConverter::doToVo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static StatusOfEventsResultVo doToVo(StatusOfEventsResultDo statusOfEventsDo) {
        if (Objects.isNull(statusOfEventsDo)) {
            return null;
        }
        StatusOfEventsResultVo vo = new StatusOfEventsResultVo();
        vo.setEventId(statusOfEventsDo.getEventId());
        vo.setExecuteBouns(Objects.isNull(statusOfEventsDo.getExecuteBouns()) ? null : AmountUtil.toBigDecimal(statusOfEventsDo.getExecuteBouns()));
        vo.setExecuteTime(Objects.isNull(statusOfEventsDo.getExecuteTime()) ? null : DateUtil.toUCTByMilli(statusOfEventsDo.getExecuteTime()));
        vo.setBetNos(statusOfEventsDo.getBetNos());
        vo.setBetRecords(StatusOfEventsBetRecordConverter.doToVoList(statusOfEventsDo.getBetRecords()));
        vo.setEventStatus(statusOfEventsDo.getEventStatus());
        return vo;
    }


}

