package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
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
@ApiModel(description = "成就")
public class AchievementVo implements  Serializable {
    private static final long serialVersionUID = -1704382640858325682L;
    private String id;
    private String name;
    private String description;
    private String iconUrl;
    private ProgressVo progressVoList;
    private Long completeAt;
    private Integer status;
    private String redeemId;
    private List<RewardVo> rewards;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressVo implements Serializable {
        private BigDecimal target;
        private BigDecimal current;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardVo implements Serializable {
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
