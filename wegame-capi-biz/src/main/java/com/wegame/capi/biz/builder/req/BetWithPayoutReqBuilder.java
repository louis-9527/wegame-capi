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
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import org.slf4j.MDC;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
                        // todo 0元投注
                        .isFreeRound(false)
                        .jackpotDetails(new BetWithPayoutReq.BetDetails.JackpotDetails())
                        .build())
                .build();
    }

    public static BetWithPayoutReq buildReq(BetRecordDo betRecordDo, List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BetWithPayoutReq req = buildReq(betRecordDo);
        BetWithPayoutReq.BetDetails.JackpotDetails details = BetWithPayoutReq.BetDetails.JackpotDetails.builder()
                .jackpotPool(buildBetWithPayoutJackpotPool(jackpotRewardPoolDos))
                .jackpotContributionDetails(buildBetWithPayoutJackpotPoolCommission(betRecordDo))
                .build();

        req.getBetDetails().setJackpotDetails(details);
        return req;
    }

    private static BetWithPayoutReq.BetDetails.JackpotContributionDetails buildBetWithPayoutJackpotPoolCommission(BetRecordDo betRecordDo) {
        BetRecordDo.Jackpot jackpot = betRecordDo.getJackpot();
        if (Objects.nonNull(jackpot)) {
            return BetWithPayoutReq.BetDetails.JackpotContributionDetails.builder()
                    .grandContributionAmount(AmountUtil.toBigDecimal(jackpot.getGrandContributionAmount()))
                    .majorContributionAmount(AmountUtil.toBigDecimal(jackpot.getMajorContributionAmount()))
                    .minorContributionAmount(AmountUtil.toBigDecimal(jackpot.getMinorContributionAmount()))
                    .miniContributionAmount(AmountUtil.toBigDecimal(jackpot.getMiniContributionAmount()))
                    .betBonusContributionAmount(BigDecimal.ZERO)
                    .build();
        }
        return new BetWithPayoutReq.BetDetails.JackpotContributionDetails();
    }

    private static BetWithPayoutReq.BetDetails.JackpotPool buildBetWithPayoutJackpotPool(List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BigDecimal totalJackpotAmount = AmountUtil.toBigDecimal(jackpotRewardPoolDos.stream().mapToLong(JackpotRewardPoolDo::getAmount).sum());

        Map<JackpotRewardPoolTypeEnum, JackpotRewardPoolDo> rewardPoolMap = jackpotRewardPoolDos.stream()
                .collect(Collectors.toMap(
                        JackpotRewardPoolDo::getType,
                        pool -> pool,
                        (existing, replacement) -> existing // 处理 key 冲突
                ));
        JackpotRewardPoolDo grand = rewardPoolMap.get(JackpotRewardPoolTypeEnum.grand);
        BetWithPayoutReq.BetDetails.GrandPool grandPool = BetWithPayoutReq.BetDetails.GrandPool.builder()
                .amount(AmountUtil.toBigDecimal(grand.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(grand.getInitAmount()))
                .build();

        JackpotRewardPoolDo major = rewardPoolMap.get(JackpotRewardPoolTypeEnum.major);
        BetWithPayoutReq.BetDetails.MajorPool majorPool = BetWithPayoutReq.BetDetails.MajorPool.builder()
                .amount(AmountUtil.toBigDecimal(major.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(major.getInitAmount()))
                .build();

        JackpotRewardPoolDo minor = rewardPoolMap.get(JackpotRewardPoolTypeEnum.minor);
        BetWithPayoutReq.BetDetails.MinorPool minorPool = BetWithPayoutReq.BetDetails.MinorPool.builder()
                .amount(AmountUtil.toBigDecimal(minor.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(minor.getInitAmount()))
                .build();

        JackpotRewardPoolDo mini = rewardPoolMap.get(JackpotRewardPoolTypeEnum.mini);
        BetWithPayoutReq.BetDetails.MiniPool miniPool = BetWithPayoutReq.BetDetails.MiniPool.builder()
                .amount(AmountUtil.toBigDecimal(mini.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(mini.getInitAmount()))
                .build();

        BetWithPayoutReq.BetDetails.BetBonusPool betBonusPool = BetWithPayoutReq.BetDetails.BetBonusPool.builder()
                .amount(BigDecimal.ZERO)
                .initAmount(BigDecimal.ZERO)
                .build();

        return BetWithPayoutReq.BetDetails.JackpotPool.builder()
                .grandPool(grandPool)
                .majorPool(majorPool)
                .minorPool(minorPool)
                .miniPool(miniPool)
                .betBonusPool(betBonusPool)
                .totalJackpotAmount(totalJackpotAmount)
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
                        .isFreeRound(false)
                        .jackpotDetails(new BetStarSpinReq.BetDetails.JackpotDetails())
                        .build())
                .build();
    }

    public static BetStarSpinReq buildStarSpinReq(BetRecordDo betRecordDo, List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BetStarSpinReq req = buildStarSpinReq(betRecordDo);
        BetStarSpinReq.BetDetails.JackpotDetails details = BetStarSpinReq.BetDetails.JackpotDetails.builder()
                .jackpotPool(buildStarSpinJackpotPool(jackpotRewardPoolDos))
                .jackpotContributionDetails(buildStarCardJackpotPoolCommission(betRecordDo))
                .build();

        req.getBetDetails().setJackpotDetails(details);
        return req;
    }

    private static BetStarSpinReq.BetDetails.JackpotContributionDetails buildStarCardJackpotPoolCommission(BetRecordDo betRecordDo) {
        BetRecordDo.Jackpot jackpot = betRecordDo.getJackpot();
        if (Objects.nonNull(jackpot)) {
            return BetStarSpinReq.BetDetails.JackpotContributionDetails.builder()
                    .grandContributionAmount(AmountUtil.toBigDecimal(jackpot.getGrandContributionAmount()))
                    .majorContributionAmount(AmountUtil.toBigDecimal(jackpot.getMajorContributionAmount()))
                    .minorContributionAmount(AmountUtil.toBigDecimal(jackpot.getMinorContributionAmount()))
                    .miniContributionAmount(AmountUtil.toBigDecimal(jackpot.getMiniContributionAmount()))
                    .betBonusContributionAmount(BigDecimal.ZERO)
                    .build();
        }
        return new BetStarSpinReq.BetDetails.JackpotContributionDetails();
    }

    private static BetStarSpinReq.BetDetails.JackpotPool buildStarSpinJackpotPool(List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BigDecimal totalJackpotAmount = AmountUtil.toBigDecimal(jackpotRewardPoolDos.stream().mapToLong(JackpotRewardPoolDo::getAmount).sum());

        Map<JackpotRewardPoolTypeEnum, JackpotRewardPoolDo> rewardPoolMap = jackpotRewardPoolDos.stream()
                .collect(Collectors.toMap(
                        JackpotRewardPoolDo::getType,
                        pool -> pool,
                        (existing, replacement) -> existing // 处理 key 冲突
                ));
        JackpotRewardPoolDo grand = rewardPoolMap.get(JackpotRewardPoolTypeEnum.grand);
        BetStarSpinReq.BetDetails.GrandPool grandPool = BetStarSpinReq.BetDetails.GrandPool.builder()
                .amount(AmountUtil.toBigDecimal(grand.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(grand.getInitAmount()))
                .build();

        JackpotRewardPoolDo major = rewardPoolMap.get(JackpotRewardPoolTypeEnum.major);
        BetStarSpinReq.BetDetails.MajorPool majorPool = BetStarSpinReq.BetDetails.MajorPool.builder()
                .amount(AmountUtil.toBigDecimal(major.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(major.getInitAmount()))
                .build();

        JackpotRewardPoolDo minor = rewardPoolMap.get(JackpotRewardPoolTypeEnum.minor);
        BetStarSpinReq.BetDetails.MinorPool minorPool = BetStarSpinReq.BetDetails.MinorPool.builder()
                .amount(AmountUtil.toBigDecimal(minor.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(minor.getInitAmount()))
                .build();

        JackpotRewardPoolDo mini = rewardPoolMap.get(JackpotRewardPoolTypeEnum.mini);
        BetStarSpinReq.BetDetails.MiniPool miniPool = BetStarSpinReq.BetDetails.MiniPool.builder()
                .amount(AmountUtil.toBigDecimal(mini.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(mini.getInitAmount()))
                .build();

        BetStarSpinReq.BetDetails.BetBonusPool betBonusPool = BetStarSpinReq.BetDetails.BetBonusPool.builder()
                .amount(BigDecimal.ZERO)
                .initAmount(BigDecimal.ZERO)
                .build();

        return BetStarSpinReq.BetDetails.JackpotPool.builder()
                .grandPool(grandPool)
                .majorPool(majorPool)
                .minorPool(minorPool)
                .miniPool(miniPool)
                .betBonusPool(betBonusPool)
                .totalJackpotAmount(totalJackpotAmount)
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
                .betValue(AmountUtil.toBigDecimal(betRecordDo.getBetAmount()))
                .build();
    }
}
