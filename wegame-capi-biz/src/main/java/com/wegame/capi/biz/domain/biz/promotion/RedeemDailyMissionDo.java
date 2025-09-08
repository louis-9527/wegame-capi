package com.wegame.capi.biz.domain.biz.promotion;

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
public class RedeemDailyMissionDo implements Serializable {
    private static final long serialVersionUID = 8718553488187817145L;
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
     * 平台id
     */
    private String pId;
    /**
     * 等级
     */
    private Integer level;
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
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
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
        /**
         * 奖励id
         */
        private String rewardId;
        /**
         * 游戏id
         */
        private String gameId;
        /**
         * 游戏code
         */
        private String gameCode;
        /**
         * 游戏名称
         */
        private String gameName;
    }
}
