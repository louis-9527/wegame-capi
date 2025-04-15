package com.wegame.capi.biz.builder.req;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.singlewallet.BetStarSpinReq;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Date;
import java.util.Objects;

public class BetWithPayoutReqBuilder {

    public static BetWithPayoutReq buildReq(BetRecordDo betRecordDo) {
        long uctSecond = DateUtil.toUCTSecond(betRecordDo.getCreateTime());
        return BetWithPayoutReq.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .playerName(betRecordDo.getPlayerName())
                .refId(betRecordDo.getBetSerialNo())
                .ieId(Objects.nonNull(betRecordDo.getExtendInfo()) ? betRecordDo.getExtendInfo().getIeId() : null)
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
    public static BetStarSpinReq buildStarSpinReq(BetRecordDo betRecordDo) {
        long uctSecond = DateUtil.toUCTSecond(betRecordDo.getCreateTime());
        return BetStarSpinReq.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .playerName(betRecordDo.getPlayerName())
                .refId(betRecordDo.getBetSerialNo())
                .createTime(DateUtil.toUTC(new Date(betRecordDo.getCreateTime())))
                .betAmount(AmountUtil.toBigDecimal(betRecordDo.getPayAmount()))
                .payoutAmount(AmountUtil.toBigDecimal(betRecordDo.getPayOutAmount()))
                .betDetails(BetStarSpinReq.BetDetails.builder()
                        .gameId(betRecordDo.getGameId())
                        .roundNo(betRecordDo.getBetSerialNo())
                        .wagersDate(uctSecond)
                        .settlementDate(uctSecond)
                        .isFreeRound(BetSubTypeEnum.isFreeWheel(betRecordDo.getBetSubType()))
                        .jackpotDetails(new BetStarSpinReq.BetDetails.JackpotDetails())
                        .build())
                .build();
    }
}
