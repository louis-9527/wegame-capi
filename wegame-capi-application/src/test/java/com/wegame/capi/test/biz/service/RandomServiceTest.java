package com.wegame.capi.test.biz.service;

import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RandomServiceTest extends BaseTest {

    @Autowired
    RandomService randomService;

    @BeforeAll
    public void beforeTest() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .build());
    }

    @Test
    public void test_randomInt() {
        try {
            for (int i = 1; i < 100; i++) {
                int randomNumber = randomService.randomInt(0, i);
                log.info("randomNumber={}", randomNumber);
                System.out.println(randomNumber);
            }
        } catch (Exception e) {
            log.error("test_randomInt err, errMsg: {}", e.getMessage(), e);
        }
    }
}
