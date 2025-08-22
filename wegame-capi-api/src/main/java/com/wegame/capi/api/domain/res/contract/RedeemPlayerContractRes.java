package com.wegame.capi.api.domain.res.contract;

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
public class RedeemPlayerContractRes implements Serializable {
    private static final long serialVersionUID = -63632342354L;
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
        private static final long serialVersionUID = -63789483534L;
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
