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
@ApiModel(description = "背包奖励明细")
public class BagRewardDetailVo implements Serializable {
    private static final long serialVersionUID = -2041608152456306530L;
    @ApiModelProperty(value = "奖励ID", required = true)
    private String rewardId;
    @ApiModelProperty(value = "奖励配置id", required = true)
    private String rewardConfigId;
    @ApiModelProperty(value = "奖励名称", required = true)
    private String name;
    @ApiModelProperty(value = "等级", required = true)
    private Integer starLevel;
    @ApiModelProperty(value = "奖励描述", required = true)
    private String description;
    @ApiModelProperty(value = "使用余额要求", required = true)
    private Long balanceRequired;
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
    @ApiModelProperty(value = "奖励投注金额", required = true)
    private Long betValue;
    @ApiModelProperty(value = "图标URL", required = true)
    private String imgUrl;
    @ApiModelProperty(value = "是否免费下注", required = true)
    private Boolean isFreeBet;
}
