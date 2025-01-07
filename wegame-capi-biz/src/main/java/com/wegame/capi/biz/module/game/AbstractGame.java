package com.wegame.capi.biz.module.game;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.reward.RewardHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.RewardHandlerBuilder.build;

/**
 * 抽象游戏
 * @param <R>
 */
public abstract class AbstractGame<R extends ToVoConverter> implements Game<R> {

    public abstract RewardHandler getRewardHandler();

    public abstract List<WheelDo> generate(GamePlayDo gamePlay);

    public abstract R buildGameRecord(WheelDo wheel, RewardResultDo rewardResult);

    public List<R> play(GamePlayDo gamePlay) {
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成-游戏滚轮列表
        List<WheelDo> wheelList = generate(gamePlay);
        // 组装&返回-游戏记录列表
        return Optional.ofNullable(wheelList)
                .orElse(Collections.emptyList())
                .stream()
                .map(wheel -> buildGameRecord(wheel, getRewardHandler().handle(build(gameInfo, gamePlay, wheel))))
                .collect(Collectors.toList());
    }
}
