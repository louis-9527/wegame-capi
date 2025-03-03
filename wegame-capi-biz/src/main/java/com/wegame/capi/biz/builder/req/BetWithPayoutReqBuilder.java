package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Date;

import static com.wegame.capi.api.domain.req.singlewallet.BetStatusReq.BET_WITH_PAYOUT_BET_TYPE;

public class BetWithPayoutReqBuilder {

    public static BetWithPayoutReq buildReq(BetRecordDo betRecordDo) {
        long uctSecond = DateUtil.toUCTSecond(betRecordDo.getCreateTime());
        return BetWithPayoutReq.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .playerName(betRecordDo.getPlayerName())
                .refId(betRecordDo.getBetSerialNo())
                .ieId(betRecordDo.getIeId())
                .createTime(DateUtil.toUTC(new Date(betRecordDo.getCreateTime())))
                .betAmount(AmountUtil.toBigDecimal(betRecordDo.getPayAmount()))
                .payoutAmount(AmountUtil.toBigDecimal(betRecordDo.getPayOutAmount()))
                .betDetails(BetWithPayoutReq.BetDetails.builder()
                        .gameId(betRecordDo.getGameId())
                        .roundNo(betRecordDo.getBetSerialNo())
                        .wagersDate(uctSecond)
                        .settlementDate(uctSecond)
                        .isFreeRound(BetSubTypeEnum.isFreeWheel(betRecordDo.getBetSubType()))
                        .jackpotDetails(new BetWithPayoutReq.BetDetails.JackpotDetails())
                        .build())
                .build();
    }

    //    public static BetStatusReq buildStatusReq(BetRecordDo betRecordDo) {
    //        return BetStatusReq.builder()
    //                .pId(betRecordDo.getPId())
    //                .channelId(betRecordDo.getChannelId())
    //                .playerName(betRecordDo.getPlayerName())
    //                .refId(betRecordDo.getBetSerialNo())
    //                .betType(BET_WITH_PAYOUT_BET_TYPE)
    //                .wagersDate(betRecordDo.getCreateTime() / 1000)
    //                .createTime(DateUtil.toUTC(new Date()))
    //                .build();
    //    }

    //    public static void main(String[] args) {
    //        long date = DateUtil.getCurrentMilli();
    //        System.out.println(date);
    //    }
}
