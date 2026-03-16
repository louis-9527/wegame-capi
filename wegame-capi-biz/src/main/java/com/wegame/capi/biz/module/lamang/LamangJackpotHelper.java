package com.wegame.capi.biz.module.lamang;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotDrawAndAwardDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;

import java.util.*;
import java.util.stream.Collectors;

public class LamangJackpotHelper {
    public static JackpotDrawAndAwardDo buildJackpotDrawAndAwardDo(BetRecordDo<LamangGameRecordDo> betRecord, SingleWalletBetResultDo singleWalletBetResultDo) {
        AssertUtil.assertNotNull(betRecord, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(singleWalletBetResultDo, ErrorCode.ERR_PARAM);
        return JackpotDrawAndAwardDo.builder()
                .betId(singleWalletBetResultDo.getBetId())
                .betAmount(betRecord.getBetAmount())
                .channelId(betRecord.getChannelId())
                .playerName(betRecord.getPlayerName())
                .betRecordId(betRecord.getId())
                .pId(betRecord.getPId())
                .playerId(betRecord.getPlayerId())
                .userId(betRecord.getUserId())
                .betPromotionType(betRecord.getBetPromotionType())
                .betType(BetTypeEnum.valueOfCode(betRecord.getBetType()))
                .gameCount(buildGameDrawTimes(betRecord.getGameRecordList()))
                .contribution(buildContribution(betRecord))
                .build();
    }

    public static Map<JackpotRewardPoolTypeEnum, Long> buildContribution(BetRecordDo<LamangGameRecordDo> betRecord) {
        Map<JackpotRewardPoolTypeEnum, Long> contributionMap = new HashMap<>();
        BetRecordDo.Jackpot jackpot = betRecord.getJackpot();
        if (Objects.nonNull(jackpot)) {
            contributionMap.put(JackpotRewardPoolTypeEnum.grand ,Objects.nonNull(jackpot.getGrandContributionAmount()) ? jackpot.getGrandContributionAmount() : jackpot.getGrandSupContributionAmount());
            contributionMap.put(JackpotRewardPoolTypeEnum.major ,Objects.nonNull(jackpot.getMajorContributionAmount()) ? jackpot.getMajorContributionAmount() : jackpot.getMajorSupContributionAmount());
            contributionMap.put(JackpotRewardPoolTypeEnum.minor ,Objects.nonNull(jackpot.getMinorContributionAmount()) ? jackpot.getMinorContributionAmount() : jackpot.getMinorSupContributionAmount());
            contributionMap.put(JackpotRewardPoolTypeEnum.mini ,Objects.nonNull(jackpot.getMiniContributionAmount()) ? jackpot.getMiniContributionAmount() : jackpot.getMiniSupContributionAmount());
        }

        return contributionMap;
    }

    public static Map<Long, Integer> buildGameDrawTimes(List<LamangGameRecordDo> recordList) {
        return Optional.ofNullable(recordList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(gameRecord -> Objects.nonNull(gameRecord.getJackpotDrawTimes()) && gameRecord.getJackpotDrawTimes() > 0)
                .collect(Collectors.toMap(
                        LamangGameRecordDo::getId,
                        LamangGameRecordDo::getJackpotDrawTimes,
                        Integer::sum));
    }
}
