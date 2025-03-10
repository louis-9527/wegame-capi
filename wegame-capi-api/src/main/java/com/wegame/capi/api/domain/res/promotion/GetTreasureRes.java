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
public class GetTreasureRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;
    private String pId;
    private String channelId;
    private String refId;
    private String playerName;
    private List<Treasure> treasure;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Treasure implements Serializable {
        private static final long serialVersionUID = 1L;
        private String treasureId;
        private String name;
        private String channelId;
        private Integer level;
        private BigDecimal experience;
        private BigDecimal target;
        /**
         * status 1 - in progress , 2 - redeemable , 3 - redeemed
         */
        private Integer status;
        private List<Reward> rewards;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reward implements Serializable {
        private String rewardConfigId;
        private String name;
        private Integer count;
        private String imgUrl;
        private BigDecimal betValue;
        private BigDecimal balanceRequired;
        private Integer starLevel;
    }
}
