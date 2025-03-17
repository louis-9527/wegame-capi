package com.wegame.capi.api.domain.req.dynamicoperation;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UpdateEventsBetRecordReq implements Serializable {
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
