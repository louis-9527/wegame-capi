package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.operation.UpdateEventsBetRecordReq;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDetailDo;
import com.wegame.capi.common.utils.AmountUtil;

public class DynamicOperationStatusReportBuilder {

    public static UpdateEventsBetRecordReq updateEventsBetRecordReqBuild(OperationEventDetailDo eventDetail){
        return UpdateEventsBetRecordReq.builder()
                .betNo(eventDetail.getBetId())
                .betAmount(AmountUtil.toBigDecimal(eventDetail.getBetAmount()))
                .payoutAmount(AmountUtil.toBigDecimal(eventDetail.getPayOutAmount()))
                .orderTime(DateUtil.toUCTByMilli(eventDetail.getBetTime()))
                .settlementTime(DateUtil.toUCTByMilli(eventDetail.getPayOutTime()))
                .build();
    }
}
