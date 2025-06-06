package com.wegame.capi.biz.module.lamang;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.domain.biz.game.LamangGameRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotDrawAndAwardDo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LamangJackpotHelper {
    public static JackpotDrawAndAwardDo buildJackpotDrawAndAwardDo(BetRecordDo<LamangGameRecordDo> betRecord, SingleWalletBetResultDo singleWalletBetResultDo) {
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
                .gameCount(buildGameDrawTimes(betRecord.getGameRecordList()))
                .build();
    }

    public static Map<Long, Integer> buildGameDrawTimes(List<LamangGameRecordDo> dos) {
        return dos.stream()
                .filter(gameRecord -> Objects.nonNull(gameRecord.getJackpotDrawTimes()) && gameRecord.getJackpotDrawTimes() > 0)
                .collect(Collectors.toMap(
                        LamangGameRecordDo::getId,
                        LamangGameRecordDo::getJackpotDrawTimes,
                        Integer::sum // 处理 key 冲突
                ));
    }
}
