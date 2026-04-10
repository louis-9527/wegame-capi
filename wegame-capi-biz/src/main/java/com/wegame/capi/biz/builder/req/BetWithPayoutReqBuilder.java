package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.freespin.FreeSpinReq;
import com.wegame.capi.api.domain.req.singlewallet.BetStarSpinReq;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.promotion.RewardDetailDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.utils.AmountUtil;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

public class BetWithPayoutReqBuilder {

    public static BetWithPayoutReq buildReq(BetRecordDo betRecordDo) {
        long uctSecond = DateUtil.toUCTSecond(betRecordDo.getCreateTime());
        return BetWithPayoutReq.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .playerName(betRecordDo.getPlayerName())
                .refId(buildRefId(betRecordDo))
                .ieId(Objects.nonNull(betRecordDo.getExtendInfo()) ? betRecordDo.getExtendInfo().getIeId() : null)
                .createTime(DateUtil.toUTC(new Date(betRecordDo.getCreateTime())))
                .betAmount(AmountUtil.toBigDecimal(betRecordDo.getPayAmount()))
                .payoutAmount(AmountUtil.toBigDecimal(betRecordDo.getPayOutAmount()))
                .betDetails(BetWithPayoutReq.BetDetails.builder()
                        .gameId(betRecordDo.getGameId())
                        .roundNo(betRecordDo.getBetSerialNo())
                        .wagersDate(uctSecond)
                        .settlementDate(uctSecond)
                        .isFreeRound(false)
                        .jackpotDetails(new BetWithPayoutReq.BetDetails.JackpotDetails())
                        .build())
                .build();
    }

    public static BetStarSpinReq buildStarSpinReq(BetDo betDo, BetRecordDo betRecordDo) {
        long uctSecond = DateUtil.toUCTSecond(betRecordDo.getCreateTime());
        RewardDetailDo rewardDetail = betDo.getRewardDetail();
        boolean isFreeBet = Optional.ofNullable(rewardDetail.getDetails()).map(RewardDetailDo.RewardDetail::getIsFreeBet).orElse(false);

        BetStarSpinReq betStarSpinReq = BetStarSpinReq.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .playerName(betRecordDo.getPlayerName())
                .refId(buildRefId(betRecordDo))
                .createTime(DateUtil.toUTC(new Date(betRecordDo.getCreateTime())))
                .isFreeBet(isFreeBet)
                .build();
        if (isFreeBet) {
            betStarSpinReq.setBonusAmount(AmountUtil.toBigDecimal(betRecordDo.getPayAmount()));
            betStarSpinReq.setBonusId(betDo.getRewardId());
            betStarSpinReq.setEvent(Optional.of(rewardDetail).map(RewardDetailDo::getDetails).map(RewardDetailDo.RewardDetail::getName).orElse(""));
        } else {
            betStarSpinReq.setBetAmount(AmountUtil.toBigDecimal(betRecordDo.getPayAmount()));
            betStarSpinReq.setPayoutAmount(AmountUtil.toBigDecimal(betRecordDo.getPayAmount()));
            betStarSpinReq.setBetDetails(BetStarSpinReq.BetDetails.builder()
                    .gameId(betRecordDo.getGameId())
                    .roundNo(betRecordDo.getBetSerialNo())
                    .wagersDate(uctSecond)
                    .settlementDate(uctSecond)
                    .isFreeRound(false)
                    .jackpotDetails(new BetStarSpinReq.BetDetails.JackpotDetails())
                    .build());
        }
        return betStarSpinReq;
    }

    public static FreeSpinReq buildFreeSpinReq(BetRecordDo betRecordDo) {
        long uctSecond = DateUtil.toUCTSecond(betRecordDo.getCreateTime());
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return FreeSpinReq.builder()
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerName(userInfo.getPlayerName())
                .refId(buildRefId(betRecordDo))
                .createTime(DateUtil.toUTC(new Date()))
                .payoutAmount(AmountUtil.toBigDecimal(betRecordDo.getPayOutAmount()))
                .betDetails(FreeSpinReq.BetDetails.builder()
                        .gameId(betRecordDo.getGameId())
                        .roundNo(betRecordDo.getBetSerialNo())
                        .wagersDate(uctSecond)
                        .settlementDate(uctSecond)
                        .build())
                .freeSpinType(1)
                .campaignId(betRecordDo.getExtendInfo().getCampaignId())
                .betValue(AmountUtil.toBigDecimal(betRecordDo.getBetAmount()))
                .build();
    }

    public static String buildRefId(BetRecordDo betRecordDo) {
        return betRecordDo.getBetSerialNo() + betRecordDo.getGameId();
    }
}
