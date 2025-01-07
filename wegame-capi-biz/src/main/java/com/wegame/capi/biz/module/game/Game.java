package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;

import java.util.List;

/**
 * 游戏
 */
public interface Game {

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
    List<MariaMakilingGameRecordDo> play(GamePlayDo gamePlay);
}
