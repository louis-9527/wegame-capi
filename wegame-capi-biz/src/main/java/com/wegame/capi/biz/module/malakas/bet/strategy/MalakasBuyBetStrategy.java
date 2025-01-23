package com.wegame.capi.biz.module.malakas.bet.strategy;

import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.module.base.game.Game;
import org.springframework.stereotype.Component;

@Component("mariaMakilingBuyBetStrategy")
public class MalakasBuyBetStrategy extends AbstractMalakasBetStrategy {


    @Override
    public Game getGame() {
        return null;
    }

    @Override
    public BetResultDo<MariaMakilingGameRecordDo> bet(BetDo<MariaMakilingBetDo> betDo) {
        return null;
    }
}
