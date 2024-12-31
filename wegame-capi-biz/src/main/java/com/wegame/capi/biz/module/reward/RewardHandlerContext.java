package com.wegame.capi.biz.module.reward;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.game.RewardDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.common.enums.RewardHandlertypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 中奖handler context
 */
@Slf4j
@Component
public class RewardHandlerContext {

    private static final Map<RewardHandlertypeEnum, String> handlerNameMap = new ImmutableMap.Builder<RewardHandlertypeEnum, String>()
            .put(RewardHandlertypeEnum.MARIA_MAKILING, RewardHandlertypeEnum.MARIA_MAKILING.getHandlerName())
            .build();

    @Autowired
    Map<String, RewardHandler> handlerMap;

    public RewardResultDo handler(Long gameId, Integer betType, Long[][] rollerGrid) {
        RewardHandlertypeEnum handlerType = RewardHandlertypeEnum.valueOfGameId(gameId);
        RewardHandler rewardHandler = handlerMap.get(handlerNameMap.get(handlerType));
        return rewardHandler.handle(RewardDo.builder().build());
    }
}
