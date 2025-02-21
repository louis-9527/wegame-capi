package com.wegame.capi.test.biz.service;

import com.wegame.capi.biz.service.DynamicOperationEventConfigService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DynamicOperationEventConfigServiceTest extends BaseTest {
    @Autowired
    private DynamicOperationEventConfigService dynamicOperationEventConfigService;

    @Test
    public void add(){
        dynamicOperationEventConfigService.add(GameTypeEnum.MARIA_MAKILING);
    }
}
