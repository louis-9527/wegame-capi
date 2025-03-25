package com.wegame.capi.biz.module.base.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.module.base.bet.strategy.BetStrategy;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 抽象投注上下文
 */
@Component
public abstract class AbstractBetContext {

    @Autowired
    DistributeLock distributeLock;

    /**
     * 获取投注策略名称Map
     *
     * @return map
     */
    public abstract Map<BetTypeEnum, String> betStrategyNameMap();

    /**
     * 获取投注策略Map
     *
     * @return map
     */
    public abstract Map<String, BetStrategy> betStrategyMap();

    public void beforeBet(BetDo betDo) {
        // do nothing
    }

    public void afterBet(BetDo betDo, BetResultDo betResultDo) {
        // do nothing
    }

    public BetResultDo bet(BetDo betDo) {
        String betStrategyName = betStrategyNameMap().get(betDo.getBetType());
        AssertUtil.assertStrNotEmpty(betStrategyName, ErrorCode.ERR_PARAM, "Not Support BetType");
        BetStrategy betStrategy = betStrategyMap().get(betStrategyName);
        AssertUtil.assertNotNull(betStrategy, ErrorCode.ERR_PARAM, "Not Support BetType");
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_PARAM, "Not Login");
        beforeBet(betDo);
        BetResultDo betResultDo = betStrategy.bet(betDo);
        afterBet(betDo, betResultDo);
        return betResultDo;
    }
}
