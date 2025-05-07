//package com.wegame.capi.test.api;
//
//import com.wegame.base.utils.FastJsonUtils;
//import com.wegame.capi.api.WeGameGoApi;
//import com.wegame.capi.api.domain.req.go.MinMaxRandomReq;
//import com.wegame.capi.api.domain.req.go.MultiMinMaxRandomReq;
//import com.wegame.capi.biz.service.RandomService;
//import com.wegame.capi.test.BaseTest;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
//@Slf4j
//public class WeGameGoApiTest extends BaseTest {
//    @Autowired
//    WeGameGoApi weGameGoApi;
//    @Autowired
//    RandomService randomService;
//
//    @Test
//    public void test_minMaxRandom() {
//        long startMill = System.currentTimeMillis();
//
//        for (int i = 0; i < 1000; i++) {
//
//            Integer random = weGameGoApi.minMaxRandom(MinMaxRandomReq.builder().min(1).max(10000).build());
//            log.info("test_minMaxRandom:{}", random);
//        }
//        // 1000次大概1200毫秒
//        long endMill = System.currentTimeMillis();
//        log.info("test_minMaxRandom,耗时:{}", endMill - startMill);
//    }
//
//
//    @Test
//    public void test_multiMinMaxRandom() {
//        long startMill = System.currentTimeMillis();
//
//        for (int i = 0; i < 10; i++) {
//            List<Integer> minMaxRandomRes = weGameGoApi.multiMinMaxRandom(MultiMinMaxRandomReq.builder().count(100).min(1).max(10000).build());
//            log.info("test_multiMinMaxRandom:{}", FastJsonUtils.toJSONString(minMaxRandomRes));
//        }
//        // 1000次大概230毫秒
//        long endMill = System.currentTimeMillis();
//        log.info("test_multiMinMaxRandom,耗时:{}", endMill - startMill);
//    }
//
//    @Test
//    public void test_randomService() {
//        long startMill = System.currentTimeMillis();
//
//        for (int i = 0; i < 100; i++) {
//            int random = randomService.randomInt(1, 10000);
//            log.info("test_randomService:{}", random);
//        }
//        long endMill = System.currentTimeMillis();
//        log.info("test_randomService,耗时:{}", endMill - startMill);
//    }
//
//}
