package com.wegame.capi.biz.module.base.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 抽象游戏上下文
 */
@Component
public abstract class AbstractGameContext<R> {

    /**
     * 获取GamePlay策略名称Map
     *
     * @return 策略名称Map
     */
    public abstract Map<GamePlayTypeEnum, String> playStrategyNameMap();

    /**
     * 获取GamePlay策略Map
     *
     * @return 策略Map
     */
    public abstract Map<String, Game> playStrategyMap();

    /**
     * 获取游戏信息
     * @return 游戏信息
     */
    public abstract GameInfoDo gameInfo();

    public List<R> play(GamePlayDo gamePlayDo) {
        String strategyName = playStrategyNameMap().get(gamePlayDo.getGamePlayType());
        AssertUtil.assertStrNotEmpty(strategyName, ErrorCode.ERR_PARAM, "Not Support GamePlayType");
        Game<R> game = playStrategyMap().get(strategyName);
        AssertUtil.assertNotNull(game, ErrorCode.ERR_PARAM, "Not Support GamePlayType");
        return game.play(gamePlayDo);
    }
}
