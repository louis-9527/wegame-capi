package com.wegame.capi.test.application.controller;

import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.application.controller.CommonController;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import com.wegame.capi.biz.service.BetService;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CommonControllerTest extends BaseTest {

    @Autowired
    CommonController commonController;
    @Test
    public void test_ping() {
        CommonResponse<String> commonResponse = commonController.ping();
        Assert.assertNotNull(commonResponse);
        Assert.assertNotNull(commonResponse.getData());
        Assert.assertTrue(StringUtils.equals(commonResponse.getData(), "pong"));
    }

}
