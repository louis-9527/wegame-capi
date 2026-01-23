package com.wegame.capi.biz.module.bayanihan.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.BayanihanSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component("bayanihanMultipleCalculateStrategy")
public class BayanihanMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<BayanihanSymbolDo> {
    private static final BigDecimal DEFAULT_MULTI = BigDecimal.valueOf(1);

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<BayanihanSymbolDo> rewardHandle, List<RewardLineDo<BayanihanSymbolDo>> rewardLineList) {
        //默认都是一倍
        return BigDecimal.ONE;
    }
}
