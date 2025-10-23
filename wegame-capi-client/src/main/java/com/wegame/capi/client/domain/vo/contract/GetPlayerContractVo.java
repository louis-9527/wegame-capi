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
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "获取玩家契约模型")
public class GetPlayerContractVo implements Serializable {
    private static final long serialVersionUID = -632887243L;
    @ApiModelProperty(value = "pId", required = true)
    private String pId;
    @ApiModelProperty(value = "channelId", required = true)
    private String channelId;
    @ApiModelProperty(value = "refId", required = true)
    private String refId;
    @ApiModelProperty(value = "playerName", required = true)
    private String playerName;
    @ApiModelProperty(value = "createTime", required = true)
    private String createTime;
    @ApiModelProperty(value = "endTime", required = true)
    private String endTime;
    @ApiModelProperty(value = "rewardId", required = true)
    private String rewardId;
    @ApiModelProperty(value = "experience", required = true)
    private BigDecimal experience;
    @ApiModelProperty(value = "target", required = true)
    private BigDecimal target;
    @ApiModelProperty(value = "status", required = true)
    private String status;
    @ApiModelProperty(value = "imgURL", required = true)
    private String imgURL;
    @ApiModelProperty(value = "imgURL", required = true)
    private String starCardImgUrl;
    @ApiModelProperty(value = "imgURL", required = true)
    private String freeSpinImgUrl;
    @ApiModelProperty(value = "Reward type: 1: starCard / 2: freeSpin", required = true)
    private Integer rewardType;
    @ApiModelProperty(value = "isEnabled", required = true)
    private Boolean isEnabled;
}
