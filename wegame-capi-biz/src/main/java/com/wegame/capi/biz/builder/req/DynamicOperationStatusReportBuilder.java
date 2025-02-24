package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsBetRecordReq;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.common.utils.AmountUtil;

public class DynamicOperationStatusReportBuilder {

    public static UpdateEventsBetRecordReq updateEventsBetRecordReqBuild(DynamicOperationEventDetailDo eventDetail){
        return UpdateEventsBetRecordReq.builder()
                .betNo(String.valueOf(eventDetail.getBetRecordId()))
                .betAmount(AmountUtil.toBigDecimal(eventDetail.getBetAmount()))
                .payoutAmount(AmountUtil.toBigDecimal(eventDetail.getPayOutAmount()))
                .orderTime(DateUtil.toUCTByMilli(eventDetail.getBetTime()))
                .settlementTime(DateUtil.toUCTByMilli(eventDetail.getPayOutTime()))
                .build();
    }
}
