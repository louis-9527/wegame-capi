package com.wegame.capi.biz.module.reward;

import com.wegame.capi.biz.domain.game.RewardDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;

/**
 * 中奖handler
 */
public interface RewardHandler {

    /**
     * 处理
     *
     * @param rewardDo rewardDo
     * @return result
     */
    RewardResultDo handle(RewardDo rewardDo);

}
