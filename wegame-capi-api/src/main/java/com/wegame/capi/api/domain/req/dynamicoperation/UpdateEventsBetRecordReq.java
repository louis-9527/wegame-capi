package com.wegame.capi.api.domain.req.dynamicoperation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventsBetRecordReq {
    /**
     * 订单号
     */
    private String betNo;

    /**
     * 下注事件
     */
    private Date orderTime;

    /**
     * 派奖时间
     */
    private Date settlementTime;
    /**
     * 投注额度
     */
    private BigDecimal betAmount;

    /**
     * 派彩额度
     */
    private BigDecimal payoutAmount;
}
