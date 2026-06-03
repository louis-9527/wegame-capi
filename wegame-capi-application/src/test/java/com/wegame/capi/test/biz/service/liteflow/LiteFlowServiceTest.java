package com.wegame.capi.test.biz.service.liteflow;

import com.wegame.capi.biz.playerintervention.liteflow.execute.LiteFlowService;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class LiteFlowServiceTest extends BaseTest {
    @Autowired
    private LiteFlowService liteFlowService;

    @Test
    public void test() {
        liteFlowService.test();
    }


}
