package com.wegame.capi.api.domain.res.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemAchievementRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;
    /**
     * pId
     */
    private String pId;
    /**
     * channelId
     */
    private String channelId;
    /**
     * refId
     */
    private String refId;
    /**
     * playerName
     */
    private String playerName;
    /**
     * 奖励列表
     */
    private List<RedeemAchievementReward> reward;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedeemAchievementReward implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * rewardId
         */
        private String rewardId;
        /**
         * name
         */
        private String name;
        /**
         * rewardCount
         */
        private Integer rewardCount;
    }
}
