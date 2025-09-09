package com.wegame.capi.biz.domain.biz.contract;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RedeemPlayerContractDo implements Serializable {
    private static final long serialVersionUID = -63098767854L;
    /**
     * isClaimed
     */
    private Boolean isClaimed;
    /**
     * Reward type: 1: starCard / 2: freeSpin
     */
    private String rewardType;
    /**
     * Reward info
     */
    private ContractRewardInfo rewardInfo;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ContractRewardInfo implements Serializable {
        private static final long serialVersionUID = -656782432534L;
        /**
         * gameId
         */
        private String gameId;
        /**
         * gameCode
         */
        private String gameCode;
        /**
         * gameName
         */
        private String gameName;
        /**
         * rewardId
         */
        private String rewardId;
        /**
         * imgURL
         */
        private String imgURL;
        /**
         * StarCard name / FreeSpin name
         */
        private String rewardName;
        /**
         * rewardConfigId
         */
        private String rewardConfigId;
        /**
         * starLevel
         */
        private Integer starLevel;
        /**
         * betValue
         */
        private BigDecimal betValue;
        /**
         * balanceRequired
         */
        private BigDecimal balanceRequired;
        /**
         * campaignId
         */
        private String campaignId;
        /**
         * freeBet
         */
        private Integer freeBet;
        /**
         * count
         */
        private Integer count;
    }

}
