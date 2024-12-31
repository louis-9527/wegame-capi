package com.wegame.capi.biz.service;

import com.wegame.capi.biz.domain.game.RewardRuleResultDo;
import com.wegame.capi.biz.handler.context.RewardRuleHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 中奖规则服务
 */
@Slf4j
@Service
public class RewardRuleService {

    @Autowired
    RewardRuleHandlerContext rewardRuleHandlerContext;

    /**
     * @param gameId
     * @param betType
     * @param rollerGrid
     * @return
     */
    public RewardRuleResultDo calcRewardLines(Long gameId, Integer betType, Integer[][] rollerGrid) {
        // 中奖规则处理
        return rewardRuleHandlerContext.handler(gameId, betType, rollerGrid);
    }
}
