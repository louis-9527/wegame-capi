package com.wegame.capi.api.domain.res.singlewallet;

import com.wegame.capi.common.enums.BetStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SingleWalletBetWithPayoutRes extends SingleWalletBaseRes implements Serializable {
    private static final long serialVersionUID = -1827164647777864099L;
    private String playerName;
    private String betId;
    private BigDecimal credit;
    private String refId;
    private BetStatusEnum betStatus;
}
