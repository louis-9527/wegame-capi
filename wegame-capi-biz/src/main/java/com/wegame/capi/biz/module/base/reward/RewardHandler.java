package com.wegame.capi.biz.module.base.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;

/**
 * 中奖handler
 */
public interface RewardHandler<T extends SymbolDo> {

    /**
     * 中奖处理
     *
     * @param rewardHandle rewardHandle
     * @return result
     */
    RewardResultDo<T> handle(RewardHandleDo<T> rewardHandle);
}
