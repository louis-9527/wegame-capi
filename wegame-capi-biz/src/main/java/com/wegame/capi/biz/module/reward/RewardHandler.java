package com.wegame.capi.biz.module.reward;

import com.wegame.capi.biz.domain.RewardHandleDo;
import com.wegame.capi.biz.domain.RewardResultDo;

/**
 * 中奖handler
 */
public interface RewardHandler {

    /**
     * 中奖处理
     *
     * @param rewardHandle
     * @return
     */
    RewardResultDo handle(RewardHandleDo rewardHandle);

}
