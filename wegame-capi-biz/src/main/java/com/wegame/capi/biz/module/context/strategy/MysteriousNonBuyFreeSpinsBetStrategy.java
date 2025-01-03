package com.wegame.capi.biz.module.context.strategy;

import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import org.springframework.stereotype.Component;

@Component("mysteriousNonBuyFreeSpinsBetStrategy")
public class MysteriousNonBuyFreeSpinsBetStrategy extends AbstractMysteriousBetStrategy {

    @Override
   public BetResultDo bet(BetDo betDo) {
        // todo
        return null;
    }
}