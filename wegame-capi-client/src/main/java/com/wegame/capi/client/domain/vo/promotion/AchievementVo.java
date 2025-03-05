package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "成就")
public class AchievementVo implements  Serializable {
    private static final long serialVersionUID = -1704382640858325682L;
    @ApiModelProperty(value = "成就名称", required = true)
    private String name;
    @ApiModelProperty(value = "任务描述", required = true)
    private String description;
    @ApiModelProperty(value = "任务描述", required = true)
    private String iconUrl;
    @ApiModelProperty(value = "当前进度", required = true)
    private BigDecimal current;
    @ApiModelProperty(value = "目标进度", required = true)
    private BigDecimal target;
    @ApiModelProperty(value = "1 - in progress , 2 - redeemable , 3 - redeemed", required = true)
    private Integer status;

}
