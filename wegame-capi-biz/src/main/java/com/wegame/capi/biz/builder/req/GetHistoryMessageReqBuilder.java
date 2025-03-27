package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.GetHistoryMessageReq;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import org.slf4j.MDC;

import java.util.Date;

public class GetHistoryMessageReqBuilder {

    public static GetHistoryMessageReq buildReq(Integer pageNo, Integer pageSize) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return GetHistoryMessageReq.builder()
                .playerName(userInfo.getPlayerName())
                .pId(userInfo.getPId())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .channelId(userInfo.getChannelId())
                .createTime(DateUtil.toUTC(new Date()))
                .page(pageNo)
                .limit(pageSize)
                .build();
    }
}
