package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("干预事件结果")
@EqualsAndHashCode(callSuper = false)
public class StatusOfEventsResultVo implements Serializable {
    private static final long serialVersionUID = 7891252423487716266L;
    @ApiModelProperty("事件ID")
    private String eventId;
    @ApiModelProperty("干预事件游戏金额")
    private BigDecimal executeBouns;
    @ApiModelProperty("执行时间")
    private Date executeTime;
    @ApiModelProperty("干预事件订单号")
    private List<String> betNos;
    @ApiModelProperty("干预事件订单信息")
    private List<StatusOfEventsRecordVo> betRecords;
    @ApiModelProperty("干预事件状态码")
    private Integer eventStatus;
}
