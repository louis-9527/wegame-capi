package com.wegame.capi.biz.module.reward;

import com.wegame.capi.biz.domain.game.RewardHandleDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;

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
