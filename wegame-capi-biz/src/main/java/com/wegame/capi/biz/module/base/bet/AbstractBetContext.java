package com.wegame.capi.biz.module.base.bet;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetContextDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.module.base.bet.strategy.BetStrategy;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.wegame.capi.common.Constants.OPT_BUSY;

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

    public BetResultDo bet(BetContextDo betContextDo) {
        String betStrategyName = betStrategyNameMap().get(betContextDo.getBetType());
        AssertUtil.assertStrNotEmpty(betStrategyName, ErrorCode.ERR_PARAM, "Not Support BetType");
        BetStrategy betStrategy = betStrategyMap().get(betStrategyName);
        AssertUtil.assertNotNull(betStrategy, ErrorCode.ERR_PARAM, "Not Support BetType");
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_NOT_LOGIN);

        if (BetSubTypeEnum.isFreeWheelQuery(betContextDo.getBetSubType().getCode())) {
            return betStrategy.bet(betContextDo);
        } else {
            return distributeLock.tryLockThenCall(LockNameUtil.getBetLockName(userInfo.getPlayerId()),
                    100,
                    3000,
                    () -> betStrategy.bet(betContextDo),
                    OPT_BUSY);
        }
    }
}
