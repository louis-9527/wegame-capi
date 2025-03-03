package com.wegame.capi.api.domain.res.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetAchievementRes implements Serializable {
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
    private List<GetAchievement> achievementList;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAchievement implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * id
         */
        private String id;
        /**
         * Redeem Achievement Id
         */
        private String redeemId;
        /**
         * Achievement name
         */
        private String name;
        /**
         * status:1 - in progress , 2 - redeemable , 3 - redeemed
         */
        private Integer status;
        /**
         * Achievement description
         */
        private String description;
        /**
         * rewardId
         */
        private Integer rewardId;
        /**
         * rewardName
         */
        private String rewardName;
        /**
         * Achievement progress
         */
        private Integer progress;
        /**
         * Complete Achievement DateTime
         */
        private Date completeAt;
    }
}
