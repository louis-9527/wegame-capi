package com.wegame.capi.biz.context;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.context.strategy.AbstractMysteriousBetStrategy;
import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import com.wegame.capi.common.enums.BetTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mysterious投注上下文
 */
@Component("mysteriousBetContext")
public class MysteriousBetContext extends AbstractBetContext {

    private static final Map<BetTypeEnum, String> betStrategyNameMap = new ImmutableMap.Builder<BetTypeEnum, String>()
            .put(BetTypeEnum.NORMAL_BET, "mysteriousNormalBetStrategy")
            .put(BetTypeEnum.BUY_FREE_SPINS_BET, "mysteriousBuyFreeSpinsBetStrategy")
            .put(BetTypeEnum.NON_BUY_FREE_SPINS_BET, "mysteriousNonBuyFreeSpinsBetStrategy")
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
    public BetResultDo bet(BetDo betDo) {
        String betStrategyName = betStrategyNameMap.get(betDo.getBetType());
        AssertUtil.assertStrNotEmpty(betStrategyName, ErrorCode.ERR_PARAM, "Not Support BetType");
        AbstractMysteriousBetStrategy betStrategy = betStrategyMap.get(betStrategyName);
        AssertUtil.assertNotNull(betStrategy, ErrorCode.ERR_PARAM, "Not Support BetType");
        return betStrategy.bet(betDo);
    }
}
