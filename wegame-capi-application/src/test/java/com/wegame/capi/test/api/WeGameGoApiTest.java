package com.wegame.capi.test.api;

import com.wegame.capi.api.WeGameGoApi;
import com.wegame.capi.api.domain.req.wegamego.MinMaxRandomReq;
import com.wegame.capi.api.domain.req.wegamego.MinMaxRandomRes;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class WeGameGoApiTest extends BaseTest {
    @Autowired
    WeGameGoApi weGameGoApi;
    @Autowired
    RandomService randomService;

    @Test
    public void test_random() {
        long startMill = System.currentTimeMillis();
//
//        for (int i = 0; i < 1000; i++) {
//
//            MinMaxRandomRes randomRes = weGameGoApi.random(MinMaxRandomReq.builder().min(1).max(10000).build());
//            log.info("test_random:{}", randomRes.getResult());
//        }
        MinMaxRandomRes randomRes = weGameGoApi.random(MinMaxRandomReq.builder().min(1).max(10000).build());
        log.info("test_random:{}", randomRes.getResult());
        // 1000次大概1200毫秒
        long endMill = System.currentTimeMillis();
        log.info("test_random,耗时:{}", endMill - startMill);
    }

    @Test
    public void test_randomService() {
        long startMill = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            int random = randomService.randomIntOld(1, 10000);
            log.info("test_randomService:{}", random);

        }
        long endMill = System.currentTimeMillis();
        log.info("test_randomService,耗时:{}", endMill - startMill);
    }

}
