package com.wegame.capi.biz.builder;


import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;

public class RewardHandlerBuilder {

    public static RewardHandleDo build(GameInfoDo gameInfo, GamePlayDo gamePlay, WheelDo wheel) {
        AssertUtil.assertNotNull(gameInfo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gamePlay, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        return RewardHandleDo.builder()
                .gameType(gameInfo.getGameType())
                .betType(gamePlay.getBetType())
                .betSubType(gamePlay.getBetSubType())
                .betAmount(gamePlay.getBetAmount())
                .wheel(wheel)
                .build();
    }

}
