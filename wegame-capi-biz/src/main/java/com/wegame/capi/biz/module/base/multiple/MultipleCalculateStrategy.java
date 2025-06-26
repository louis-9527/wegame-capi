package com.wegame.capi.biz.module.base.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.common.enums.MultiplePieTypeEnum;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.List;

/**
 * 倍率计算策略
 */
public interface MultipleCalculateStrategy<T extends SymbolDo> {

    /**
     * 计算倍率
     * 计算(基础)倍率
     *
     * @param rewardHandle
     * @return 倍率
     */
    BigDecimal calculateMultiple(RewardHandleDo<T> rewardHandle, List<RewardLineDo<T>> rewardLineList);

    /**
     * 计算倍率盘倍率
     *
     * @param rewardHandle
     * @return <倍率盘类型，倍率>
     */
    default Pair<MultiplePieTypeEnum, BigDecimal> calculateMultiplePie(RewardHandleDo<SymbolDo> rewardHandle) {
        return null;
    }

    /**
     * 计算单条线中奖倍率
     *
     * @param rewardHandle
     * @param rewardLine
     * @return 倍率
     */
    default BigDecimal calculateMultipleOfSingleRewardLine(RewardHandleDo<T> rewardHandle, RewardLineDo<T> rewardLine) {
        return null;
    }
}





