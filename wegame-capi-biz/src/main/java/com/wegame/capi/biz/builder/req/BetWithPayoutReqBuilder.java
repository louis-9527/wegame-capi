package com.wegame.capi.biz.builder.req;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.freespin.FreeSpinReq;
import com.wegame.capi.api.domain.req.singlewallet.BetStarSpinReq;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.req.singlewallet.JackpotReq;
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
                        .isFreeRound(BetSubTypeEnum.isFreeWheel(betRecordDo.getBetSubType()))
                        .jackpotDetails(new BetWithPayoutReq.BetDetails.JackpotDetails())
                        .build())
                .build();
    }

    public static BetWithPayoutReq buildReq(BetRecordDo betRecordDo, List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BetWithPayoutReq.BetDetails.JackpotDetails details = BetWithPayoutReq.BetDetails.JackpotDetails
                .builder()
                .jackpotContributionDetails(buildJackpotPoolCommission(betRecordDo))
                .jackpotPool(buildJackpotPool(jackpotRewardPoolDos))
                .build();
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
                        .jackpotDetails(details)
                        .build())
                .build();
    }

    private static BetWithPayoutReq.BetDetails.JackpotContributionDetails buildJackpotPoolCommission(BetRecordDo betRecordDo) {
        BetRecordDo.Jackpot jackpot = betRecordDo.getJackpot();
        if (Objects.nonNull(jackpot)) {
            return BetWithPayoutReq.BetDetails.JackpotContributionDetails.builder()
                            .grandContributionAmount(AmountUtil.toBigDecimal(jackpot.getGrandContributionAmount()))
                    .majorContributionAmount(AmountUtil.toBigDecimal(jackpot.getMajorContributionAmount()))
                    .minorContributionAmount(AmountUtil.toBigDecimal(jackpot.getMinorContributionAmount()))
                    .miniContributionAmount(AmountUtil.toBigDecimal(jackpot.getMiniContributionAmount()))
                    .build();
        }
        return null;
    }

    private static BetWithPayoutReq.BetDetails.JackpotPool buildJackpotPool(List<JackpotRewardPoolDo> jackpotRewardPoolDos) {
        BigDecimal totalJackpotAmount = AmountUtil.toBigDecimal(jackpotRewardPoolDos.stream().mapToLong(JackpotRewardPoolDo::getAmount).sum());
        Map<JackpotRewardPoolTypeEnum, JackpotRewardPoolDo> jackpotRewardPoolDoMap = jackpotRewardPoolDos.stream()
                .collect(Collectors.toMap(
                        JackpotRewardPoolDo::getType,
                        pool -> pool,
                        (existing, replacement) -> existing // 处理 key 冲突
                ));
        // 组装奖池信息
        JackpotRewardPoolDo grandPool = jackpotRewardPoolDoMap.get(JackpotRewardPoolTypeEnum.grand);
        BetWithPayoutReq.BetDetails.GrandPool grandBuilder = BetWithPayoutReq.BetDetails.GrandPool.builder()
                .amount(AmountUtil.toBigDecimal(grandPool.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(grandPool.getInitAmount())).build();

        JackpotRewardPoolDo majorPool = jackpotRewardPoolDoMap.get(JackpotRewardPoolTypeEnum.major);
        BetWithPayoutReq.BetDetails.MajorPool majorBuilder = BetWithPayoutReq.BetDetails.MajorPool.builder()
                .amount(AmountUtil.toBigDecimal(majorPool.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(majorPool.getInitAmount())).build();

        JackpotRewardPoolDo minorPool = jackpotRewardPoolDoMap.get(JackpotRewardPoolTypeEnum.minor);
        BetWithPayoutReq.BetDetails.MinorPool minorBuilder = BetWithPayoutReq.BetDetails.MinorPool.builder()
                .amount(AmountUtil.toBigDecimal(minorPool.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(minorPool.getInitAmount())).build();

        JackpotRewardPoolDo miniPool = jackpotRewardPoolDoMap.get(JackpotRewardPoolTypeEnum.mini);
        BetWithPayoutReq.BetDetails.MiniPool miniBuilder = BetWithPayoutReq.BetDetails.MiniPool.builder()
                .amount(AmountUtil.toBigDecimal(miniPool.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(miniPool.getInitAmount())).build();

        BetWithPayoutReq.BetDetails.BetBonusPool betBonusBuilder = BetWithPayoutReq.BetDetails.BetBonusPool.builder()
                .amount(BigDecimal.ZERO)
                .initAmount(BigDecimal.ZERO).build();

        BigDecimal jackpotAmount = grandBuilder.getAmount().add(majorBuilder.getAmount()).add(minorBuilder.getAmount()).add(miniBuilder.getAmount());
        return BetWithPayoutReq.BetDetails.JackpotPool.builder()
                .totalJackpotAmount(jackpotAmount)
                .grandPool(grandBuilder)
                .majorPool(majorBuilder)
                .minorPool(minorBuilder)
                .miniPool(miniBuilder)
                .betBonusPool(betBonusBuilder).build();
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
