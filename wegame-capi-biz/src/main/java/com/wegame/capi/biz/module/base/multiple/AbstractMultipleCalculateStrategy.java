package com.wegame.capi.biz.module.base.multiple;

import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractMultipleCalculateStrategy<T extends SymbolDo>
        implements MultipleCalculateStrategy<T> {

}
