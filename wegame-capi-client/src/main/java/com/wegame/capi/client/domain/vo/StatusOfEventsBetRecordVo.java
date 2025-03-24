package com.wegame.capi.client.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("干预事件订单信息")
@EqualsAndHashCode(callSuper = false)
public class StatusOfEventsBetRecordVo {
    @ApiModelProperty(value = "订单号", required = true)
    private String betNo;
    @ApiModelProperty(value = "下注时间", required = true)
    private Date orderTime;
    @ApiModelProperty(value = "派奖时间", required = true)
    private Date settlementTime;
    @ApiModelProperty(value = "投注额度", required = true)
    private BigDecimal betAmount;
    @ApiModelProperty(value = "派彩额度", required = true)
    private BigDecimal payoutAmount;
}
