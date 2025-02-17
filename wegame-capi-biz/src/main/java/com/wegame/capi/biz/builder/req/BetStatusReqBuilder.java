package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.biz.domain.biz.BetRecordDo;

import java.util.Date;

import static com.wegame.capi.api.domain.req.singlewallet.BetStatusReq.BET_WITH_PAYOUT_BET_TYPE;

public class BetStatusReqBuilder {

    public static BetStatusReq buildStatusReq(BetRecordDo betRecordDo) {
        return BetStatusReq.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .playerName(betRecordDo.getPlayerName())
                .refId(betRecordDo.getBetSerialNo())
                .betType(BET_WITH_PAYOUT_BET_TYPE)
                .wagersDate(betRecordDo.getCreateTime() / 1000)
                .createTime(DateUtil.toUTC(new Date()))
                .build();
    }
}
