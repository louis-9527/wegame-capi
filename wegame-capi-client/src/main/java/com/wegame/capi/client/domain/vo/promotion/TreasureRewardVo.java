package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "宝箱奖励")
public class TreasureRewardVo implements Serializable {
    private static final long serialVersionUID = 7692352397871708565L;
    @ApiModelProperty(value = "奖励ID", required = true)
    private String rewardId;
    @ApiModelProperty(value = "奖励名称", required = true)
    private String rewardName;
    @ApiModelProperty(value = "奖励次数", required = true)
    private Integer rewardCount;
    @ApiModelProperty(value = "奖励等级", required = true)
    private Integer starLevel;
}
