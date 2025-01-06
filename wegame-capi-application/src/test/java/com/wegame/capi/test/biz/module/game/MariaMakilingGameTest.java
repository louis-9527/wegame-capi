package com.wegame.capi.test.biz.module.game;

import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.test.BaseTest;
import com.wegame.capi.biz.domain.biz.GamePlayDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.game.MariaMakilingGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wegame.capi.biz.util.RandomSymbolUtil.randomOneSymbol;

@Slf4j
public class MariaMakilingGameTest extends BaseTest {

    @Autowired
    MariaMakilingGame mariaMakilingGame;

    @Test
    public void test_randomOneSymbol_commonProbabilistic() {
        GamePlayDo playDo = GamePlayDo.builder()
                .gameType(GameTypeEnum.MARIA_MAKILING)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .build();
        int[][] arr = {{1, 2, 2}, {3, 4, 4}, {5, 6, 6}};
        System.out.println(FastJsonUtils.toJSONString(arr));
        for (int index = 1; index <= 5; index++) {
            Map<String, AtomicInteger> map = new HashMap<>();
            for (int i = 0; i < 100000000; i++) {
                String symbolId = randomOneSymbol(index, mariaMakilingGame.getProbabilisticConfig(playDo));
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

    @Test
    public void test_randomOneSymbol_freeProbabilistic() {
        GamePlayDo playDo = GamePlayDo.builder()
                .gameType(GameTypeEnum.MARIA_MAKILING)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .build();
        int[][] arr = {{1, 2, 2}, {3, 4, 4}, {5, 6, 6}};
        System.out.println(FastJsonUtils.toJSONString(arr));
        for (int index = 1; index <= 5; index++) {
            Map<String, AtomicInteger> map = new HashMap<>();
            for (int i = 0; i < 100000000; i++) {
                String symbolId = randomOneSymbol(index, mariaMakilingGame.getProbabilisticConfig(playDo));
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

    @Test
    public void test_generate() {
        int times = 1;
        //        for (int i = 0; i < times; i++) {
        //            GamePlayDo playDo = GamePlayDo.builder()
        //                    .gameType(GameTypeEnum.MARIA_MAKILING)
        //                    .betType(BetTypeEnum.NORMAL_BET)
        //                    .betSubType(BetSubTypeEnum.NORMAL_BET)
        //                    .betAmount(100L)
        //                    .build();
        //            List<WheelDo> wheelDos = mariaMakilingGame.generate(playDo);
        //            wheelDos.forEach(WheelDo::print);
        //        }
        //        for (int i = 0; i < times; i++) {
        //            GamePlayDo playDo = GamePlayDo.builder()
        //                    .gameType(GameTypeEnum.MARIA_MAKILING)
        //                    .betType(BetTypeEnum.FREE_SPINS_BET)
        //                    .betSubType(BetSubTypeEnum.NON_BUY_FREE_SPINS_BET)
        //                    .betAmount(100L)
        //                    .build();
        //            List<WheelDo> wheelDos = mariaMakilingGame.generate(playDo);
        //            wheelDos.forEach(WheelDo::print);
        //        }
        //        for (int i = 0; i < times; i++) {
        //            GamePlayDo playDo = GamePlayDo.builder()
        //                    .gameType(GameTypeEnum.MARIA_MAKILING)
        //                    .betType(BetTypeEnum.FREE_SPINS_BET)
        //                    .betSubType(BetSubTypeEnum.BUY_FREE_SPINS_BET)
        //                    .betAmount(100L)
        //                    .build();
        //            List<WheelDo> wheelDos = mariaMakilingGame.generate(playDo);
        //            wheelDos.forEach(WheelDo::print);
        //        }

        for (int i = 0; i < times; i++) {
            GamePlayDo playDo = GamePlayDo.builder()
                    .gameType(GameTypeEnum.MARIA_MAKILING)
                    .betType(BetTypeEnum.FREE_SPINS_BET)
                    .betSubType(BetSubTypeEnum.BUY_FREE_SPINS_BET)
                    .betAmount(100L)
                    .freeSpinsFlag(true)
                    .build();
            List<WheelDo> wheelDos = mariaMakilingGame.generate(playDo);
            wheelDos.forEach(WheelDo::print);
        }
    }

}