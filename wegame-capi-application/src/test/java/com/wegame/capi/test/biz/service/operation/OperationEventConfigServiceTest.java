package com.wegame.capi.test.biz.service.operation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.service.operation.OperationEventConfigService;
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
    public void addConfig() {
        OperationEventConfigPo po = new OperationEventConfigPo();
        po.setId(200L);
        po.setGameId("zhumeng001");
        po.setMultiple(4.0);
        po.setBetType("BUY_BET");
        Long id = operationEventConfigService.addConfig(po, OperationEventTypeEnum.MARIA_MAKILING);
        Assert.assertNotNull(id);
    }

    @Test
    public void addConfigRecord() {
        String dd = "{\"index\": 0, \"freeFlag\": false, \"symbolArr\": [[{\"_id\": \"1003\", \"name\": \"Symbol1003\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1004\", \"name\": \"Symbol1004\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2004\", \"name\": \"Symbol2004\", \"symbolType\": \"COMMON\"}], [{\"_id\": \"2001\", \"name\": \"Symbol2001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1002\", \"name\": \"Symbol1002\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2001\", \"name\": \"Symbol2001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1005\", \"name\": \"Symbol1005\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2002\", \"name\": \"Symbol2002\", \"symbolType\": \"COMMON\"}], [{\"_id\": \"1003\", \"name\": \"Symbol1003\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2002\", \"name\": \"Symbol2002\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}]]}";
        WheelDo<SymbolDo> wheelDo = JSON.parseObject(dd, new TypeReference<WheelDo<SymbolDo>>() {
        });
        OperationEventConfigRecordPo recordPo = new OperationEventConfigRecordPo();
        recordPo.setId(10002L);
        recordPo.setBetRecordId(100L);
        recordPo.setIndex(0);
        recordPo.setBatchId(0L);
        recordPo.setWheel(wheelDo);
        recordPo.setParentId(0L);
        List<Long> ks = new ArrayList<>();
        ks.add(1002L);
        recordPo.setGameRecordIds(ks);
        recordPo.setFreeWheelFlag(false);
        operationEventConfigService.addConfigRecord(recordPo, OperationEventTypeEnum.MARIA_MAKILING);
    }
}
