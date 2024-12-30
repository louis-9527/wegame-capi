package com.wegame.capi.client.domain.request;

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
@ApiModel(description = "对账请求参数")
public class BetSummaryVo implements Serializable {
    @ApiModelProperty(value = "Number of bets", required = true)
    private Long betCount;
    @ApiModelProperty(value = "Amount of bets", required = true)
    private BigDecimal totalBet;
    @ApiModelProperty(value = "Amount of payouts", required = true)
    private BigDecimal totalPayout;
}
