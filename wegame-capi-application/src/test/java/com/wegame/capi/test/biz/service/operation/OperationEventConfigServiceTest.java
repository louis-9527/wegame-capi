package com.wegame.capi.test.biz.service.operation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.biz.service.operation.OperationEventConfigService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.OperationEventConfigPo;
import com.wegame.capi.dao.mapper.domain.business.OperationEventConfigRecordPo;
import com.wegame.capi.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class OperationEventConfigServiceTest extends BaseTest {

    @Autowired
    OperationEventConfigService operationEventConfigService;

    @Test
    public void test_add() {
        OperationEventConfigDo configDo = new OperationEventConfigDo();
        configDo.setId(200L);
        configDo.setGameId("zhumeng001");
        configDo.setMultiple(4.0);
        configDo.setBetType(BetTypeEnum.BUY_BET);
        Long id = operationEventConfigService.add(configDo, OperationEventTypeEnum.MARIA_MAKILING);
        Assert.assertNotNull(id);
    }
}
