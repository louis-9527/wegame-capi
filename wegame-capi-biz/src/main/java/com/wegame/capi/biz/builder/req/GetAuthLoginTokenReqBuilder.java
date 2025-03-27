package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.singlewallet.GetAuthLoginTokenReq;
import com.wegame.capi.biz.domain.user.UserLoginDo;
import org.slf4j.MDC;

import java.util.Date;

public class GetAuthLoginTokenReqBuilder {

    public static GetAuthLoginTokenReq buildReq(UserLoginDo userLoginDo) {
        return GetAuthLoginTokenReq.builder()
                .pId(userLoginDo.getPId())
                .channelId(userLoginDo.getChannelId())
                .playerName(userLoginDo.getPlayerName())
                .createTime(DateUtil.toUTC(new Date()))
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .token(userLoginDo.getToken())
                .build();
    }
}
