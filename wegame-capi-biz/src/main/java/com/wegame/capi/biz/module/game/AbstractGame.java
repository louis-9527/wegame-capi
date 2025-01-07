package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.reward.RewardHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;

public abstract class AbstractGame implements Game {

    public abstract RewardHandler getRewardHandler();

    /**
     * 生成滚轮
     *
     * @param gamePlay
     * @return
     */
    public abstract List<WheelDo> generate(GamePlayDo gamePlay);

    public List<MariaMakilingGameRecordDo> play(GamePlayDo gamePlay) {
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成-游戏滚轮列表
        List<WheelDo> wheelList = generate(gamePlay);
        // 判断-滚轮中奖结果
        List<MariaMakilingGameRecordDo> gameRecords = Optional.ofNullable(wheelList)
                .orElse(Collections.emptyList())
                .stream()
                .map(wheel -> {
                    RewardResultDo rewardResult = getRewardHandler().handle(build(gameInfo, gamePlay, wheel));
                    return MariaMakilingGameRecordDo.builder()
                            .index(wheel.getIndex())
                            .wheel(wheel)
                            .rewardResult(rewardResult)
                            .build();
                }).collect(Collectors.toList());
        // 返回-游戏记录列表
        return gameRecords;
    }
}
