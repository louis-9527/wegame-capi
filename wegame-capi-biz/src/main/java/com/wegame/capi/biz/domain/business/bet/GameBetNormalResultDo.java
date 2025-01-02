package com.wegame.capi.biz.domain.business.bet;

import com.wegame.base.enums.BooleanEnum;
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

    private GameBetResultDetailDo resultDetailDo;

}
