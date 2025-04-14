package com.wegame.capi.test.biz.manager;

import com.wegame.capi.biz.manager.operation.MariaMakilingOperationEventManager;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class MariaMakilingDynamicOperationManagerTest extends BaseTest {

    @Autowired
    @Qualifier("mariaMakilingOperationEventManager")
    MariaMakilingOperationEventManager mariaMakilingOperationEventManager;


    @Test
    public void test_asyncReportEvent() {
        mariaMakilingOperationEventManager.asyncReportEvent(1899046143636467712L,null);
    }
}
