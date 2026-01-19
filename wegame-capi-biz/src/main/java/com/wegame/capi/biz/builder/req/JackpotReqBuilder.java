package com.wegame.capi.biz.builder.req;

import com.google.common.collect.Lists;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.singlewallet.JackpotReq;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotAwardDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.common.utils.AmountUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class JackpotReqBuilder {

    public static JackpotReq buildReq(JackpotAwardDo jackpotAwardDo, Map<JackpotRewardPoolTypeEnum, JackpotRewardPoolDo> jackpotRewardPoolMap) {
        // 组装奖池信息
        JackpotRewardPoolDo grandPool = jackpotRewardPoolMap.get(JackpotRewardPoolTypeEnum.grand);
        JackpotReq.JackpotPool.SupplementaryPool grandSupplementaryPool = JackpotReq.JackpotPool.SupplementaryPool.builder()
                .initAmount(AmountUtil.toBigDecimal(grandPool.getReservedPoolInitAmount()))
                .amount(AmountUtil.toBigDecimal(grandPool.getReservedAmount()))
                .build();
        JackpotReq.JackpotPool.GrandPool grandBuilder = JackpotReq.JackpotPool.GrandPool.builder()
                .amount(AmountUtil.toBigDecimal(grandPool.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(grandPool.getInitAmount()))
                .supplementaryPool(grandSupplementaryPool)
                .build();

        JackpotRewardPoolDo majorPool = jackpotRewardPoolMap.get(JackpotRewardPoolTypeEnum.major);
        JackpotReq.JackpotPool.SupplementaryPool majorSupplementaryPool = JackpotReq.JackpotPool.SupplementaryPool.builder()
                .initAmount(AmountUtil.toBigDecimal(majorPool.getReservedPoolInitAmount()))
                .amount(AmountUtil.toBigDecimal(majorPool.getReservedAmount()))
                .build();
        JackpotReq.JackpotPool.MajorPool majorBuilder = JackpotReq.JackpotPool.MajorPool.builder()
                .amount(AmountUtil.toBigDecimal(majorPool.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(majorPool.getInitAmount()))
                .supplementaryPool(majorSupplementaryPool)
                .build();

        JackpotRewardPoolDo minorPool = jackpotRewardPoolMap.get(JackpotRewardPoolTypeEnum.minor);
        JackpotReq.JackpotPool.SupplementaryPool minorSupplementaryPool = JackpotReq.JackpotPool.SupplementaryPool.builder()
                .initAmount(AmountUtil.toBigDecimal(minorPool.getReservedPoolInitAmount()))
                .amount(AmountUtil.toBigDecimal(minorPool.getReservedAmount()))
                .build();
        JackpotReq.JackpotPool.MinorPool minorBuilder = JackpotReq.JackpotPool.MinorPool.builder()
                .amount(AmountUtil.toBigDecimal(minorPool.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(minorPool.getInitAmount()))
                .supplementaryPool(minorSupplementaryPool)
                .build();

        JackpotRewardPoolDo miniPool = jackpotRewardPoolMap.get(JackpotRewardPoolTypeEnum.mini);
        JackpotReq.JackpotPool.SupplementaryPool miniSupplementaryPool = JackpotReq.JackpotPool.SupplementaryPool.builder()
                .initAmount(AmountUtil.toBigDecimal(miniPool.getReservedPoolInitAmount()))
                .amount(AmountUtil.toBigDecimal(miniPool.getReservedAmount()))
                .build();
        JackpotReq.JackpotPool.MiniPool miniBuilder = JackpotReq.JackpotPool.MiniPool.builder()
                .amount(AmountUtil.toBigDecimal(miniPool.getAmount()))
                .initAmount(AmountUtil.toBigDecimal(miniPool.getInitAmount()))
                .supplementaryPool(miniSupplementaryPool)
                .build();

        JackpotReq.JackpotPool.BetBonusPool betBonusBuilder = JackpotReq.JackpotPool.BetBonusPool.builder()
                .amount(BigDecimal.ZERO)
                .initAmount(BigDecimal.ZERO).build();

        BigDecimal totalSupJackpotAmount = AmountUtil.toBigDecimal(jackpotRewardPoolMap.values().stream().mapToLong(JackpotRewardPoolDo::getReservedAmount).sum());
        JackpotReq.JackpotPool jackpotPool = JackpotReq.JackpotPool.builder()
                .totalJackpotAmount(grandBuilder.getAmount().add(majorBuilder.getAmount()).add(minorBuilder.getAmount()).add(miniBuilder.getAmount()))
                .totalSupJackpotAmount(totalSupJackpotAmount)
                .grandPool(grandBuilder)
                .majorPool(majorBuilder)
                .minorPool(minorBuilder)
                .miniPool(miniBuilder)
                .betBonusPool(betBonusBuilder).build();

        // 组装 jackpotDetails信息
        JackpotUserRewardRecordDo jackpotUserRewardRecordDo = jackpotAwardDo.getJackpotUserRewardRecordDo();
        JackpotReq.JackpotWinDetails jackpotWinDetails = JackpotReq.JackpotWinDetails.builder()
                .jackpotType(jackpotUserRewardRecordDo.getRewardPoolType().getCode())
                .winAmount(AmountUtil.toBigDecimal(jackpotUserRewardRecordDo.getRewardAmount()))
                .build();
        JackpotReq.JackpotDetails jackpotDetails = JackpotReq.JackpotDetails.builder()
                .jackpotTypes(Lists.newArrayList(jackpotUserRewardRecordDo.getRewardPoolType().getCode()))
                .jackpotPool(jackpotPool)
                .jackpotWinDetails(Lists.newArrayList(jackpotWinDetails)).build();

        return JackpotReq.builder()
                .pId(jackpotAwardDo.getPId())
                .channelId(jackpotAwardDo.getChannelId())
                .playerName(jackpotAwardDo.getPlayerName())
                .refId(jackpotAwardDo.getJackpotUserRewardRecordDo().getRewardGrantSerialNo())
                .betId(jackpotAwardDo.getJackpotUserRewardRecordDo().getBetId())
                .roundNo(jackpotAwardDo.getJackpotUserRewardRecordDo().getRewardGrantSerialNo())
                .createTime(DateUtil.toUTC(new Date(jackpotAwardDo.getJackpotUserRewardRecordDo().getCreateTime())))
                .jackpotAmount(AmountUtil.toBigDecimal(jackpotUserRewardRecordDo.getRewardAmount()))
                .jackpotDetails(jackpotDetails)
                .build();
    }
}
