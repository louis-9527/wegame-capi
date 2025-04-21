package com.wegame.capi.api.domain.req.promotion;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FreeSpinReq extends BaseReq {

    private static final long serialVersionUID = -3546749350564193728L;
    /** payoff amount */
    private BigDecimal payoutAmount;
    /** Bet details */
    private BetDetails betDetails;
    /** Free Spin type : 1 Normal */
    private Integer freeSpinType;
    /** Campaign id of the free spin  */
    private String campaignId;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BetDetails {
        /** Game unique id */
        private String gameId;
        /** Game record unique id */
        private String roundNo;
        /** Bet time [Unix timestamps in seconds] */
        private Long wagersDate;
        /** Bet settlement time [Unix timestamps in seconds] */
        private Long settlementDate;
    }
}
