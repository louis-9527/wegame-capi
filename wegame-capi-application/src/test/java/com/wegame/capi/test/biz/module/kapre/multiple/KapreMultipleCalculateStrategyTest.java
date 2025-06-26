package com.wegame.capi.test.biz.module.kapre.multiple;

import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
import com.wegame.capi.biz.module.base.multiple.MultipleCalculateStrategy;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class KapreMultipleCalculateStrategyTest extends BaseTest {

    @Autowired
    MultipleCalculateStrategy<KapreSymbolDo> kapreMultipleCalculateStrategy;

    @Test
    public void calculateMultiple_normal_normalBet_test() {
//        BigDecimal calculateMultiple = lamangMultipleCalculateStrategy.calculateMultiple();
    }

}
