package com.wegame.capi.api.domain.res.promotion;

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
public class GetRewardDetailRes implements Serializable {
    private static final long serialVersionUID = -2877868732659795305L;

    private String refId;
    /**
     * 奖励配置id
     */
    private String rewardConfigId;
    /**
     * 明细
     */
    private RewardDetail details;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RewardDetail implements Serializable {

        private static final long serialVersionUID = 3399519039239876329L;
        /**
         * 奖励名称
         */
        private String name;
        /**
         * 奖励描述
         */
        private String description;
        /**
         * 使用余额要求
         */
        private BigDecimal balanceRequired;
        /**
         * 奖励URL
         */
        private String imgUrl;
        /**
         * 游戏ID,需要传给前端,做游戏跳转
         */
        private String gameId;
        /**
         * 游戏内部ID
         */
        private String gameCode;
        /**
         * 游戏名称
         */
        private String gameName;
        /**
         * 奖励类型(来源)
         */
        private String rewardType;
        /**
         * 奖励投注金额
         */
        private BigDecimal betValue;
        /**
         * 开始日期
         */
        private Integer starLevel;
        /**
         * Reward valid day
         */
        private Integer periodDay;
        /**
         * Reward valid hour todo @TUTU
         */
        private Integer PeriodHour;
        /**
         * is Free Bet
         */
        private Boolean isFreeBet;
    }
}
