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
public class BagOtherRewardDetailVo implements Serializable {
    private static final long serialVersionUID = -2041608152456306530L;
    @ApiModelProperty(value = "奖励ID", required = true)
    private String rewardId;
    @ApiModelProperty(value = "奖励名称", required = true)
    private String name;
    @ApiModelProperty(value = "奖励描述", required = true)
    private String description;
    @ApiModelProperty(value = "gameCode", required = true)
    private String gameCode;
    @ApiModelProperty(value = "系统游戏ID", required = true)
    private String gameId;
    @ApiModelProperty(value = "游戏名称", required = true)
    private String gameName;
    @ApiModelProperty(value = "图标URL", required = true)
    private String imgUrl;
    @ApiModelProperty(value = "奖励总数", required = true)
    private Integer totalCount;
}
