package com.wegame.capi.client.domain.vo.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "获取玩家契约模型")
public class RedeemPlayerContractVo implements Serializable {
    private static final long serialVersionUID = -656567423784L;
    @ApiModelProperty(value = "isClaimed", required = true)
    private Boolean isClaimed;
    @ApiModelProperty(value = "rewardType", required = true)
    private String rewardType;
    @ApiModelProperty(value = "rewardInfo", required = true)
    private ContractRewardInfo rewardInfo;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ContractRewardInfo implements Serializable {
        private static final long serialVersionUID = -656782432534L;
        @ApiModelProperty(value = "gameId", required = true)
        private String gameId;
        @ApiModelProperty(value = "gameCode", required = true)
        private String gameCode;
        @ApiModelProperty(value = "gameName", required = true)
        private String gameName;
        @ApiModelProperty(value = "rewardId", required = true)
        private String rewardId;
        @ApiModelProperty(value = "imgURL", required = true)
        private String imgURL;
        @ApiModelProperty(value = "rewardName", required = true)
        private String rewardName;
        @ApiModelProperty(value = "rewardConfigId", required = true)
        private String rewardConfigId;
        @ApiModelProperty(value = "starLevel", required = true)
        private Integer starLevel;
        @ApiModelProperty(value = "betValue", required = true)
        private BigDecimal betValue;
        @ApiModelProperty(value = "balanceRequired", required = true)
        private BigDecimal balanceRequired;
        @ApiModelProperty(value = "campaignId", required = true)
        private String campaignId;
        @ApiModelProperty(value = "freeBet", required = true)
        private BigDecimal freeBet;
        @ApiModelProperty(value = "count", required = true)
        private Integer count;
    }

}
