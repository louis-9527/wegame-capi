package com.wegame.capi.biz.context;

import com.wegame.capi.biz.context.strategy.bet.AbstractBetContext;
import org.springframework.stereotype.Component;

/**
 * Mysterious投注上下文
 */
@Component("mysteriousBetContext")
public class MysteriousBetContext extends AbstractBetContext {

    @Override
    public Long getGameId() {
        return 0L;
    }

    @Override
    public String gameName() {
        return "Mysterious";
    }
}
