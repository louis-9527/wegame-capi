package com.wegame.capi.biz.domain.biz.jackpot;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JackpotAwardDo {
    private String pId;
    private String channelId;
    private String playerId;
    private String playerName;
    private Long userId;
    private JackpotUserRewardRecordDo jackpotUserRewardRecordDo;

    public static JackpotAwardDo build(JackpotDrawAndAwardDo drawAndAwardDo,  JackpotUserRewardRecordDo buildRecord){
       return JackpotAwardDo.builder()
                .pId(drawAndAwardDo.getPId())
                .channelId(drawAndAwardDo.getChannelId())
                .playerId(drawAndAwardDo.getPlayerId())
                .playerName(drawAndAwardDo.getPlayerName())
                .userId(drawAndAwardDo.getUserId())
                .jackpotUserRewardRecordDo(buildRecord).build();
    }

}
