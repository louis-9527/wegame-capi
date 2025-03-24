package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.GetBagCurrentRewardReq;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import org.slf4j.MDC;

import java.util.Date;

public class GetBagCurrentRewardReqBuilder {
    private static final Integer BAG_CURRENT_REWORD_TYPE = 1;

    public static GetBagCurrentRewardReq buildReq() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return GetBagCurrentRewardReq.builder()
                .type(BAG_CURRENT_REWORD_TYPE)
                .playerName(userInfo.getPlayerName())
                .gameCode(userInfo.getGameId())
                .pId(userInfo.getPId())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .channelId(userInfo.getChannelId())
                .createTime(DateUtil.toUTC(new Date()))
                .build();
    }
}
