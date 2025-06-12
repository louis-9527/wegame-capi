package com.wegame.capi.biz.module.diawatamayari.reward;

import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.module.base.multiple.MultipleCalculateStrategy;
import com.wegame.capi.biz.module.base.odds.OddsCalculateStrategy;
import com.wegame.capi.biz.module.base.reward.AbstractRewardHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("diwataMayariRewardHandler")
public class DiwataMayariRewardHandler extends AbstractRewardHandler<DiwataMayariSymbolDo> {

    @Autowired
    OddsCalculateStrategy<DiwataMayariSymbolDo> diwataMayariOddsCalculateStrategy;
    @Autowired
    MultipleCalculateStrategy<DiwataMayariSymbolDo> diwataMayariMultipleCalculateStrategy;
    @Autowired
    DiwataMayariConfig diwataMayariConfig;

    @Override
    public RewardResultDo<DiwataMayariSymbolDo> handle(RewardHandleDo<DiwataMayariSymbolDo> rewardHandle) {
        // todo
        throw new RuntimeException("DiwataMayariRewardHandler todo");
    }
}
