package com.wegame.capi.biz.context.strategy.bet;

import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import org.springframework.stereotype.Component;

/**
 * 投注上下文
 */
@Component
public abstract class AbstractBetContext {
    /**
     * 游戏id
     * @return
     */
    public abstract Long getGameId();

    /**
     * 游戏名称
     * @return
     */
    public abstract String gameName();

    /**
     * 投注
     * @param betDo 投注
     * @return 投注结果
     */
    public abstract BetResultDo bet(BetDo betDo);

}
