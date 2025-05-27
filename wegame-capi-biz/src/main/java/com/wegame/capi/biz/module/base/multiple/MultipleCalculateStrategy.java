package com.wegame.capi.biz.module.base.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 倍率计算策略
 */
public interface MultipleCalculateStrategy<T extends SymbolDo> {

    /**
     * 计算倍率
     * @param rewardHandle
     * @return 倍率
     */
    BigDecimal calculateMultiple(RewardHandleDo<T> rewardHandle, List<RewardLineDo<T>> rewardLineList);
}





