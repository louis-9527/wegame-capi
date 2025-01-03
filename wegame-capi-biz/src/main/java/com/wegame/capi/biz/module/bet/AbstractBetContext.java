package com.wegame.capi.biz.module.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import com.wegame.capi.biz.module.bet.strategy.BetStrategy;
import com.wegame.capi.common.enums.BetSubTypeEnum;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 抽象投注上下文
 */
@Component
public abstract class AbstractBetContext {

    /**
     * 获取投注策略名称Map
     *
     * @return map
     */
    public abstract Map<BetSubTypeEnum, String> betStrategyNameMap();

    /**
     * 获取投注策略Map
     *
     * @return map
     */
    public abstract Map<String, BetStrategy<?, ?>> betStrategyMap();

    public BetResultDo<?> bet(BetDo<?> betDo) {
        String betStrategyName = betStrategyNameMap().get(betDo.getBetType());
        AssertUtil.assertStrNotEmpty(betStrategyName, ErrorCode.ERR_PARAM, "Not Support BetType");
        BetStrategy betStrategy = betStrategyMap().get(betStrategyName);
        AssertUtil.assertNotNull(betStrategy, ErrorCode.ERR_PARAM, "Not Support BetType");
        return betStrategy.bet(betDo);
    }

}
