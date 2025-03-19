package com.wegame.capi.test.biz.manager;

import com.wegame.capi.biz.manager.MariaMakilingOperationEventManager;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MariaMakilingDynamicOperationManagerTest extends BaseTest {
    @Autowired
    MariaMakilingOperationEventManager mariaMakilingOperationEventManager;


    @Test
    public void asyncReportEvent(){
        mariaMakilingOperationEventManager.asyncReportEvent(1899046143636467712L);
    }
}
