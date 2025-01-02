package com.wegame.capi.application.service;//package com.wegame.capi.application.controller;

import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.application.BaseTest;
import com.wegame.capi.biz.module.game.MariaMakilingGame;
import com.wegame.capi.biz.module.probabilistic.MariaMakilingProbabilisticStrategy;
import com.wegame.capi.common.enums.GameTypeEnum;
import com.wegame.capi.infrastructure.config.nacos.NacosMariaMakilingConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class GameTest extends BaseTest {

    @Autowired
    MariaMakilingGame mariaMakilingGame;
    @Autowired
    NacosMariaMakilingConfig nacosMariaMakilingConfig;

    @Test
    public void test_randomOneSymbol() {
        int[][] arr = {{1,2,2},{3,4,4},{5,6,6}};

        System.out.println(FastJsonUtils.toJSONString(arr));


        for (int index = 1; index <= 5; index++) {
            Map<String, AtomicInteger> map = new HashMap<>();
            for (int i = 0; i < 100000000; i++) {
                String symbolId = mariaMakilingGame.randomOneSymbol(GameTypeEnum.MARIA_MAKILING, index);
                if (StringUtils.isNotBlank(symbolId)) {
                    if (map.containsKey(symbolId)) {
                        AtomicInteger atomicInteger = map.get(symbolId);
                        atomicInteger.incrementAndGet();
                        map.put(symbolId, atomicInteger);
                    } else {
                        AtomicInteger atomicInteger = new AtomicInteger(0);
                        atomicInteger.incrementAndGet();
                        map.put(symbolId, atomicInteger);
                    }
                } else {
                    log.error("symbolDo is null");
                }
            }
            map.entrySet().forEach(item -> {
                log.info("symbolId: {}, count: {}", item.getKey(), item.getValue());
            });
            int sum = map.values().stream().map(item -> item.get()).reduce(0, Integer::sum);
            log.info("index: {}, sum: {}", index, sum);
            System.out.println();
            System.out.println();
        }
    }
}