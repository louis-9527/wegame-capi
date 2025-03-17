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
public class RedeemTreasureRes implements Serializable {
    private static final long serialVersionUID = -63633463436518L;
    private String treasureId;
    private String playerName;
    private String channelId;
    private String pId;
    private String level;
    private String refId;
    private String experience;
    private Reward reward;

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
