package com.wegame.capi.biz.handler;

import com.wegame.capi.biz.domain.game.RewardRuleDo;
import com.wegame.capi.biz.domain.game.RewardRuleResultDo;

/**
 * 中奖规则handler
 */
public interface RewardRuleHandler {

    /**
     * 处理
     *
     * @param rewardRuleDo rewardRuleDo
     * @return result
     */
    RewardRuleResultDo handle(RewardRuleDo rewardRuleDo);
}
