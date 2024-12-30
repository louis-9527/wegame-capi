package com.wegame.capi.biz.handler;

import com.wegame.capi.biz.domain.game.MultipleDo;
import com.wegame.capi.biz.domain.game.MultipleResultDo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 倍数handler
 */
public interface MultipleHandler {
    /**
     * 处理
     *
     * @param multipleDo multipleDo
     * @return result
     */
    MultipleResultDo handle(MultipleDo multipleDo);

    /**
     * 获取倍数
     *
     * @param wildSymbolIdList wildSymbolIdList
     * @return result
     */
    BigDecimal getMultiple(List<Long> wildSymbolIdList);
}





