package com.wegame.capi.api.domain.req.singlewallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BetWithPayoutReq extends SingleWalletBaseReq {
    private static final long serialVersionUID = 2474046915423606033L;

    private BigDecimal betAmount;

    private BigDecimal payoutAmount;

    private BetDetails betDetails;


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
   public static class BetDetails {
        private String gameId;
        private String roundNo;
        private Long wagersDate;
        private Long settlementDate;
        private Boolean isFreeRound;
    }

}
