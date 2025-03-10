package com.wegame.capi.api.domain.res.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemAchievementRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;
    private String pId;
    private String channelId;
    private String refId;
    private String playerName;
    private List<RedeemAchievementReward> reward;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedeemAchievementReward implements Serializable {
        private static final long serialVersionUID = 1L;
        private String rewardConfigId;
        private String name;
        private Integer starLevel;
        private String imgUrl;
        private BigDecimal betValue;
        private BigDecimal balanceRequired;
        private Integer count;
    }
}
