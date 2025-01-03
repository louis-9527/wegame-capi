package com.wegame.capi.biz.module.bet.strategy.mariamakiling;

import com.wegame.base.Validate;
import com.wegame.capi.biz.module.bet.strategy.AbstractBetStrategy;
import com.wegame.capi.biz.module.game.Game;
import com.wegame.capi.biz.module.game.MariaMakilingGame;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractMariaMakilingBetStrategy<T, R> extends AbstractBetStrategy<T, R> {

    @Autowired
    MariaMakilingGame mariaMakilingGame;

    @Override
    public Game getGame() {
        return mariaMakilingGame;
    }
}
