package com.wegame.capi.client.domain.vo.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "任务")
public class DailyMissionVo implements  Serializable {
    private static final long serialVersionUID = -1704392640858325682L;
    @ApiModelProperty(value = "任务ID", required = true)
    private String dailyMissionId;
    @ApiModelProperty(value = "任务描述", required = true)
    private String description;
    @ApiModelProperty(value = "当前进度", required = true)
    private String experience;
    @ApiModelProperty(value = "目标进度", required = true)
    private String target;
    @ApiModelProperty(value = "icon地址", required = true)
    private String imgUrl;
    @ApiModelProperty(value = "状态 1锁定 2进行中 3完成", required = true)
    private String status;
}
