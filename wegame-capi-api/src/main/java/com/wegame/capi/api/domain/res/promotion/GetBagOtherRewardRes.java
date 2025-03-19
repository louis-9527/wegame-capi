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
@EqualsAndHashCode
public class GetBagOtherRewardRes implements Serializable {
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
     * rewardBag
     */
    private List<GetBagOtherRewardRecord> rewardBag;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class GetBagOtherRewardRecord implements Serializable {
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
         * 奖励投注金额
         */
        private BigDecimal betValue;
        /**
         * 开始日期
         */
        private Date rewardDate;
        /**
         * totalCount
         */
        private Integer totalCount;
    }
}
