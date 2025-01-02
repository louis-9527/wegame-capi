package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.GamePlayDo;
import com.wegame.capi.biz.domain.GamePlayResultDo;
import com.wegame.capi.biz.domain.WheelDo;
import com.wegame.capi.biz.domain.game.RewardHandleDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.biz.module.reward.strategy.RewardHandler;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractGame implements Game {

    protected abstract RewardHandler getRewardHandler();

    protected abstract List<WheelDo> generate(GamePlayDo gamePlay);

    public GamePlayResultDo play(GamePlayDo gamePlay) {
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成-游戏滚轮列表
        List<WheelDo> wheelList = generate(gamePlay);
        // 判断-中奖结果
        List<RewardResultDo> rewardResultList = wheelList.stream().map(wheel -> getRewardHandler().handle(RewardHandleDo.builder()
                .gameType(gameInfo.getGameType())
                .betType(gamePlay.getBetType())
                .betSubType(gamePlay.getBetSubType())
                .betAmount(gamePlay.getBetAmount())
                .wheel(wheel)
                .build())).collect(Collectors.toList());
        // 返回-结果信息
        return GamePlayResultDo.builder()
                .gameType(gameInfo.getGameType())
                .gameInfo(gameInfo)
                .wheelList(wheelList)
                .rewardResultList(rewardResultList)
                .build();
    }


}
