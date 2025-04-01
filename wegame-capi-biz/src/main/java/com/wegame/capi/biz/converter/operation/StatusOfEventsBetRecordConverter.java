package com.wegame.capi.biz.converter.operation;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.StatusOfEventsRecordDo;
import com.wegame.capi.client.domain.vo.StatusOfEventsBetRecordVo;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatusOfEventsBetRecordConverter {

    public static List<StatusOfEventsBetRecordVo> doToVoList(List<StatusOfEventsRecordDo> doList) {
        return Optional.ofNullable(doList)
                .orElse(Collections.emptyList())
                .stream()
                .map(StatusOfEventsBetRecordConverter::doToVo)
                .collect(Collectors.toList());
    }

    private static StatusOfEventsBetRecordVo doToVo(StatusOfEventsRecordDo recordDo) {
        if (Objects.isNull(recordDo)) {
            return null;
        }
        StatusOfEventsBetRecordVo vo = new StatusOfEventsBetRecordVo();
        vo.setBetNo(recordDo.getBetNo());
        vo.setOrderTime(DateUtil.toUCTByMilli(recordDo.getOrderTime()));
        vo.setSettlementTime(DateUtil.toUCTByMilli(recordDo.getSettlementTime()));
        vo.setBetAmount(Objects.isNull(recordDo.getBetAmount()) ? null : AmountUtil.toBigDecimal(recordDo.getBetAmount()));
        vo.setPayoutAmount(AmountUtil.toBigDecimal(recordDo.getPayoutAmount()));
        return vo;
    }
}
