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
public class GetAchievementRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;
    private String pId;
    private String channelId;
    private String refId;
    private String playerName;
    private List<Achievement> achievementList;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
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
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Progress implements Serializable {
        private BigDecimal target;
        private BigDecimal current;
    }

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
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
