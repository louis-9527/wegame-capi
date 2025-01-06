package com.wegame.capi.biz.module.game;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.GameInfoDo;
import com.wegame.capi.biz.domain.biz.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayResultDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelRewardDo;
import com.wegame.capi.biz.module.reward.RewardHandler;

import java.util.List;
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

    public GamePlayResultDo play(GamePlayDo gamePlay) {
        // 获取-游戏基础信息
        GameInfoDo gameInfo = getGameInfo();
        // 生成-游戏滚轮列表
        List<WheelDo> wheelList = generate(gamePlay);
        // 判断-滚轮中奖结果
        List<WheelRewardDo> wheelRewardList = wheelList.stream().map(wheel -> {
            RewardResultDo rewardResult = getRewardHandler().handle(build(gameInfo, gamePlay, wheel));
            AssertUtil.assertNotNull(rewardResult, ErrorCode.ERR_INTER);
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
