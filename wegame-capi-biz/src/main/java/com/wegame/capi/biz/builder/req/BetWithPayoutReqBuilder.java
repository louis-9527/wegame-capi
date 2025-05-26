package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.freespin.FreeSpinReq;
import com.wegame.capi.api.domain.req.singlewallet.BetStarSpinReq;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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

    public static BetWithPayoutReq buildReq(BetRecordDo betRecordDo, List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BetWithPayoutReq req = buildReq(betRecordDo);
        BetWithPayoutReq.BetDetails.JackpotDetails details = new BetWithPayoutReq.BetDetails.JackpotDetails();
        details.setJackpotPool(buildJackpotPool(jackpotRewardPoolDos));
        details.setJackpotContributionDetails(buildJackpotPoolCommission(betRecordDo));
        req.getBetDetails().setJackpotDetails(details);
        return req;
    }

    private static BetWithPayoutReq.BetDetails.JackpotContributionDetails buildJackpotPoolCommission(BetRecordDo betRecordDo) {
        BetWithPayoutReq.BetDetails.JackpotContributionDetails contributionDetails = new BetWithPayoutReq.BetDetails.JackpotContributionDetails();
        BetRecordDo.Jackpot jackpot =betRecordDo.getJackpot();
        if (Objects.nonNull(jackpot)) {
            contributionDetails.setGrandContributionAmount(AmountUtil.toBigDecimal(jackpot.getGrandContributionAmount()));
            contributionDetails.setMajorContributionAmount(AmountUtil.toBigDecimal(jackpot.getMajorContributionAmount()));
            contributionDetails.setMinorContributionAmount(AmountUtil.toBigDecimal(jackpot.getMinorContributionAmount()));
            contributionDetails.setMiniContributionAmount(AmountUtil.toBigDecimal(jackpot.getMiniContributionAmount()));

        }
        return contributionDetails;
    }

    private static BetWithPayoutReq.BetDetails.JackpotPool buildJackpotPool(List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BetWithPayoutReq.BetDetails.JackpotPool jackpotPool = new BetWithPayoutReq.BetDetails.JackpotPool();
        BigDecimal totalJackpotAmount = AmountUtil.toBigDecimal(jackpotRewardPoolDos.stream().mapToLong(JackpotRewardPoolDo::getAmount).sum());
        jackpotPool.setTotalJackpotAmount(totalJackpotAmount);
        jackpotRewardPoolDos.forEach(jackpotRewardPoolDo -> {
            switch (jackpotRewardPoolDo.getType()) {
                case grand:
                    jackpotPool.getGrandPool().setAmount(AmountUtil.toBigDecimal(jackpotRewardPoolDo.getAmount()));
                    jackpotPool.getGrandPool().setInitAmount(AmountUtil.toBigDecimal(jackpotRewardPoolDo.getInitAmount()));
                    break;
                case major:
                    jackpotPool.getMajorPool().setAmount(AmountUtil.toBigDecimal(jackpotRewardPoolDo.getAmount()));
                    jackpotPool.getMajorPool().setInitAmount(AmountUtil.toBigDecimal(jackpotRewardPoolDo.getInitAmount()));
                    break;
                case minor:
                    jackpotPool.getMinorPool().setAmount(AmountUtil.toBigDecimal(jackpotRewardPoolDo.getAmount()));
                    jackpotPool.getMinorPool().setInitAmount(AmountUtil.toBigDecimal(jackpotRewardPoolDo.getInitAmount()));
                    break;
                case mini:
                    jackpotPool.getMiniPool().setAmount(AmountUtil.toBigDecimal(jackpotRewardPoolDo.getAmount()));
                    jackpotPool.getMiniPool().setInitAmount(AmountUtil.toBigDecimal(jackpotRewardPoolDo.getInitAmount()));
                    break;
                default:break;
            }
        });
        return jackpotPool;
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
                        .isFreeRound(false)
                        .jackpotDetails(new BetStarSpinReq.BetDetails.JackpotDetails())
                        .build())
                .build();
    }
    public static FreeSpinReq buildFreeSpinReq(BetRecordDo betRecordDo) {
        long uctSecond = DateUtil.toUCTSecond(betRecordDo.getCreateTime());
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);
        return FreeSpinReq.builder()
                .pId(userInfo.getPId())
                .channelId(userInfo.getChannelId())
                .playerName(userInfo.getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
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
                .build();
    }
}
