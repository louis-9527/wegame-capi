package com.wegame.capi.biz.domain.business.bet;

import com.wegame.capi.biz.domain.business.bet.GameBetReqDo;
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
public class GameBetNormalReqDo{

    private Long betAmount;
}
