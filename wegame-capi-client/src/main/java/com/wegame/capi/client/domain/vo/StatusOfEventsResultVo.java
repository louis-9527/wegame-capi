package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("干预事件结果")
@EqualsAndHashCode(callSuper = false)
public class StatusOfEventsResultVo implements Serializable {
    private static final long serialVersionUID = 7891252423487716266L;
    @ApiModelProperty("干预事件ID")
    private String eventId;
    @ApiModelProperty("干预事件状态码")
    private Integer eventStatus;
    @ApiModelProperty("干预事件游戏金额")
    private BigDecimal executeBouns;
    @ApiModelProperty("干预事件订单号")
    private List<String> betNos;
    @ApiModelProperty("干预事件订单信息")
    private List<StatusOfEventsBetRecordVo> betRecords;
    @ApiModelProperty("执行时间")
    private Date executeTime;
}
