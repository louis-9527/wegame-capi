package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.GetAchievementReq;
import com.wegame.capi.api.domain.req.promotion.GetTreasureReq;
import com.wegame.capi.api.domain.req.promotion.RedeemAchievementReq;
import com.wegame.capi.api.domain.req.promotion.RedeemTreasureReq;
import com.wegame.capi.api.domain.res.promotion.GetAchievementRes;
import com.wegame.capi.client.context.UserInfoContext;
import org.slf4j.MDC;

import java.util.Date;

public class AchievementReqBuilder {
    public static GetAchievementReq buildGetAchievementReq() {
        return GetAchievementReq.builder()
                .pId(UserInfoContext.getUserInfo().getPId())
                .channelId(UserInfoContext.getUserInfo().getChannelId())
                .playerName(UserInfoContext.getUserInfo().getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .build();
    }

    public static RedeemAchievementReq buildRedeemAchievementReq(String achievementId, String redeemId) {
        return RedeemAchievementReq.builder()
                .pId(UserInfoContext.getUserInfo().getPId())
                .channelId(UserInfoContext.getUserInfo().getChannelId())
                .playerName(UserInfoContext.getUserInfo().getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .achievementId(achievementId)
                .redeemId(redeemId)
                .build();
    }
}
