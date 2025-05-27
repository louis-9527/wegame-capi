package com.wegame.capi.biz.domain.biz.jackpot;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class JackpotDrawAndAwardDo {
    private String pId;
    private String channelId;
    private String playerId;
    private String playerName;
    private String userId;
    private Long betRecordId;
    private String betId;
    private Long betAmount;
    private Map<Long,Integer> gameCount;
}
