package com.wegame.capi.client.domain.vo.contract;

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
public class RedeemPlayerContractVo implements Serializable {
    private static final long serialVersionUID = -656567423784L;
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
