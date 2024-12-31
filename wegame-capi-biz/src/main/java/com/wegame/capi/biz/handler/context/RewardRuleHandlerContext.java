package com.wegame.capi.biz.handler.context;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.game.RewardRuleDo;
import com.wegame.capi.biz.domain.game.RewardRuleResultDo;
import com.wegame.capi.biz.handler.RewardRuleHandler;
import com.wegame.capi.common.enums.RewardRuleHandlertypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 中奖规则handler context
 */
@Slf4j
@Component
public class RewardRuleHandlerContext {

    private static final Map<RewardRuleHandlertypeEnum, String> handlerNameMap = new ImmutableMap.Builder<RewardRuleHandlertypeEnum, String>()
            .put(RewardRuleHandlertypeEnum.MARIA_MAKILING, RewardRuleHandlertypeEnum.MARIA_MAKILING.getHandlerName())
            .build();

    @Autowired
    Map<String, RewardRuleHandler> handlerMap;

    public RewardRuleResultDo handler(String gameId, Integer betType, Integer[][] rollerGrid) {
        RewardRuleHandlertypeEnum handlerType = RewardRuleHandlertypeEnum.valueOfGameId(gameId);
        RewardRuleHandler rewardRuleHandler = handlerMap.get(handlerNameMap.get(handlerType));
        return rewardRuleHandler.handle(RewardRuleDo.builder()
                .gameId(gameId)
                .betType(betType)
                .rollerGrid(rollerGrid)
                .build());
    }

}
