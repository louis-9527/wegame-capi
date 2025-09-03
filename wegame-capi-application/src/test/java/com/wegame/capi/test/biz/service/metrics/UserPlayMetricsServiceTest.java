package com.wegame.capi.test.biz.service.metrics;

import com.wegame.capi.biz.service.metrics.UserPlayMetricsService;
import com.wegame.capi.client.enums.MetricsTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class UserPlayMetricsServiceTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(UserPlayMetricsServiceTest.class);
    @Autowired
    private UserPlayMetricsService userPlayMetricsService;

    @Test
    public void test_flush_normal(){

        boolean flag = userPlayMetricsService.flush("1", "1", "1", MetricsTypeEnum.IBONG_ADARNA);
        log.info("test_flush_normal");

    }


}
