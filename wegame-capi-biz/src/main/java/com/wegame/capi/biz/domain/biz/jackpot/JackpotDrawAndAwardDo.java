package com.wegame.capi.biz.domain.biz.jackpot;

import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
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
    private Long userId;
    private Long betRecordId;
    private String betId;
    private Long betAmount;
    private Map<Long, Integer> gameCount;

    /**
     * 营销类型
     */
    private Integer betPromotionType;
    /**
     * 投注类型
     */
    private BetTypeEnum betType;

    /**
     * 奖池贡献
     */

    private Map<JackpotRewardPoolTypeEnum, Long> contribution;
}
