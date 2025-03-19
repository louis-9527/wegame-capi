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
public class RedeemAchievementRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;
    /**
     * 渠道id
     */
    private String pId;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * refId
     */
    private String refId;
    /**
     * 玩家姓名
     */
    private String playerName;
    /**
     * 奖励列表
     */
    private RedeemAchievementReward reward;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class RedeemAchievementReward implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 奖励配置id
         */
        private String rewardConfigId;
        /**
         * 奖励名称
         */
        private String name;
        /**
         * 星级
         */
        private Integer starLevel;
        /**
         * 奖励图片
         */
        private String imgUrl;
        /**
         * 数量
         */
        private Integer count;
        /**
         * 奖励投注金额
         */
        private BigDecimal betValue;
        /**
         * 使用余额要求
         */
        private BigDecimal balanceRequired;
    }
}
