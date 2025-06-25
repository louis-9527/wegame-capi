package com.wegame.capi.biz.module.kapre.reward;

import com.wegame.capi.biz.config.domain.KapreConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
import com.wegame.capi.biz.module.base.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.base.odds.OddsCalculateStrategy;
import com.wegame.capi.biz.module.base.reward.AbstractRewardHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component("kapreRewardHandler")
public class KapreRewardHandler extends AbstractRewardHandler<KapreSymbolDo> {
    private static final Integer MIN_SCATTER_COUNT_FOR_FREE_SPIN = 4;

    @Autowired
    OddsCalculateStrategy<KapreSymbolDo> kapreOddsCalculateStrategy;
    @Autowired
    MultipleCalculateStrategy<KapreSymbolDo> kapreMultipleCalculateStrategy;
    @Autowired
    KapreConfig kapreConfig;

    @Override
    public RewardResultDo<KapreSymbolDo> handle(RewardHandleDo<KapreSymbolDo> rewardHandle) {
        return null;
    }
}
