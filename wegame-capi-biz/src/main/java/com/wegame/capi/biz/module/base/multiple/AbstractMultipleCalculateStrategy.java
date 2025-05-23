package com.wegame.capi.biz.module.base.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public abstract class AbstractMultipleCalculateStrategy<T extends SymbolDo> implements MultipleCalculateStrategy<T> {
    @Override
    public <R> R calculateMultipleWheelMultiple(RewardHandleDo<T> rewardHandle, List<RewardLineDo<T>> rewardLineList) {
        return null;
    }
}
