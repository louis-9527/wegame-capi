package com.wegame.capi.biz.module.lamang.game;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.wegame.capi.biz.module.lamang.LamangGameHelper.LAMANG_GAME_INFO;
import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * lamangGameContext GamePlay上下文
 */
@Component("lamangGameContext")
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangGameContext<R> extends AbstractGameContext<R> {

    private static final Map<GamePlayTypeEnum, String> STRATEGY_NAME_MAP = new ImmutableMap.Builder<GamePlayTypeEnum, String>()
            .put(GamePlayTypeEnum.TEMPLATE_PLAY, "lamangGameTemplatePlay")
            .put(GamePlayTypeEnum.NORMAL_PLAY, "lamangGameNormalPlay")
            .put(GamePlayTypeEnum.EVENT_PLAY, "lamangGameEventPlay")
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
        return LAMANG_GAME_INFO;
    }
}
