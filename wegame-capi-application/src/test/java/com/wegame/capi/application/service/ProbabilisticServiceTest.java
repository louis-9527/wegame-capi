package com.wegame.capi.application.service;//package com.wegame.capi.application.controller;

import com.wegame.capi.application.BaseTest;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.module.probabilistic.strategy.MariaMakilingProbabilisticStrategy;
import com.wegame.capi.common.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProbabilisticServiceTest extends BaseTest {

    @Autowired
    MariaMakilingProbabilisticStrategy mariaMakilingProbabilisticStrategy;

    @Test
    public void test_randomOneSymbol() {
        for (int index = 1; index <= 5; index++) {
            Map<SymbolDo, AtomicInteger> map = new HashMap<>();
            for (int i = 0; i < 100000000; i++) {
                SymbolDo symbolDo = mariaMakilingProbabilisticStrategy.randomOneSymbol(GameTypeEnum.MARIA_MAKILING, index);
                if (Objects.nonNull(symbolDo)) {
                    if (map.containsKey(symbolDo)) {
                        AtomicInteger atomicInteger = map.get(symbolDo);
                        atomicInteger.incrementAndGet();
                        map.put(symbolDo, atomicInteger);
                    } else {
                        AtomicInteger atomicInteger = new AtomicInteger(0);
                        atomicInteger.incrementAndGet();
                        map.put(symbolDo, atomicInteger);
                    }
                } else {
                    log.error("symbolDo is null");
                }
            }
            map.entrySet().forEach(item -> {
                log.info("symbol: {}, count: {}", item.getKey().getName(), item.getValue());
            });
            int sum = map.values().stream().map(item -> item.get()).reduce(0, Integer::sum);
            log.info("index: {}, sum: {}", index, sum);
            System.out.println();
            System.out.println();
        }
    }
}