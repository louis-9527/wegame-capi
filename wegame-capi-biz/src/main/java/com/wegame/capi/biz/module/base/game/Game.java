package com.wegame.capi.biz.module.base.game;


import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;

import java.util.List;

/**
 * 游戏
 */
public interface Game<R> {

    /**
     * 获取游戏信息
     *
     * @return
     */
    GameInfoDo getGameInfo();

    /**
     * 玩游戏
     *
     * @param gamePlay
     * @return 游戏记录
     */
    List<R> play(GamePlayDo gamePlay);
}
