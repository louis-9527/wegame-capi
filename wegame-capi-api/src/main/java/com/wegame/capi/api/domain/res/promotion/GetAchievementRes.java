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
    private String pId;
    private String channelId;
    private String refId;
    private String playerName;
    private List<Achievement> achievementList;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Achievement implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id;
        private String name;
        private String description;
        private String iconUrl;
        private Progress progressList;
        private Date completeAt;
        private Integer status;
        private String redeemId;
        private List<Reward> rewards;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Progress implements Serializable {
        private BigDecimal target;
        private BigDecimal current;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reward implements Serializable {
        private static final long serialVersionUID = 1L;
        private String rewardConfigId;
        private String name;
        private String starLevel;
        private Integer count;
        private String imgUrl;
        private BigDecimal betValue;
        private BigDecimal balanceRequired;
    }
}
