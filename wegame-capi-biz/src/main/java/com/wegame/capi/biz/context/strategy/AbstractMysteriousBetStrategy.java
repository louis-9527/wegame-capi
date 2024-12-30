package com.wegame.capi.biz.context.strategy;

import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;

/**
 * 投注策
 */
public abstract class AbstractMysteriousBetStrategy {

    /**
     * 投注
     * @param betDo
     * @return
     */
    public abstract BetResultDo bet(BetDo betDo);

}
