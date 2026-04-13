package com.wegame.capi.api.domain.res.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GetBagCurrentRewardRes implements Serializable {
    private static final long serialVersionUID = 1165155893675353428L;
    /**
     * 玩家
     */
    private String playerName;
    /**
     * refid
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
     * rewardBag
     */
    private List<GetBagCurrentRewardRecord> rewardBag;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class GetBagCurrentRewardRecord implements Serializable {
        private static final long serialVersionUID = 1906312186996092565L;
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
         * 奖励ID
         */
        private String rewardId;
        /**
         * 奖励配置id
         */
        private String rewardConfigId;
        /**
         * 等级
         */
        private Integer starLevel;
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
        private Date rewardDate;
        /**
         * 过期日期
         */
        private Date expiryDate;
        /**
         * is Free Bet
         */
        private Boolean isFreeBet;
    }
}
