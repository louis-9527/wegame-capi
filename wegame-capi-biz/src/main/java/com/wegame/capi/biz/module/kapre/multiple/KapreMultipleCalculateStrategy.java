package com.wegame.capi.biz.module.kapre.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component("kapreMultipleCalculateStrategy")
public class KapreMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<KapreSymbolDo> {

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<KapreSymbolDo> rewardHandle, List<RewardLineDo<KapreSymbolDo>> rewardLineList) {
        throw new RuntimeException("todo");
    }
}
