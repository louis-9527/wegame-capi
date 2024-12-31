package com.wegame.capi.biz.handler.context;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.game.MultipleDo;
import com.wegame.capi.biz.domain.game.MultipleResultDo;
import com.wegame.capi.biz.domain.game.OddsDo;
import com.wegame.capi.biz.domain.game.OddsResultDo;
import com.wegame.capi.biz.handler.MultipleHandler;
import com.wegame.capi.biz.handler.OddsHandler;
import com.wegame.capi.common.enums.MultipleHandlertypeEnum;
import com.wegame.capi.common.enums.OddsHandlertypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 倍数handler context
 */
@Slf4j
@Component
public class MultipleHandlerContext {
    private static final Map<MultipleHandlertypeEnum, String> handlerNameMap = new ImmutableMap.Builder<MultipleHandlertypeEnum, String>()
            .put(MultipleHandlertypeEnum.MARIA_MAKILING, MultipleHandlertypeEnum.MARIA_MAKILING.getHandlerName())
            .build();

    @Autowired
    Map<String, MultipleHandler> handlerMap;

    public MultipleResultDo handler(MultipleDo multipleDo) {
        MultipleHandlertypeEnum handlerType = MultipleHandlertypeEnum.valueOfGameId(multipleDo.getGameId());
        MultipleHandler multipleHandler = handlerMap.get(handlerNameMap.get(handlerType));
        return multipleHandler.handle(multipleDo);
    }

    public BigDecimal getMultiple(String gameId, List<Integer> wildSymbolIdList) {
        MultipleHandlertypeEnum handlerType = MultipleHandlertypeEnum.valueOfGameId(gameId);
        MultipleHandler multipleHandler = handlerMap.get(handlerNameMap.get(handlerType));
        return multipleHandler.getMultiple(wildSymbolIdList);
    }


}
