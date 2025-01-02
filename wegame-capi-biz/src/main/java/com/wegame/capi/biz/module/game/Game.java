package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.GamePlayDo;
import com.wegame.capi.biz.domain.GamePlayResultDo;

/**
 * 游戏
 */
public interface Game {

    GameInfoDo getGameInfo();
    GamePlayResultDo play(GamePlayDo play);

}
