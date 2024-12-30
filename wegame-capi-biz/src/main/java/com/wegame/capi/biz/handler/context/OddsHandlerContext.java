package com.wegame.capi.biz.handler.context;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.game.OddsDo;
import com.wegame.capi.biz.domain.game.OddsResultDo;
import com.wegame.capi.biz.domain.game.RewardDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.biz.handler.OddsHandler;
import com.wegame.capi.biz.handler.RewardHandler;
import com.wegame.capi.common.enums.OddsHandlertypeEnum;
import com.wegame.capi.common.enums.RewardHandlertypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 赔率handler context
 */
@Slf4j
@Component
public class OddsHandlerContext {

    private static final Map<OddsHandlertypeEnum, String> handlerNameMap = new ImmutableMap.Builder<OddsHandlertypeEnum, String>()
            .put(OddsHandlertypeEnum.MARIA_MAKILING, OddsHandlertypeEnum.MARIA_MAKILING.getHandlerName())
            .build();

    @Autowired
    Map<String, OddsHandler> handlerMap;

    public OddsResultDo handler(OddsDo oddsDo) {
        OddsHandlertypeEnum handlerType = OddsHandlertypeEnum.valueOfGameId(oddsDo.getGameId());
        OddsHandler oddsHandler = handlerMap.get(handlerNameMap.get(handlerType));
       return oddsHandler.handle(oddsDo);
    }

    public BigDecimal getOdds(OddsDo oddsDo) {
        OddsHandlertypeEnum handlerType = OddsHandlertypeEnum.valueOfGameId(oddsDo.getGameId());
        OddsHandler oddsHandler = handlerMap.get(handlerNameMap.get(handlerType));
        return oddsHandler.getOdds(oddsDo);
    }
}
