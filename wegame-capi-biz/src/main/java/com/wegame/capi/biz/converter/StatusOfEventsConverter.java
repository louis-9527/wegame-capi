package com.wegame.capi.biz.converter;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.StatusOfEventsRecordDo;
import com.wegame.capi.biz.domain.biz.operation.StatusOfEventsResultDo;
import com.wegame.capi.client.domain.vo.StatusOfEventsRecordVo;
import com.wegame.capi.client.domain.vo.StatusOfEventsResultVo;
import com.wegame.capi.common.utils.AmountUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StatusOfEventsConverter {

    public static List<StatusOfEventsResultVo> toVoList(List<StatusOfEventsResultDo> statusOfEventsDos) {
        if (CollectionUtils.isEmpty(statusOfEventsDos)) {
            return new ArrayList<>();
        }
        return statusOfEventsDos.stream()
                .map(StatusOfEventsConverter::do2Vo)
                .collect(Collectors.toList());
    }

    private static StatusOfEventsResultVo do2Vo(StatusOfEventsResultDo statusOfEventsDo) {
        if (Objects.isNull(statusOfEventsDo)) {
            return null;
        }
        StatusOfEventsResultVo statusOfEventsVo = new StatusOfEventsResultVo();
        statusOfEventsVo.setEventId(statusOfEventsDo.getEventId());
        statusOfEventsVo.setExecuteBouns(Objects.isNull(statusOfEventsDo.getExecuteBouns()) ? null : AmountUtil.toBigDecimal(statusOfEventsDo.getExecuteBouns()));
        statusOfEventsVo.setExecuteTime(Objects.isNull(statusOfEventsDo.getExecuteTime()) ? null : DateUtil.toUCTByMilli(statusOfEventsDo.getExecuteTime()));
        statusOfEventsVo.setBetNos(statusOfEventsDo.getBetNos());
        statusOfEventsVo.setBetRecords(toRecordVoList(statusOfEventsDo.getBetRecords()));
        statusOfEventsVo.setEventStatus(statusOfEventsDo.getEventStatus());
        return statusOfEventsVo;
    }

    private static List<StatusOfEventsRecordVo> toRecordVoList(List<StatusOfEventsRecordDo> recordDos) {
        if (CollectionUtils.isEmpty(recordDos)) {
            return new ArrayList<>();
        }
        return recordDos.stream()
                .map(StatusOfEventsConverter::do2RecordVo)
                .collect(Collectors.toList());
    }

    private static StatusOfEventsRecordVo do2RecordVo(StatusOfEventsRecordDo recordDo) {
        if (Objects.isNull(recordDo)) {
            return null;
        }
        StatusOfEventsRecordVo recordVo = new StatusOfEventsRecordVo();
        recordVo.setBetNo(recordDo.getBetNo());
        recordVo.setOrderTime(DateUtil.toUCTByMilli(recordDo.getOrderTime()));
        recordVo.setSettlementTime(DateUtil.toUCTByMilli(recordDo.getSettlementTime()));
        recordVo.setBetAmount(Objects.isNull(recordDo.getBetAmount()) ? null : AmountUtil.toBigDecimal(recordDo.getBetAmount()));
        recordVo.setPayoutAmount(AmountUtil.toBigDecimal(recordDo.getPayoutAmount()));
        return recordVo;
    }
}

