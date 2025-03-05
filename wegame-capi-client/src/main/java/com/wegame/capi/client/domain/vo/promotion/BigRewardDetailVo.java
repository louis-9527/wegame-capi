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
@ApiModel(description = "奖励明细")
public class BigRewardDetailVo implements Serializable {
    private static final long serialVersionUID = -2041608152456306530L;
    @ApiModelProperty(value = "奖励ID", required = true)
    private String rewardId;
    @ApiModelProperty(value = "奖励名称", required = true)
    private String name;
    @ApiModelProperty(value = "奖励描述", required = true)
    private String description;
    @ApiModelProperty(value = "余额要求", required = true)
    private String balanceRequired;
    @ApiModelProperty(value = "跳转时游戏ID", required = true)
    private String skipGameId;
    @ApiModelProperty(value = "系统游戏ID", required = true)
    private String gameId;
    @ApiModelProperty(value = "游戏名称", required = true)
    private String gameName;
    @ApiModelProperty(value = "生效时间", required = true)
    private Long effectTime;
    @ApiModelProperty(value = "过期时间", required = true)
    private Long expiredTime;
    @ApiModelProperty(value = "奖励类型,1:每日任务 2:宝箱 4:成就", required = true)
    private String rewardType;
    @ApiModelProperty(value = "投注基本金额", required = true)
    private Long betValue;
    @ApiModelProperty(value = "图标URL", required = true)
    private String imgUrl;
}
