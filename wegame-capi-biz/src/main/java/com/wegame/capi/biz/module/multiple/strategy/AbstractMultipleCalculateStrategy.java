package com.wegame.capi.biz.module.multiple.strategy;

import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.common.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public abstract class AbstractMultipleCalculateStrategy implements MultipleCalculateStrategy {

    public BigDecimal calculateMultiple(GameTypeEnum gameType, SymbolDo[][] symbolArr) {
        // todo 根据SymbolDo关联的规则计算倍率
        return BigDecimal.ZERO;
    }

}
