package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "奖励明细")
public class RewardDetailVo implements Serializable {
    private static final long serialVersionUID = 6500143231638833380L;
    @ApiModelProperty(value = "请求refId", required = true)
    private String refId;
    @ApiModelProperty(value = "rewardConfigId", required = true)
    private String rewardConfigId;
    @ApiModelProperty(value = "详情", required = true)
    private Detail details;

    @Data
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Detail {
        @ApiModelProperty(value = "奖励名称", required = true)
        private String name;
        @ApiModelProperty(value = "奖励描述", required = true)
        private String description;
        @ApiModelProperty(value = "使用余额要求", required = true)
        private Long balanceRequired;
        @ApiModelProperty(value = "奖励URL", required = true)
        private String imgUrl;
        @ApiModelProperty(value = "跳转时游戏ID", required = true)
        private String skipGameId;
        @ApiModelProperty(value = "系统游戏ID", required = true)
        private String gameId;
        @ApiModelProperty(value = "游戏名称", required = true)
        private String gameName;
        @ApiModelProperty(value = "奖励类型(来源)", required = true)
        private String rewardType;
        @ApiModelProperty(value = "奖励投注金额", required = true)
        private Long betValue;
        @ApiModelProperty(value = "星卡等级", required = true)
        private Integer starLevel;
        @ApiModelProperty(value = "Reward valid day", required = true)
        private Integer periodDay;
        @ApiModelProperty(value = "Reward valid hour", required = true)
        private Integer periodHour;
        @ApiModelProperty(value = "is Free Bet", required = true)
        private Boolean isFreeBet;
    }
}
