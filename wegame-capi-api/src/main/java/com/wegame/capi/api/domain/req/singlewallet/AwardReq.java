package com.wegame.capi.api.domain.req.singlewallet;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 结算/派彩请求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AwardReq extends BaseReq {
    /**
     * 下注ID
     */
    private String betId;
    /**
     * 结算金额
     */
    private BigDecimal awardAmount;
    /**
     * 结算明细
     */
    private AwardReq.AwardDetails awardDetails;

    /**
     * 结算明细
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class AwardDetails implements Serializable {
        /**
         * Bet settlement time Unix timestamps in seconds
         */
        private Long settlementDate;
    }
}
