package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.GamePlayDo;
import com.wegame.capi.biz.domain.GamePlayResultDo;

/**
 * 游戏
 */
public interface Game {

    /**
     * 获取游戏信息
     * @return
     */
    GameInfoDo getGameInfo();

    /**
     * 玩游戏
     * @param gamePlay
     * @return
     */
    GamePlayResultDo play(GamePlayDo gamePlay);
}
