package com.wegame.capi.biz.domain.biz.promotion;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FreeSpinDo implements Serializable {
    private static final long serialVersionUID = 343465397087279138L;
    /** Player name */
    private String playerName;
    /** Unique id of the bet */
    private String betId;
    /** Player balance */
    private Decimal credit;
    /** Request id of the request */
    private String refId;
    /** The number of free spins remaining in the player's activity */
    private Integer remainingCount;
    /** Campaign id of the free spin */
    private String campaignId;
}
