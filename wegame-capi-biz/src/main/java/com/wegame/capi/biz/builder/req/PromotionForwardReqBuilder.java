package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.PromotionForwardReq;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import org.slf4j.MDC;

import java.util.Date;
import java.util.Map;

public class PromotionForwardReqBuilder extends Builder {

    public static PromotionForwardReq build(Map<String, Object> data) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return PromotionForwardReq.builder()
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerName(userInfo.getPlayerName())
                .refId(buildRefId(MDC.get(ThreadMdcUtil.TRACE_ID), userInfo.getGameId()))
                .createTime(DateUtil.toUTC(new Date()))
                .data(data)
                .build();
    }
}
