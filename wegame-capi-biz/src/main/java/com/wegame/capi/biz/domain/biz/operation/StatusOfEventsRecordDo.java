package com.wegame.capi.biz.domain.biz.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 干预事件记录
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StatusOfEventsRecordDo implements Serializable {
    /**
     * 订单号
     */
    private String betNo;
    /**
     * 下注时间
     */
    private Long orderTime;
    /**
     * 派奖时间
     */
    private Long settlementTime;
    /**
     * 投注额度
     */
    private Long betAmount;
    /**
     * 派彩额度
     */
    private Long payoutAmount;
}
