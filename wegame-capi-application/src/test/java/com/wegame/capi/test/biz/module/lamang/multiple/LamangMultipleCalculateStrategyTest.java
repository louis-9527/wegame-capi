package com.wegame.capi.test.biz.module.lamang.multiple;

import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.module.base.multiple.MultipleCalculateStrategy;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class LamangMultipleCalculateStrategyTest extends BaseTest {

    @Autowired
    MultipleCalculateStrategy<LamangSymbolDo> lamangMultipleCalculateStrategy;

    @Test
    public void calculateMultiple_normal_normalBet_test() {
//        BigDecimal calculateMultiple = lamangMultipleCalculateStrategy.calculateMultiple();
    }

}
