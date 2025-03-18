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
public class GetBagOtherRewardRes {
    private String playerName;
    private String refId;
    private String channelId;
    private String pid;
    private List<GetBagOtherRewardRecord> rewardBag;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
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
         * 过期日期
         */
        private Integer totalCount;

    }
}
