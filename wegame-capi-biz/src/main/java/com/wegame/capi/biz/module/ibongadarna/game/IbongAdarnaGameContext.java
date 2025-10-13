package com.wegame.capi.biz.module.ibongadarna.game;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.wegame.capi.biz.module.ibongadarna.IbongAdarnaGameHelper.IBONG_ADARNA_GAME_INFO;

/**
 * IbongAdarnaGameContext GamePlay上下文
 */
@Component("ibongAdarnaGameContext")
public class IbongAdarnaGameContext extends AbstractGameContext {

    private static final Map<GamePlayTypeEnum, String> STRATEGY_NAME_MAP = new ImmutableMap.Builder<GamePlayTypeEnum, String>()
            .put(GamePlayTypeEnum.NORMAL_PLAY, "ibongAdarnaGameNormalPlay")
            .put(GamePlayTypeEnum.EVENT_PLAY, "ibongAdarnaGameEventPlay")
            .put(GamePlayTypeEnum.TEMPLATE_PLAY, "ibongAdarnaGameTemplatePlay")
            .build();

    @Autowired
    Map<String, Game> gamePlayStrategyMap;

    @Override
    public Map<GamePlayTypeEnum, String> playStrategyNameMap() {
        return STRATEGY_NAME_MAP;
    }

    @Override
    public Map<String, Game> playStrategyMap() {
        return gamePlayStrategyMap;
    }

    @Override
    public GameInfoDo gameInfo() {
        return IBONG_ADARNA_GAME_INFO;
    }
}
