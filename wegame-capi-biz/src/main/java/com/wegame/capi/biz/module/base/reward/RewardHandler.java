package com.wegame.capi.biz.module.base.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;

/**
 * 中奖handler
 */
public interface RewardHandler {

    /**
     * 中奖处理
     *
     * @param rewardHandle rewardHandle
     * @return result
     */
    RewardResultDo handle(RewardHandleDo rewardHandle);
}
