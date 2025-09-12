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
public class RedeemDailyMissionRes implements Serializable {
    private static final long serialVersionUID = -8678185038662751436L;
    /**
     * 任务id
     */
    private String dailyMissionId;
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
     * refId
     */
    private String refId;
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
        private static final long serialVersionUID = -3174174700578158659L;
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
