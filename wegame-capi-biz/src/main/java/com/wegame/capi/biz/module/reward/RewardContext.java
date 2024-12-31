package com.wegame.capi.biz.module.reward;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.game.RewardResultDo;
import com.wegame.capi.biz.module.reward.strategy.RewardStrategy;
import com.wegame.capi.common.enums.BetSubTypeEnum;
import com.wegame.capi.common.enums.BetTypeEnum;
import com.wegame.capi.common.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 中奖编排服务
 */
@Slf4j
@Service
public class RewardContext {

    private static final Map<GameTypeEnum, String> strategyNameMap = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.MARIA_MAKILING, "mariaMakilingRewardStrategy")
            .build();

    @Autowired
    Map<String, RewardStrategy> rewardStrategyMap;

    // todo 参数封装
    public RewardResultDo handle(GameTypeEnum gameType,
                                 BetTypeEnum betType,
                                 BetSubTypeEnum betSubType,
                                 Long betAmount,
                                 SymbolDo[][] symbolArr) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "Not Support Game");
        String strategyName = strategyNameMap.get(gameType);
        RewardStrategy strategy = rewardStrategyMap.get(strategyName);
        AssertUtil.assertNotNull(strategy, ErrorCode.ERR_INTER, "NOT SUPPORT");
        return strategy.handle(gameType, betType, betSubType, betAmount, symbolArr);
    }
}
