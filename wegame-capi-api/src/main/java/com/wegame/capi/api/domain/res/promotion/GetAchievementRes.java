package com.wegame.capi.api.domain.res.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GetAchievementRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;
    /**
     * 平台id
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
     * 成就列表
     */
    private List<Achievement> achievementList;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Achievement implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 成就id
         */
        private String id;
        /**
         * 成就名称
         */
        private String name;
        /**
         * 成就描述
         */
        private String description;
        /**
         * 成就图标
         */
        private String iconUrl;
        /**
         * 进度
         */
        private Progress progressList;
        /**
         * 完成时间
         */
        private Date completeAt;
        /**
         * 状态 1进行中 2可兑换 3已兑换
         */
        private Integer status;
        /**
         * 兑换id
         */
        private String redeemId;
        /**
         * 奖励列表
         */
        private List<Reward> rewards;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Progress implements Serializable {
        /**
         * 目标进度
         */
        private BigDecimal target;
        /**
         * 当前进度
         */
        private BigDecimal current;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reward implements Serializable {
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
         * 奖励类型
         */
        private Integer type;
        /**
         * 星级
         */
        private String starLevel;
        /**
         * 数量
         */
        private Integer count;
        /**
         * 奖励图标
         */
        private String imgUrl;
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
