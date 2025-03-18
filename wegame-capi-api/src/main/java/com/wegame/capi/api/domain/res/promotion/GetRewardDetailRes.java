package com.wegame.capi.api.domain.res.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetRewardDetailRes implements Serializable {
    private static final long serialVersionUID = -2877868732659795305L;

    private String refId;

    private String rewardConfigId;

    private RewardDetail details;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardDetail implements Serializable{

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
         * Reward valid hour
         */
        private Integer PeriodHour;
    }
}
