package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.singlewallet.GetCreditReq;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import org.slf4j.MDC;

import java.util.Date;

public class GetCreditReqBuilder {

    public static GetCreditReq buildReq() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return GetCreditReq.builder()
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerName(userInfo.getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .build();
    }

    public static GetCreditReq buildReq(UserInfo userInfo) {
        return GetCreditReq.builder()
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerName(userInfo.getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .build();
    }
}
