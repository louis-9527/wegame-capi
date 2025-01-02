package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.*;
import com.wegame.capi.biz.domain.game.RewardHandleDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.biz.module.reward.strategy.RewardHandler;

import java.util.List;

public abstract class AbstractGame implements Game {

//    private RewardHandler rewardHandler;

    protected abstract List<WheelDo> generate(GamePlayDo gamePlay);

    public GamePlayResultDo play(GamePlayDo gamePlay) {
        // 获取游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成游戏滚轮
        List<WheelDo> wheelList = generate(gamePlay);
        // 中奖结果 todo 待定
        //        RewardResultDo resultDo = rewardHandler.handle(RewardHandleDo.builder()
        //                .gameType(gameInfo.getGameType())
        //                .betType(gamePlay.getBetType())
        //                .betSubType(gamePlay.getBetSubType())
        //                .betAmount(gamePlay.getBetAmount())
        //                .build());
        // 返回
        return GamePlayResultDo.builder()
                .gameType(gameInfo.getGameType())
                .gameInfo(gameInfo)
                .wheelList(wheelList)
                .rewardResult(null) // todo
                .build();
    }


}
