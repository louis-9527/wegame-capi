package com.wegame.capi.api.domain.res.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GetTreasureRes implements Serializable {
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
     * 宝箱列表
     */
    private List<Treasure> treasure;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Treasure implements Serializable {
        private static final long serialVersionUID = 1L;
        /**
         * 宝箱id
         */
        private String treasureId;
        /**
         * 宝箱名称
         */
        private String name;
        /**
         * 渠道id
         */
        private String channelId;
        /**
         * 宝箱等级
         */
        private Integer level;
        /**
         * 当前经验
         */
        private BigDecimal experience;
        /**
         * 目标进度
         */
        private BigDecimal target;
        /**
         * 状态 1进行种 2可兑换 3已兑换
         */
        private Integer status;
        /**
         * 奖励列表
         */
        private List<Reward> rewards;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
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
         * 奖励数量
         */
        private Integer count;
        /**
         * 奖励图片
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
        /**
         * 星级
         */
        private Integer starLevel;
    }
}
