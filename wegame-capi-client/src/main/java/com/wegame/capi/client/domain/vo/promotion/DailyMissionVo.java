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
@ApiModel(description = "任务")
public class DailyMissionVo implements  Serializable {

    private static final long serialVersionUID = -1704392640858325682L;
    @ApiModelProperty(value = "任务ID", required = true)
    private String dailyMissionId;
    @ApiModelProperty(value = "任务名称", required = true)
    private String description;
    @ApiModelProperty(value = "当前进度", required = true)
    private BigDecimal experience;
    @ApiModelProperty(value = "目标进度", required = true)
    private BigDecimal target;
    @ApiModelProperty(value = "状态:1 - in progress , 2 - finish", required = true)
    private Integer status;
    @ApiModelProperty(value = "等级", required = true)
    private Integer starLevel;

}
