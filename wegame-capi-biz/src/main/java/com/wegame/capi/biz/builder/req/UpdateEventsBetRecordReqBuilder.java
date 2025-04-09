package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.operation.UpdateEventsBetRecordReq;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDetailDo;
import com.wegame.capi.common.utils.AmountUtil;

public class UpdateEventsBetRecordReqBuilder {

    public static UpdateEventsBetRecordReq buildReq(OperationEventDetailDo eventDetailDo){
        return UpdateEventsBetRecordReq.builder()
                .betNo(eventDetailDo.getBetId())
                .betAmount(AmountUtil.toBigDecimal(eventDetailDo.getBetAmount()))
                .payoutAmount(AmountUtil.toBigDecimal(eventDetailDo.getPayOutAmount()))
                .orderTime(DateUtil.toUTC(eventDetailDo.getBetTime()))
                .settlementTime(DateUtil.toUTC(eventDetailDo.getPayOutTime()))
                .build();
    }
}
