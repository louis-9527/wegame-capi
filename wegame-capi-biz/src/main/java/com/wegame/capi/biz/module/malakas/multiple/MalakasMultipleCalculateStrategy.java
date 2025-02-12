package com.wegame.capi.biz.module.malakas.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.module.base.multiple.AbstractMultipleCalculateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component("malakasMultipleCalculateStrategy")
public class MalakasMultipleCalculateStrategy extends AbstractMultipleCalculateStrategy<MalakasSymbolDo> {

    @Override
    public BigDecimal calculateMultiple(RewardHandleDo<MalakasSymbolDo> rewardHandle) {
        return null;
    }
}
