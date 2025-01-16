package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.singlewallet.GetAuthLoginTokenReq;
import com.wegame.capi.biz.domain.UserLoginDo;
import org.slf4j.MDC;

import java.util.Date;

public class GetAuthLoginTokenReqBuilder {

    public static GetAuthLoginTokenReq build(UserLoginDo request) {
        return GetAuthLoginTokenReq.builder()
                .pId(request.getPId())
                .channelId(request.getChannelId())
                .playerName(request.getPlayerName())
                .createTime(DateUtil.toUTC(new Date()))
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .token(request.getToken())
                .build();
    }
}
