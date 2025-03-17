package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "使用宝箱返回")
public class RedeemTreasureVo implements Serializable {
    private static final long serialVersionUID = 2423962589779617794L;
    @ApiModelProperty(value = "宝箱ID", required = true)
    private String treasureId;
    @ApiModelProperty(value = "宝箱奖励", required = true)
    private TreasureRewardVo reward;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "宝箱奖励")
    public static class TreasureRewardVo implements Serializable {
        private static final long serialVersionUID = 7692352397871708565L;
        @ApiModelProperty(value = "奖励ID", required = true)
        private String rewardConfigId;
        @ApiModelProperty(value = "奖励名称", required = true)
        private String name;
        @ApiModelProperty(value = "", required = true)
        private Integer count;
        @ApiModelProperty(value = "", required = true)
        private String imgUrl;
        @ApiModelProperty(value = "", required = true)
        private BigDecimal betValue;
        @ApiModelProperty(value = "", required = true)
        private BigDecimal balanceRequired;
        @ApiModelProperty(value = "", required = true)
        private Integer starLevel;
    }
}
