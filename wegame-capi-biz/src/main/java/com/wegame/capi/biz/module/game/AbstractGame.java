package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.*;
import com.wegame.capi.biz.domain.game.RewardHandleDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.biz.module.reward.RewardHandler;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractGame implements Game {

    protected abstract RewardHandler getRewardHandler();

    /**
     * 生成滚轮
     *
     * @param gamePlay
     * @return
     */
    protected abstract List<WheelDo> generate(GamePlayDo gamePlay);

    public GamePlayResultDo play(GamePlayDo gamePlay) {
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成-游戏滚轮列表
        List<WheelDo> wheelList = generate(gamePlay);
        // 判断-滚轮中奖结果
        List<WheelRewardDo> wheelRewardList = wheelList.stream().map(wheel -> {
            RewardResultDo rewardResult = getRewardHandler().handle(RewardHandleDo.builder()
                    .gameType(gameInfo.getGameType())
                    .betType(gamePlay.getBetType())
                    .betSubType(gamePlay.getBetSubType())
                    .betAmount(gamePlay.getBetAmount())
                    .wheel(wheel).build());
            return WheelRewardDo.builder()
                    .wheelIndex(wheel.getIndex())
                    .wheel(wheel)
                    .rewardResult(rewardResult)
                    .build();
        }).collect(Collectors.toList());
        // 返回-结果信息
        return GamePlayResultDo.builder()
                .gameType(gameInfo.getGameType())
                .gameInfo(gameInfo)
                .wheelRewardList(wheelRewardList)
                .build();
    }
}
