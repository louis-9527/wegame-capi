package com.wegame.capi.biz.builder.req.level;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.level.LevelConfigReq;
import com.wegame.capi.api.domain.req.level.PlayerLevelReq;
import com.wegame.capi.api.domain.req.promotion.GetTreasureReq;
import com.wegame.capi.api.domain.req.promotion.RedeemTreasureReq;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import org.slf4j.MDC;

import java.util.Date;

public class LevelReqBuilder {

    public static LevelConfigReq buildLevelConfigReq() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return LevelConfigReq.builder()
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerName(userInfo.getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .build();
    }

    public static PlayerLevelReq buildPlayerLevelReq() {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return PlayerLevelReq.builder()
                .pId(UserInfoContext.getUserInfo().getPId())
                .channelId(userInfo.getChannelId())
                .playerName(userInfo.getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .build();
    }
}
