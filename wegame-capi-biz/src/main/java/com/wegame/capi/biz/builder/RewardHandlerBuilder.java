package com.wegame.capi.biz.builder;


import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.game.GameInfoDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.bizevent.GameBizEventDo;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;

import java.math.BigDecimal;
import java.util.List;

public class RewardHandlerBuilder {

    public static <T extends SymbolDo> RewardHandleDo<T> build(GameInfoDo gameInfo, GamePlayDo gamePlay, WheelDo<T> wheel, BigDecimal currentMultiple) {
        AssertUtil.assertNotNull(gameInfo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gamePlay, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        return RewardHandleDo.<T>builder()
                .gameType(gameInfo.getGameType())
                .gameInfo(gameInfo)
                .betType(gamePlay.getBetType())
                .betSubType(gamePlay.getBetSubType())
                .gamePlayType(gamePlay.getGamePlayType())
                .betAmount(gamePlay.getBetAmount())
                .wheel(wheel)
                .currentMultiple(currentMultiple)
                .build();
    }

    public static <T extends SymbolDo> RewardHandleDo<T> build(GameInfoDo gameInfo, GamePlayDo gamePlay, WheelDo<T> wheel, BigDecimal currentMultiple, Long currentTotalRewardAmount) {
        AssertUtil.assertNotNull(gameInfo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gamePlay, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(wheel, ErrorCode.ERR_INTER);
        return RewardHandleDo.<T>builder()
                .gameType(gameInfo.getGameType())
                .gameInfo(gameInfo)
                .betType(gamePlay.getBetType())
                .betSubType(gamePlay.getBetSubType())
                .gamePlayType(gamePlay.getGamePlayType())
                .betAmount(gamePlay.getBetAmount())
                .payAmount(gamePlay.getPayAmount())
                .wheel(wheel)
                .currentMultiple(currentMultiple)
                .currentTotalRewardAmount(currentTotalRewardAmount)
                .build();
    }

    /**
     * atlantis add
     */
    public static <T extends SymbolDo> RewardHandleDo<T> build(GameInfoDo gameInfo,
                                                               GamePlayDo gamePlay,
                                                               WheelDo<T> wheel,
                                                               BigDecimal currentMultiple,
                                                               Long currentTotalRewardAmount,
                                                               Integer totalRewardCount,
                                                               List<GameBizEventDo> fixedBizEventList) {

        RewardHandleDo<T> rewardHandleDo = build(gameInfo, gamePlay, wheel, currentMultiple, currentTotalRewardAmount);
        rewardHandleDo.setTotalRewardCount(totalRewardCount);
        rewardHandleDo.setFixedBizEventList(fixedBizEventList);
        return rewardHandleDo;
    }

    /**
     * bayanihan add
     */
    public static <T extends SymbolDo> RewardHandleDo<T> build(GameInfoDo gameInfo,
                                                               GamePlayDo gamePlay,
                                                               WheelDo<T> wheel,
                                                               BigDecimal currentMultiple,
                                                               Integer rewardFreeWheelTimes) {

        RewardHandleDo<T> rewardHandleDo = build(gameInfo, gamePlay, wheel, currentMultiple);
        rewardHandleDo.setActualRewardFreeWheelTimes(rewardFreeWheelTimes);
        return rewardHandleDo;
    }

}
