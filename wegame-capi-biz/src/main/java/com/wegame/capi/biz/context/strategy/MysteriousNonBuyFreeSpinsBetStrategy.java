package com.wegame.capi.biz.context.strategy;

import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import org.springframework.stereotype.Component;

@Component("mysteriousNonBuyFreeSpinsBetStrategy")
public class MysteriousNonBuyFreeSpinsBetStrategy extends AbstractMysteriousBetStrategy {

    @Override
    BetResultDo bet(BetDo betDo) {
        // todo
        return null;
    }
}