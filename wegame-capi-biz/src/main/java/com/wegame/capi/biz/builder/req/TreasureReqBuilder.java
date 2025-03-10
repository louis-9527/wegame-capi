package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.GetTreasureReq;
import com.wegame.capi.api.domain.req.promotion.RedeemTreasureReq;
import org.slf4j.MDC;

import java.util.Date;

public class TreasureReqBuilder {
    public static GetTreasureReq buildGetTreasureReq() {
//        return GetDailyMissionReq.builder()
//                .pId(UserInfoContext.getUserInfo().getPId())
//                .channelId(UserInfoContext.getUserInfo().getChannelId())
//                .playerName(UserInfoContext.getUserInfo().getPlayerName())
//                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
//                .createTime(DateUtil.toUTC(new Date()))
//                .build();
        return GetTreasureReq.builder()
                .pId("50")
                .channelId("1050")
                .playerName("cpcpmstest01")
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .build();
    }

    public static RedeemTreasureReq buildRedeemTreasureReq(String treasureId, Integer level) {
        return RedeemTreasureReq.builder()
                .pId("50")
                .channelId("1050")
                .playerName("cpcpmstest01")
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .treasureId(treasureId)
                .level(level)
                .build();
    }
}
