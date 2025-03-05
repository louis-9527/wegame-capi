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
@ApiModel(description = "背包星卡列表VO")
public class RewardStarCardVo implements  Serializable {

    private static final long serialVersionUID = -1704392640858105682L;
    @ApiModelProperty(value = "星卡ID", required = true)
    private String id;
    @ApiModelProperty(value = "星卡名称", required = true)
    private String name;
    @ApiModelProperty(value = "星卡描述", required = true)
    private String description;
    @ApiModelProperty(value = "使用的游戏ID", required = true)
    private String gameId;
    @ApiModelProperty(value = "余额要求", required = true)
    private String balanceRequired;
    @ApiModelProperty(value = "生效时间", required = true)
    private Long effectTime;
    @ApiModelProperty(value = "奖励类型,1:每日任务 2:宝箱 4:成就", required = true)
    private String rewardType;
    @ApiModelProperty(value = "过期时间", required = true)
    private Long expiredTime;
    @ApiModelProperty(value = "投注基本金额", required = true)
    private Long specifiedBet;
    @ApiModelProperty(value = "信息", required = true)
    private Long msg;
}
