package com.wegame.capi.biz.domain.business.bet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameBetNormalResultDo {
    /**
     * 免费轮获得标识 0否 1是
     */
    private GameBetResultDetailDo resultDetailDo;

}
