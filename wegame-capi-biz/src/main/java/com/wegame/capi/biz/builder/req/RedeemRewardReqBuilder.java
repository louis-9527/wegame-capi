package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.RedeemRewardReq;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.utils.AmountUtil;
import org.slf4j.MDC;

import java.util.Date;

public class RedeemRewardReqBuilder {

    public static RedeemRewardReq buildReq(String rewardId, Long payoutAmount, Long betWithPayoutRefId) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return RedeemRewardReq.builder()
                .playerName(userInfo.getPlayerName())
                .pId(userInfo.getPId())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .channelId(userInfo.getChannelId())
                .createTime(DateUtil.toUTC(new Date()))
                .rewardId(rewardId)
                .payoutAmount(AmountUtil.toBigDecimal(payoutAmount))
                .betWIthPayoutRefId(String.valueOf(betWithPayoutRefId))
                .build();
    }
}
