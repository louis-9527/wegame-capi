package com.wegame.capi.biz.converter;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationStatusOfEventsDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationStatusOfEventsRecordDO;
import com.wegame.capi.client.domain.dto.CreditChangeNotifyDto;
import com.wegame.capi.client.domain.request.CreditChangeNotifyRequest;
import com.wegame.capi.client.domain.vo.DynamicOperationStatusOfEventsRecordVo;
import com.wegame.capi.client.domain.vo.DynamicOperationStatusOfEventsVo;
import com.wegame.capi.common.utils.AmountUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlatformCallbackConverter {

    public static CreditChangeNotifyDto requestToDto(CreditChangeNotifyRequest request) {
        if (Objects.isNull(request)) {
            return null;
        }

        return CreditChangeNotifyDto.builder()
                .channelId(request.getChannelId())
                .playerName(request.getPlayerName())
                .hash(request.getHash())
                .refId(request.getRefId())
                .createTime(request.getCreateTime())
                .credit(request.getCredit())
                .sId(request.getSId())
                .build();
    }

    public static List<DynamicOperationStatusOfEventsVo> dynamicOperationStatusOfEventsDoToVos(List<DynamicOperationStatusOfEventsDo> statusOfEventsDos) {
        if (CollectionUtils.isEmpty(statusOfEventsDos)) {
            return new ArrayList<>();
        }
        return statusOfEventsDos.stream()
                .map(PlatformCallbackConverter::dynamicOperationStatusOfEventsDoToVo)
                .collect(Collectors.toList());
    }

    public static DynamicOperationStatusOfEventsVo dynamicOperationStatusOfEventsDoToVo(DynamicOperationStatusOfEventsDo statusOfEventsDo) {
        if (statusOfEventsDo == null) {
            return null;
        }
        DynamicOperationStatusOfEventsVo statusOfEventsVo = new DynamicOperationStatusOfEventsVo();
        statusOfEventsVo.setEventId(statusOfEventsDo.getEventId());
        statusOfEventsVo.setExecuteBouns(statusOfEventsDo.getExecuteBouns() == null ? null : AmountUtil.toBigDecimal(statusOfEventsDo.getExecuteBouns()));
        statusOfEventsVo.setExecuteTime(statusOfEventsDo.getExecuteTime() == null ? null : DateUtil.toUCTByMilli(statusOfEventsDo.getExecuteTime()));
        statusOfEventsVo.setBetNos(statusOfEventsDo.getBetNos());
        statusOfEventsVo.setBetRecords(statusOfEventsRecordDoToVos(statusOfEventsDo.getBetRecords()));
        statusOfEventsVo.setEventStatus(statusOfEventsDo.getEventStatus());
        return statusOfEventsVo;
    }


    public static List<DynamicOperationStatusOfEventsRecordVo> statusOfEventsRecordDoToVos(List<DynamicOperationStatusOfEventsRecordDO> recordDos) {
        if (CollectionUtils.isEmpty(recordDos)) {
            return new ArrayList<>();
        }
        return recordDos.stream()
                .map(PlatformCallbackConverter::statusOfEventsRecordDoToVo)
                .collect(Collectors.toList());
    }

    public static DynamicOperationStatusOfEventsRecordVo statusOfEventsRecordDoToVo(DynamicOperationStatusOfEventsRecordDO recordDo) {
        if (recordDo == null) {
            return null;
        }
        DynamicOperationStatusOfEventsRecordVo recordVo = new DynamicOperationStatusOfEventsRecordVo();
        recordVo.setBetNo(recordDo.getBetNo());
        recordVo.setOrderTime(DateUtil.toUCTByMilli(recordDo.getOrderTime()));
        recordVo.setSettlementTime(DateUtil.toUCTByMilli(recordDo.getSettlementTime()));
        recordVo.setBetAmount(recordDo.getBetAmount() == null ? null : AmountUtil.toBigDecimal(recordDo.getBetAmount()));
        recordVo.setPayoutAmount(AmountUtil.toBigDecimal(recordDo.getPayoutAmount()));
        return recordVo;
    }
}

