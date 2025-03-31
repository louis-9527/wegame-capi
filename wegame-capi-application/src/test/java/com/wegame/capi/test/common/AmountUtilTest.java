package com.wegame.capi.test.common;

import com.wegame.capi.common.utils.AmountUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

@Slf4j
public class AmountUtilTest {

    @Test
    public void decimalFormat_normal_test() {
        String decimalFormat;
         decimalFormat = AmountUtil.decimalFormat("#,##0.###", AmountUtil.toBigDecimal(1000006567L));
         assertEquals("1,000,006.567",decimalFormat);

        decimalFormat = AmountUtil.decimalFormat("#,##0.###", AmountUtil.toBigDecimal(1000006500L));
        assertEquals("1,000,006.5",decimalFormat);

        decimalFormat = AmountUtil.decimalFormat("#,##0.###", AmountUtil.toBigDecimal(1000006567L));
        assertEquals("1,000,006.567",decimalFormat);

        decimalFormat = AmountUtil.decimalFormat("#,##0.###", AmountUtil.toBigDecimal(567L));
        assertEquals("0.567",decimalFormat);

        decimalFormat = AmountUtil.decimalFormat("#,##0.###", new BigDecimal("100.5678"));
        assertEquals("100.567",decimalFormat);
    }
}
