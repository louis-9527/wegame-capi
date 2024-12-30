package com.wegame.capi.biz.context;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.context.strategy.AbstractMysteriousBetStrategy;
import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import com.wegame.capi.common.enums.GameBetTypeEnum;
import com.wegame.capi.common.enums.GameTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mysterious投注上下文
 */
@Component("mysteriousBetContext")
public class MysteriousBetContext extends AbstractBetContext {

    private static final Map<GameBetTypeEnum, String> betStrategyNameMap = new ImmutableMap.Builder<GameBetTypeEnum, String>()
            .put(GameBetTypeEnum.NORMAL_BET, "mysteriousNormalBetStrategy")
            .put(GameBetTypeEnum.NON_BUY_FREE_SPINS_BET, "mysteriousNonBuyFreeSpinsBetStrategy")
            .put(GameBetTypeEnum.BUY_FREE_SPINS_BET, "mysteriousBuyFreeSpinsBetStrategy")
            .build();

    @Autowired
    Map<String, AbstractMysteriousBetStrategy> betStrategyMap;

    @Override
    public Long getGameId() {
        return 0L;
    }

    @Override
    public String gameName() {
        return "Mysterious";
    }

    @Override
    public <T> BetResultDo<T> bet(BetDo betDo) {
        return null;
    }
}
