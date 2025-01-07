package com.wegame.capi.biz.domain.biz.bet;

import com.wegame.capi.common.enums.GameBetStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BetSingleWalletResultDo {
    private String betId;
    private String refId;
    private BigDecimal credit;
    private GameBetStatusEnum betStatus;
}
