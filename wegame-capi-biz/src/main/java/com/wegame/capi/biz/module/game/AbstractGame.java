package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.converter.Converter;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.reward.RewardHandler;

import java.util.List;

public abstract class AbstractGame<R extends Converter> implements Game<R> {

    public abstract RewardHandler getRewardHandler();

    /**
     * 生成滚轮
     *
     * @param gamePlay
     * @return
     */
    public abstract List<WheelDo> generate(GamePlayDo gamePlay);

    public abstract List<R> getRewardResult(GameInfoDo gameInfo, GamePlayDo gamePlay, List<WheelDo> wheelList);

    public List<R> play(GamePlayDo gamePlay) {
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成-游戏滚轮列表
        List<WheelDo> wheelList = generate(gamePlay);
        // 判断-滚轮中奖结果
        // 返回-游戏记录列表
        return getRewardResult(gameInfo, gamePlay, wheelList);
    }
}
