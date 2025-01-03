package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import org.springframework.stereotype.Component;

@Component("mariaMakilingNormalBetStrategy")
public class MariaMakilingNormalBetStrategy extends AbstractMariaMakilingBetStrategy<Object, Object> {

    @Override
    public BetResultDo<Object> bet(BetDo<Object> betDo) {
        // todo
        return null;
    }
}
