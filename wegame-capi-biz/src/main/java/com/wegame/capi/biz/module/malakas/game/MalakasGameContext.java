package com.wegame.capi.biz.module.malakas.game;

import com.google.common.collect.ImmutableMap;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.module.base.game.AbstractGameContext;
import com.wegame.capi.biz.module.base.game.Game;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;

/**
 * MalakasGameContext GamePlay上下文
 */
@Component("malakasGameContext")
public class MalakasGameContext<R> extends AbstractGameContext<R> {

    private static final Map<GamePlayTypeEnum, String> STRATEGY_NAME_MAP = new ImmutableMap.Builder<GamePlayTypeEnum, String>()
            .put(GamePlayTypeEnum.NORMAL_PLAY, "malakasGameNormalPlay")
            .put(GamePlayTypeEnum.EVENT_PLAY, "malakasGameEventPlay")
            .put(GamePlayTypeEnum.TEMPLATE_PLAY, "malakasGameTemplatePlay")
            .put(GamePlayTypeEnum.INVENTORY_TEMPLATE_PLAY, "malakasGameInventoryTemplatePlay")
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
        return MALAKAS_GAME_INFO;
    }
}
