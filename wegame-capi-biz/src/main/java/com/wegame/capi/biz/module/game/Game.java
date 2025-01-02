package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.GamePlayDo;
import com.wegame.capi.biz.domain.GamePlayResultDo;

/**
 * 游戏
 */
public interface Game {

    GamePlayResultDo play(GamePlayDo play);

}
