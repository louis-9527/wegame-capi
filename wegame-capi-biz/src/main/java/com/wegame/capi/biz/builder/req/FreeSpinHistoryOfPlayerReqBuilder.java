package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.freespin.FreeSpinHistoryOfPlayerReq;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Date;

@Slf4j
public class FreeSpinHistoryOfPlayerReqBuilder {

    public static FreeSpinHistoryOfPlayerReq buildReq(Integer page, Integer limit) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        // startTime endTime 传最近30天
        Date now = new Date();
        long startTime =  DateUtil.addDays(now, -30).getTime() / 1000;
        long endTime = now.getTime() / 1000;
        return FreeSpinHistoryOfPlayerReq.builder()
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerName(userInfo.getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .page(page)
                .limit(limit)
                .startTime((int) startTime)
                .endTime((int) endTime)
                .build();
    }
}
