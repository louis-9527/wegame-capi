package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.GetTreasureReq;
import com.wegame.capi.api.domain.req.promotion.RedeemTreasureReq;
import com.wegame.capi.client.context.UserInfoContext;
import org.slf4j.MDC;

import java.util.Date;

public class TreasureReqBuilder {
    public static GetTreasureReq buildGetTreasureReq() {
        return GetTreasureReq.builder()
                .pId(UserInfoContext.getUserInfo().getPId())
                .channelId(UserInfoContext.getUserInfo().getChannelId())
                .playerName(UserInfoContext.getUserInfo().getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .build();
    }

    public static RedeemTreasureReq buildRedeemTreasureReq(String treasureId, Integer level) {
        return RedeemTreasureReq.builder()
                .pId(UserInfoContext.getUserInfo().getPId())
                .channelId(UserInfoContext.getUserInfo().getChannelId())
                .playerName(UserInfoContext.getUserInfo().getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .treasureId(treasureId)
                .level(level)
                .build();
    }
}
