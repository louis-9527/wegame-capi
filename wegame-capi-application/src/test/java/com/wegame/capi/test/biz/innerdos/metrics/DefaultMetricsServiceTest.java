package com.wegame.capi.test.biz.innerdos.metrics;

import com.wegame.capi.biz.service.metrics.DefaultMetricsService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.Assert.*;

@Slf4j
public class DefaultMetricsServiceTest extends BaseTest {

    @Autowired
    private DefaultMetricsService defaultMetricsService;

    @Test
    public void metrics_normal_test() {
        // exInfo 这里代表是否有免费轮
        boolean metricsFlag = defaultMetricsService.metrics(MetricsManagerTestMocker.betRecordDo_normal(GameTypeEnum.IBONG_ADARNA), MetricsTypeEnum.IBONG_ADARNA, true);
        // 等一会儿，让定时任务执行
        LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(120));
        assertTrue(metricsFlag);
    }
    // 大盘采集并发请看下测试


}
