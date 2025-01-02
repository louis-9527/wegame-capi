package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.GamePlayDo;
import com.wegame.capi.biz.domain.GamePlayResultDo;
import com.wegame.capi.biz.domain.SymbolDo;

public abstract class AbstractGame implements Game {

    public abstract GameInfoDo getGameInfo();

    public abstract SymbolDo[][] generate(GameInfoDo gameInfo, GamePlayDo gamePlay);

    // 返回： 游戏基础信息 + 游戏结果  todo
    public GamePlayResultDo play(GamePlayDo gamePlay) {
        // 获取游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成游戏结果
        generate(gameInfo, gamePlay);
        // todo
        // 返回
        return GamePlayResultDo.builder()
                .gameType(gameInfo.getGameType())
                .gameInfoDo(gameInfo)
//                . 中奖结果// todo
                .build();
    }


}
