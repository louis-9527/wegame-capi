package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Date;

public class BetWithPayoutReqBuilder {
    public static BetWithPayoutReq buildReq(BetRecordDo betRecordDo){
        return BetWithPayoutReq.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .playerName(betRecordDo.getPlayerName())
                .refId(betRecordDo.getBetSerialNo())
                .createTime(DateUtil.toUTC(new Date()))
                .betAmount(AmountUtil.toBigDecimal(betRecordDo.getBetAmount()))
                .payoutAmount(AmountUtil.toBigDecimal(betRecordDo.getPayOutAmount()))
                .betDetails(BetWithPayoutReq.BetDetails.builder()
                        .gameId(betRecordDo.getGameId())
                        .roundNo(betRecordDo.getBetSerialNo())
                        .wagersDate(DateUtil.getCurrentTimestamp())
                        .settlementDate(DateUtil.getCurrentTimestamp())
                        .isFreeRound(BetSubTypeEnum.isFreeFound(betRecordDo.getBetSubType()))
                        .jackpotDetails(new BetWithPayoutReq.BetDetails.JackpotDetails())
                        .build())
                .build();
    }

    public static BetSingleWalletResultDo buildRes(SingleWalletBetWithPayoutRes payoutRes){
        return BetSingleWalletResultDo.builder()
                .betId(payoutRes.getBetId())
                .refId(payoutRes.getRefId())
                .credit(payoutRes.getCredit())
                .build();
    }

    public static BetStatusReq buildStatusReq(BetRecordDo betRecordDo){
        return BetStatusReq.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .playerName(betRecordDo.getPlayerName())
                .refId(betRecordDo.getBetSerialNo())
                .betType(2)
                .wagersDate(betRecordDo.getCreateTime().getTime()/1000)
                .build();
    }
}
