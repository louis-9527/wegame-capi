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
@ApiModel(description = "成就")
public class RedeemAchievementVo implements  Serializable {
    private static final long serialVersionUID = -1704382640858325682L;
    @ApiModelProperty(value = "", required = true)
    private String playerName;
    @ApiModelProperty(value = "", required = true)
    private String channelId;
    @ApiModelProperty(value = "", required = true)
    private String pId;
    @ApiModelProperty(value = "", required = true)
    private String refId;
    @ApiModelProperty(value = "", required = true)
    private List<RedeemAchievementRewardVo> reward;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(description = "宝箱奖励")
    public static class RedeemAchievementRewardVo implements Serializable {
        private static final long serialVersionUID = 7692352397871708565L;
        @ApiModelProperty(value = "奖励配置id", required = true)
        private String rewardConfigId;
        @ApiModelProperty(value = "奖励名称", required = true)
        private String name;
        @ApiModelProperty(value = "", required = true)
        private Integer count;
        @ApiModelProperty(value = "", required = true)
        private Integer starLevel;
        @ApiModelProperty(value = "", required = true)
        private String imgUrl;
        @ApiModelProperty(value = "奖励投注金额", required = true)
        private BigDecimal betValue;
        @ApiModelProperty(value = "使用余额要求", required = true)
        private BigDecimal balanceRequired;
    }
}
