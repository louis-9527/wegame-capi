package com.wegame.capi.biz.builder;


import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;

import java.math.BigDecimal;

public class RewardHandlerBuilder {

    public static <T extends SymbolDo> RewardHandleDo<T> build(GameInfoDo gameInfo, GamePlayDo gamePlay, WheelDo<T> wheel, BigDecimal currentMultiple, Long totalRewardAmount) {
        AssertUtil.assertNotNull(gameInfo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gamePlay, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        return RewardHandleDo.<T>builder()
                .gameType(gameInfo.getGameType())
                .gameInfo(gameInfo)
                .betType(gamePlay.getBetType())
                .betSubType(gamePlay.getBetSubType())
                .betAmount(gamePlay.getBetAmount())
                .wheel(wheel)
                .currentMultiple(currentMultiple)
                .totalRewardAmount(totalRewardAmount)
                .build();
    }

}
