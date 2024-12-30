package com.wegame.capi.biz.context;

import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import org.springframework.stereotype.Component;

/**
 * Mysterious投注上下文
 */
@Component
public class XxxBetContext extends AbstractBetContext {
    @Override
    public Long getGameId() {
        return 0L;
    }

    @Override
    public String gameName() {
        return "";
    }

    @Override
    public <T> BetResultDo<T> bet(BetDo betDo) {
        return null;
    }
}
