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
public class RedeemTreasureRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;
    /**
     * 宝箱id
     */
    private String treasureId;
    /**
     * 玩家姓名
     */
    private String playerName;
    /**
     * 渠道id
     */
    private String channelId;
    /**
     * 玩家id
     */
    private String playerId;
    /**
     * 平台id
     */
    private String pId;
    /**
     * 等级
     */
    private String level;
    /**
     * refId
     */
    private String refId;
    /**
     * 经验值
     */
    private String experience;
    /**
     * 奖励
     */
    private Reward reward;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reward implements Serializable {
        private static final long serialVersionUID = -63633463436518L;
        /**
         * 奖励配置id
         */
        private String rewardConfigId;
        /**
         * 奖励名称
         */
        private String name;
        /**
         * count
         */
        private Integer count;
        /**
         * imgUrl
         */
        private String imgUrl;
        /**
         * betValue
         */
        private BigDecimal betValue;
        /**
         * balanceRequired
         */
        private BigDecimal balanceRequired;
        /**
         * starLevel
         */
        private Integer starLevel;
    }
}
