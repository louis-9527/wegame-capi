package com.wegame.capi.biz.module.base.game;


import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;

import java.util.List;

/**
 * 游戏
 */
public interface Game<R> {

    /**
     * 玩游戏
     *
     * @param gamePlay
     * @return 游戏记录
     */
    List<R> play(GamePlayDo gamePlay);

    /**
     * 玩游戏
     *
     * @param betDo 投注上下文
     * @param gamePlay 游戏上下文
     * @return 游戏记录
     */
    default List<R> play(BetDo betDo, GamePlayDo gamePlay) {
        // do nothing
        return null;
    }
}
