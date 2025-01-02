package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.SymbolDo;

public abstract class AbstractGameStrategy implements GameStrategy {

    public abstract GameInfoDo getGameInfo();

    public abstract SymbolDo[][] generate();

    // 返回： 游戏基础信息 + 游戏结果  todo
    public Object play(Object gamePlayDo) {
        // 获取游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成游戏结果
        // todo
        // 返回
        return null;
    }


}
