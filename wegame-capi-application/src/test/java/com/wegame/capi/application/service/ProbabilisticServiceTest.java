package com.wegame.capi.application.service;//package com.wegame.capi.application.controller;

import com.wegame.capi.application.BaseTest;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.service.probabilistic.ProbabilisticService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@RestController
@RequestMapping("test")
@Api(value = "测试模块", tags = {"测试模块"})
public class ProbabilisticServiceTest extends BaseTest {

    @Autowired
    ProbabilisticService probabilisticService;

    @Test
    public void test_randomOneSymbol() {
        for (int index = 1; index <= 5; index++) {
            Map<SymbolDo, AtomicInteger> map = new HashMap<>();
            for (int i = 0; i < 100000000; i++) {
                SymbolDo symbolDo = probabilisticService.randomOneSymbol(10000L, index);
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