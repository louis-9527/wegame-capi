package com.wegame.capi.client.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "对账请求参数")
public class BetReconciliationDto implements Serializable {
    @ApiModelProperty(value = "渠道id", required = true)
    private String channelId;
    @ApiModelProperty(value = "hash", required = true)
    private String hash;
    @ApiModelProperty(value = "request id", required = true)
    private String refId;
    @ApiModelProperty(value = "时间", required = true)
    private Date createTime;
    @ApiModelProperty(value = "0: Bet time; 1: Settlement time", required = true)
    private Integer timeType;
    @ApiModelProperty(value = "开始时间", required = true)
    private Long startTime;
    @ApiModelProperty(value = "结束时间", required = true)
    private Long endTime;
}
