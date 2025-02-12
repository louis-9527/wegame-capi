package com.wegame.capi.biz.module.base.multiple;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.enums.GameTypeEnum;

import java.math.BigDecimal;

/**
 * 倍率计算策略
 */
public interface MultipleCalculateStrategy<T extends SymbolDo> {

    /**
     * 计算赔率
     * @param rewardHandle 游戏类型
     * @return 元素数组
     */
    BigDecimal calculateMultiple(RewardHandleDo<T> rewardHandle);
}





