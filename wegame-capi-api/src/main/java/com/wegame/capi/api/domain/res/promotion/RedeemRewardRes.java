package com.wegame.capi.api.domain.res.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RedeemRewardRes implements Serializable {
    private static final long serialVersionUID = -2877868732659795305L;
    /**
     * 玩家
     */
    private String playerName;
    /**
     * refId
     */
    private String refId;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * 平台id
     */
    private String pid;
    /**
     * 明细
     */
    private RewardDetail detail;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RewardDetail implements Serializable{

        private static final long serialVersionUID = 3399519039239876329L;
        /**
         * 奖励名称
         */
        private String name;
        /**
         * 星卡等级
         */
        private Integer starLevel;
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
         * date
         */
        private Date date;
    }
}
