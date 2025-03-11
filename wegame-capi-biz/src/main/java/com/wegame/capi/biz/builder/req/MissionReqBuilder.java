package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.GetDailyMissionReq;
import com.wegame.capi.client.context.UserInfoContext;
import org.slf4j.MDC;

import java.util.Date;

public class MissionReqBuilder {
    public static GetDailyMissionReq build() {
        return GetDailyMissionReq.builder()
                .pId(UserInfoContext.getUserInfo().getPId())
                .channelId(UserInfoContext.getUserInfo().getChannelId())
                .playerName(UserInfoContext.getUserInfo().getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .build();
//        return GetDailyMissionReq.builder()
//                .pId("50")
//                .channelId("1050")
//                .playerName("cpcpmstest01")
//                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
//                .createTime(DateUtil.toUTC(new Date()))
//                .build();
    }
}
