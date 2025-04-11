package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.FreeSpinOfPlayerReq;
import com.wegame.capi.api.domain.req.promotion.FreeSpinReq;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import org.slf4j.MDC;

import java.util.Date;

public class FreeSpinReqBuilder {

    public static FreeSpinReq buildReq(Integer payoutAmount, FreeSpinReq.BetDetails betDetails, Integer freeSpinType, String campaignId) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return FreeSpinReq.builder()
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerName(userInfo.getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .payoutAmount(payoutAmount)
                .betDetails(betDetails)
                .freeSpinType(freeSpinType)
                .campaignId(campaignId)
                .build();
    }
}
