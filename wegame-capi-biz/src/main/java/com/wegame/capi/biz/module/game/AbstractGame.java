package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.GameInfoDo;
import com.wegame.capi.biz.domain.GamePlayDo;
import com.wegame.capi.biz.domain.GamePlayResultDo;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.game.RewardHandleDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.biz.module.reward.strategy.RewardHandler;

import java.util.List;

public abstract class AbstractGame implements Game {

//    private RewardHandler rewardHandler;

    protected abstract List<SymbolDo[][]> generate(GamePlayDo gamePlay);

    public GamePlayResultDo play(GamePlayDo gamePlay) {
        // 获取游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成游戏结果
        List<SymbolDo[][]> symbolArrList = generate(gamePlay);
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
                .gameInfoDo(gameInfo)
                .symbolArrList(symbolArrList)
                .rewardResult(null) // todo
                .build();
    }


}
