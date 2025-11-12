package com.wegame.capi.test.common;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import lombok.*;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.Assert.*;

public class AmountTest {

    private List<AmountMockerModel> mockerModels;
    private static final Integer SEEK = 100;
    private static final BigDecimal TIMES = new BigDecimal("1.5");
    private static final Integer TEST_COUNT = 100;


    @Before
    public void before() {
        mockerModels = Lists.newArrayList();
        for (int i = 0; i < TEST_COUNT; i++) {
            long random = RandomUtil.randomLong(100, 1000000, true, true);
            // 保证是100的倍数
            long betAmount = (random / SEEK) * SEEK;
            long payAmount = (long) (betAmount * TIMES.doubleValue());
            mockerModels.add(new AmountMockerModel(payAmount, betAmount));
        }
    }

    @Test
    public void exAmount_test() {
        mockerModels.forEach(amountMockerModel -> {
            BigDecimal payAmountDecimal = BigDecimal.valueOf(amountMockerModel.getPayAmount());
            BigDecimal betAmountDecimal = BigDecimal.valueOf(amountMockerModel.getBetAmount());
            BigDecimal exceptPayAmount = betAmountDecimal.multiply(new BigDecimal("1.5")).setScale(0, RoundingMode.FLOOR);
            assertEquals(0, exceptPayAmount.compareTo(payAmountDecimal));
        });
    }

    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AmountMockerModel implements Serializable {
        private Long payAmount;
        private Long betAmount;
    }
}
