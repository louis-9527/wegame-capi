package com.wegame.capi.api.domain.req.promotion;

import com.wegame.capi.api.domain.req.BaseReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FreeSpinReq extends BaseReq {

    private static final long serialVersionUID = -3546749350564193728L;
    /** payoff amount */
    private Integer payoutAmount;
    /** Bet details */
    private BetDetails betDetails;
    /** Free Spin type : 1 Normal */
    private Integer freeSpinType;
    /** Campaign id of the free spin  */
    private String campaignId;

    @NoArgsConstructor
    @Data
    public static class BetDetails {
        /** Game unique id */
        private String gameId;
        /** Game record unique id */
        private String roundNo;
        /** Bet time [Unix timestamps in seconds] */
        private Integer wagersDate;
        /** Bet settlement time [Unix timestamps in seconds] */
        private Integer settlementDate;
    }
}
