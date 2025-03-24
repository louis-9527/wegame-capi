package com.wegame.capi.test.biz.service.operation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.service.operation.OperationEventConfigRecordService;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class OperationEventConfigRecordServiceTest extends BaseTest {

    @Autowired
    OperationEventConfigRecordService operationEventConfigRecordService;

    @Test
    public void test_add() {
        String dd = "{\"index\": 0, \"freeFlag\": false, \"symbolArr\": [[{\"_id\": \"1003\", \"name\": \"Symbol1003\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1004\", \"name\": \"Symbol1004\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2004\", \"name\": \"Symbol2004\", \"symbolType\": \"COMMON\"}], [{\"_id\": \"2001\", \"name\": \"Symbol2001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1002\", \"name\": \"Symbol1002\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2001\", \"name\": \"Symbol2001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1005\", \"name\": \"Symbol1005\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2002\", \"name\": \"Symbol2002\", \"symbolType\": \"COMMON\"}], [{\"_id\": \"1003\", \"name\": \"Symbol1003\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2002\", \"name\": \"Symbol2002\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}]]}";
        WheelDo<SymbolDo> wheelDo = JSON.parseObject(dd, new TypeReference<WheelDo<SymbolDo>>() {
        });
        OperationEventConfigRecordDo recordDo = new OperationEventConfigRecordDo();
        recordDo.setId(10002L);
        recordDo.setBetRecordId(100L);
        recordDo.setIndex(0);
        recordDo.setBatchId(0L);
        recordDo.setWheel(wheelDo);
        recordDo.setParentId(0L);
        List<Long> ks = new ArrayList<>();
        ks.add(1002L);
        recordDo.setGameRecordIds(ks);
        // recordDo.setFreeWheelFlag(false);
        Long id = operationEventConfigRecordService.add(recordDo, OperationEventTypeEnum.MARIA_MAKILING);
        Assert.assertNotNull(id);
    }
}
