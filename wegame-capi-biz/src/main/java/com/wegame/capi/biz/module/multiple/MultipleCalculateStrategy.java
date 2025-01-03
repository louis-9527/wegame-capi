package com.wegame.capi.biz.module.multiple;

import com.wegame.capi.biz.domain.WheelDo;
import com.wegame.capi.client.enums.GameTypeEnum;

import java.math.BigDecimal;

/**
 * 倍率计算策略
 */
public interface MultipleCalculateStrategy {

    /**
     * 计算赔率
     *
     * @param gameType 游戏类型
     * @return 元素数组
     */
    BigDecimal calculateMultiple(GameTypeEnum gameType,  WheelDo wheel);
}





