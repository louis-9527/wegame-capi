package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.freespin.FreeSpinReq;
import com.wegame.capi.api.domain.req.singlewallet.BetStarSpinReq;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.promotion.RewardDetailDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.common.utils.JackpotAmountUtil;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.BetRecordBuilder.extend_field_campaignId;
import static com.wegame.capi.biz.builder.BetRecordBuilder.extend_field_ieId;

public class BetWithPayoutReqBuilder {

    public static BetWithPayoutReq buildReq(BetRecordDo betRecordDo) {
        long uctSecond = DateUtil.toUCTSecond(betRecordDo.getCreateTime());
        return BetWithPayoutReq.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .playerName(betRecordDo.getPlayerName())
                .refId(buildRefId(betRecordDo))
                .ieId(MapUtils.isNotEmpty(betRecordDo.getExtendInfo()) ? String.valueOf(betRecordDo.getExtendInfo().get(extend_field_ieId)) : null)
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
                    .grandContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getGrandContributionAmount()))
                    .majorContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMajorContributionAmount()))
                    .minorContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMinorContributionAmount()))
                    .miniContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMiniContributionAmount()))

                    .grandSupContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getGrandSupContributionAmount()))
                    .majorSupContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMajorSupContributionAmount()))
                    .minorSupContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMinorSupContributionAmount()))
                    .miniSupContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMiniSupContributionAmount()))
                    .betBonusContributionAmount(BigDecimal.ZERO)
                    .build();
        }
        return new BetWithPayoutReq.BetDetails.JackpotContributionDetails();
    }

    private static BetWithPayoutReq.BetDetails.JackpotPool buildBetWithPayoutJackpotPool(List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BigDecimal totalJackpotAmount = JackpotAmountUtil.toBigDecimal(jackpotRewardPoolDos.stream().mapToLong(JackpotRewardPoolDo::getAmount).sum());
        BigDecimal totalSupJackpotAmount = JackpotAmountUtil.toBigDecimal(jackpotRewardPoolDos.stream().mapToLong(JackpotRewardPoolDo::getReservedAmount).sum());

        Map<JackpotRewardPoolTypeEnum, JackpotRewardPoolDo> rewardPoolMap = jackpotRewardPoolDos.stream()
                .collect(Collectors.toMap(
                        JackpotRewardPoolDo::getType,
                        pool -> pool,
                        (existing, replacement) -> existing // 处理 key 冲突
                ));
        JackpotRewardPoolDo grand = rewardPoolMap.get(JackpotRewardPoolTypeEnum.grand);
        BetWithPayoutReq.BetDetails.SupplementaryPool grandSupplementaryPool = BetWithPayoutReq.BetDetails.SupplementaryPool.builder()
                .initAmount(JackpotAmountUtil.toBigDecimal(grand.getReservedPoolInitAmount()))
                .amount(JackpotAmountUtil.toBigDecimal(grand.getReservedAmount()))
                .build();
        BetWithPayoutReq.BetDetails.GrandPool grandPool = BetWithPayoutReq.BetDetails.GrandPool.builder()
                .amount(JackpotAmountUtil.toBigDecimal(grand.getAmount()))
                .initAmount(JackpotAmountUtil.toBigDecimal(grand.getInitAmount()))
                .supplementaryPool(grandSupplementaryPool)
                .build();

        JackpotRewardPoolDo major = rewardPoolMap.get(JackpotRewardPoolTypeEnum.major);
        BetWithPayoutReq.BetDetails.SupplementaryPool majorSupplementaryPool = BetWithPayoutReq.BetDetails.SupplementaryPool.builder()
                .initAmount(JackpotAmountUtil.toBigDecimal(major.getReservedPoolInitAmount()))
                .amount(JackpotAmountUtil.toBigDecimal(major.getReservedAmount()))
                .build();
        BetWithPayoutReq.BetDetails.MajorPool majorPool = BetWithPayoutReq.BetDetails.MajorPool.builder()
                .amount(JackpotAmountUtil.toBigDecimal(major.getAmount()))
                .initAmount(JackpotAmountUtil.toBigDecimal(major.getInitAmount()))
                .supplementaryPool(majorSupplementaryPool)
                .build();

        JackpotRewardPoolDo minor = rewardPoolMap.get(JackpotRewardPoolTypeEnum.minor);
        BetWithPayoutReq.BetDetails.SupplementaryPool minorSupplementaryPool = BetWithPayoutReq.BetDetails.SupplementaryPool.builder()
                .initAmount(JackpotAmountUtil.toBigDecimal(minor.getReservedPoolInitAmount()))
                .amount(JackpotAmountUtil.toBigDecimal(minor.getReservedAmount()))
                .build();
        BetWithPayoutReq.BetDetails.MinorPool minorPool = BetWithPayoutReq.BetDetails.MinorPool.builder()
                .amount(JackpotAmountUtil.toBigDecimal(minor.getAmount()))
                .initAmount(JackpotAmountUtil.toBigDecimal(minor.getInitAmount()))
                .supplementaryPool(minorSupplementaryPool)
                .build();

        JackpotRewardPoolDo mini = rewardPoolMap.get(JackpotRewardPoolTypeEnum.mini);
        BetWithPayoutReq.BetDetails.SupplementaryPool miniSupplementaryPool = BetWithPayoutReq.BetDetails.SupplementaryPool.builder()
                .initAmount(JackpotAmountUtil.toBigDecimal(mini.getReservedPoolInitAmount()))
                .amount(JackpotAmountUtil.toBigDecimal(mini.getReservedAmount()))
                .build();
        BetWithPayoutReq.BetDetails.MiniPool miniPool = BetWithPayoutReq.BetDetails.MiniPool.builder()
                .amount(JackpotAmountUtil.toBigDecimal(mini.getAmount()))
                .initAmount(JackpotAmountUtil.toBigDecimal(mini.getInitAmount()))
                .supplementaryPool(miniSupplementaryPool)
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
                .totalSupJackpotAmount(totalSupJackpotAmount)
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
            betStarSpinReq.setBonusAmount(AmountUtil.toBigDecimal(betRecordDo.getPayOutAmount()));
            betStarSpinReq.setBonusId(betDo.getRewardId());
            betStarSpinReq.setEvent(Optional.of(rewardDetail).map(RewardDetailDo::getDetails).map(RewardDetailDo.RewardDetail::getName).orElse(""));
        } else {
            betStarSpinReq.setBetAmount(AmountUtil.toBigDecimal(betRecordDo.getPayAmount()));
            betStarSpinReq.setPayoutAmount(AmountUtil.toBigDecimal(betRecordDo.getPayOutAmount()));
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

    public static BetStarSpinReq buildStarSpinReq(BetDo betDo, BetRecordDo betRecordDo, List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BetStarSpinReq req = buildStarSpinReq(betDo, betRecordDo);
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
                    .grandContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getGrandContributionAmount()))
                    .majorContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMajorContributionAmount()))
                    .minorContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMinorContributionAmount()))
                    .miniContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMiniContributionAmount()))

                    .grandSupContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getGrandSupContributionAmount()))
                    .majorSupContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMajorSupContributionAmount()))
                    .minorSupContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMinorSupContributionAmount()))
                    .miniSupContributionAmount(JackpotAmountUtil.toBigDecimal(jackpot.getMiniSupContributionAmount()))
                    .betBonusContributionAmount(BigDecimal.ZERO)
                    .build();
        }
        return new BetStarSpinReq.BetDetails.JackpotContributionDetails();
    }

    private static BetStarSpinReq.BetDetails.JackpotPool buildStarSpinJackpotPool(List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BigDecimal totalJackpotAmount = JackpotAmountUtil.toBigDecimal(jackpotRewardPoolDos.stream().mapToLong(JackpotRewardPoolDo::getAmount).sum());
        BigDecimal totalSupJackpotAmount = JackpotAmountUtil.toBigDecimal(jackpotRewardPoolDos.stream().mapToLong(JackpotRewardPoolDo::getReservedAmount).sum());

        Map<JackpotRewardPoolTypeEnum, JackpotRewardPoolDo> rewardPoolMap = jackpotRewardPoolDos.stream()
                .collect(Collectors.toMap(
                        JackpotRewardPoolDo::getType,
                        pool -> pool,
                        (existing, replacement) -> existing // 处理 key 冲突
                ));
        JackpotRewardPoolDo grand = rewardPoolMap.get(JackpotRewardPoolTypeEnum.grand);
        BetStarSpinReq.BetDetails.SupplementaryPool grandSupplementaryPool = BetStarSpinReq.BetDetails.SupplementaryPool.builder()
                .amount(AmountUtil.toBigDecimal(grand.getReservedAmount()))
                .initAmount(AmountUtil.toBigDecimal(grand.getReservedPoolInitAmount()))
                .build();
        BetStarSpinReq.BetDetails.GrandPool grandPool = BetStarSpinReq.BetDetails.GrandPool.builder()
                .amount(AmountUtil.toBigDecimal(grand.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(grand.getInitAmount()))
                .supplementaryPool(grandSupplementaryPool)
                .build();

        JackpotRewardPoolDo major = rewardPoolMap.get(JackpotRewardPoolTypeEnum.major);
        BetStarSpinReq.BetDetails.SupplementaryPool majorSupplementaryPool = BetStarSpinReq.BetDetails.SupplementaryPool.builder()
                .amount(AmountUtil.toBigDecimal(major.getReservedAmount()))
                .initAmount(AmountUtil.toBigDecimal(major.getReservedPoolInitAmount()))
                .build();
        BetStarSpinReq.BetDetails.MajorPool majorPool = BetStarSpinReq.BetDetails.MajorPool.builder()
                .amount(AmountUtil.toBigDecimal(major.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(major.getInitAmount()))
                .supplementaryPool(majorSupplementaryPool)
                .build();

        JackpotRewardPoolDo minor = rewardPoolMap.get(JackpotRewardPoolTypeEnum.minor);
        BetStarSpinReq.BetDetails.SupplementaryPool minorSupplementaryPool = BetStarSpinReq.BetDetails.SupplementaryPool.builder()
                .amount(AmountUtil.toBigDecimal(minor.getReservedAmount()))
                .initAmount(AmountUtil.toBigDecimal(minor.getReservedPoolInitAmount()))
                .build();
        BetStarSpinReq.BetDetails.MinorPool minorPool = BetStarSpinReq.BetDetails.MinorPool.builder()
                .amount(AmountUtil.toBigDecimal(minor.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(minor.getInitAmount()))
                .supplementaryPool(minorSupplementaryPool)
                .build();

        JackpotRewardPoolDo mini = rewardPoolMap.get(JackpotRewardPoolTypeEnum.mini);
        BetStarSpinReq.BetDetails.SupplementaryPool miniSupplementaryPool = BetStarSpinReq.BetDetails.SupplementaryPool.builder()
                .amount(AmountUtil.toBigDecimal(mini.getReservedAmount()))
                .initAmount(AmountUtil.toBigDecimal(mini.getReservedPoolInitAmount()))
                .build();
        BetStarSpinReq.BetDetails.MiniPool miniPool = BetStarSpinReq.BetDetails.MiniPool.builder()
                .amount(AmountUtil.toBigDecimal(mini.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(mini.getInitAmount()))
                .supplementaryPool(miniSupplementaryPool)
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
                .totalSupJackpotAmount(totalSupJackpotAmount)
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
                .campaignId(MapUtils.isNotEmpty(betRecordDo.getExtendInfo()) ? String.valueOf(betRecordDo.getExtendInfo().get(extend_field_campaignId)) : null)
                .betValue(AmountUtil.toBigDecimal(betRecordDo.getBetAmount()))
                .build();
    }

    public static String buildRefId(BetRecordDo betRecordDo) {
        return betRecordDo.getBetSerialNo() + betRecordDo.getGameId();
    }
}
