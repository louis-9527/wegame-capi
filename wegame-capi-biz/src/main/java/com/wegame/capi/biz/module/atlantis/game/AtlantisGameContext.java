package com.wegame.capi.biz.module.atlantis.game;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.module.atlantis.AtlantisGameHelper;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * AtlantisGameContext GamePlay上下文
 */
@Component("atlantisGameContext")
public class AtlantisGameContext<R> extends AbstractGameContext<R> {

    private static final Map<GamePlayTypeEnum, String> STRATEGY_NAME_MAP = new ImmutableMap.Builder<GamePlayTypeEnum, String>()
            .put(GamePlayTypeEnum.NORMAL_PLAY, "atlantisGameNormalPlay")
            .put(GamePlayTypeEnum.EVENT_PLAY, "atlantisGameEventPlay")
            .put(GamePlayTypeEnum.TEMPLATE_PLAY, "atlantisGameTemplatePlay")
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
        return AtlantisGameHelper.ATLANTIS_GAME_INFO;
    }
}
