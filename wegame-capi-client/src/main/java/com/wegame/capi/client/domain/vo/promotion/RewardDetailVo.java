package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "奖励明细")
public class RewardDetailVo {

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
    private Integer PeriodHour;
}
