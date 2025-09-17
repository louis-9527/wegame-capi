package com.wegame.capi.test.biz.innerdos.metrics;

import com.wegame.capi.biz.manager.MetricsManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class MetricsManagerTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(MetricsManagerTest.class);
    @Autowired
    private MetricsManager metricsManager;

    @Test
    public void metrics_canNotMetrics_test(){
        boolean resultFlag = metricsManager.metrics(MetricsManagerTestMocker.betRecordDo_cannotMetrics(GameTypeEnum.IBONG_ADARNA),
                MetricsTypeEnum.IBONG_ADARNA, MetricsManagerTestMocker.success_betResult, true);
        assertFalse(resultFlag);
    }


}
