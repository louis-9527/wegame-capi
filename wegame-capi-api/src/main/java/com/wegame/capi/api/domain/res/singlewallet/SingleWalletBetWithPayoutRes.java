package com.wegame.capi.api.domain.res.singlewallet;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SingleWalletBetWithPayoutRes extends SingleWalletBaseRes implements Serializable {
    private static final long serialVersionUID = -1827164647777864099L;
    // common field
    /** Player name */
    private String playerName;
    /** Unique id of the bet */
    private String betId;
    /** Player balance */
    private BigDecimal credit;

    // free spin field
    /** The number of free spins remaining in the player's activity */
    private Integer remainingCount;
    /** Campaign id of the free spin */
    private String campaignId;
}
