package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.RedeemPlayerCodeReq;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import org.slf4j.MDC;

import java.util.Date;

public class RedeemPlayerCodeReqBuilder extends Builder {

    public static RedeemPlayerCodeReq buildRedeemPlayerCodeReq(String code) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return RedeemPlayerCodeReq.builder()
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerName(userInfo.getPlayerName())
                .refId(buildRefId(MDC.get(ThreadMdcUtil.TRACE_ID), userInfo.getGameId()))
                .createTime(DateUtil.toUTC(new Date()))
                .code(code)
                .build();
    }

}
