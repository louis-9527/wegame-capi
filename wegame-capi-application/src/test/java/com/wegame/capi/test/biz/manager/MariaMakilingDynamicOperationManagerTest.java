package com.wegame.capi.test.biz.manager;

import com.wegame.capi.biz.manager.MariaMakilingDynamicOperationManager;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MariaMakilingDynamicOperationManagerTest extends BaseTest {
    @Autowired
    private MariaMakilingDynamicOperationManager mariaMakilingDynamicOperationManager;


    @Test
    public void asyncReportEvent(){
        mariaMakilingDynamicOperationManager.asyncReportEvent(1899046143636467712L);
    }
}
