package com.wegame.capi.test.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.service.DynamicOperationEventConfigService;
import com.wegame.capi.client.enums.OperationEventGameEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigPo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigRecordPo;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class DynamicOperationEventConfigServiceTest  extends BaseTest {

    @Autowired
    private DynamicOperationEventConfigService dynamicOperationEventConfigService;

    @Test
    public void addConfig(){
        DynamicOperationConfigPo operationConfigPo = new DynamicOperationConfigPo();
        operationConfigPo.setId(200L);
        operationConfigPo.setGameId("zhumeng001");
        operationConfigPo.setMultiple(4.0);
        operationConfigPo.setBetType("BUY_BET");
        dynamicOperationEventConfigService.addConfig(operationConfigPo, OperationEventGameEnum.MARIA_MAKILING_DYNAMIC);
    }
    @Test
    public void addConfigRecord(){
        String dd = "{\"index\": 0, \"freeFlag\": false, \"symbolArr\": [[{\"_id\": \"1003\", \"name\": \"Symbol1003\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1004\", \"name\": \"Symbol1004\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2004\", \"name\": \"Symbol2004\", \"symbolType\": \"COMMON\"}], [{\"_id\": \"2001\", \"name\": \"Symbol2001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1002\", \"name\": \"Symbol1002\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2001\", \"name\": \"Symbol2001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1005\", \"name\": \"Symbol1005\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2002\", \"name\": \"Symbol2002\", \"symbolType\": \"COMMON\"}], [{\"_id\": \"1003\", \"name\": \"Symbol1003\", \"symbolType\": \"COMMON\"}, {\"_id\": \"2002\", \"name\": \"Symbol2002\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}, {\"_id\": \"1001\", \"name\": \"Symbol1001\", \"symbolType\": \"COMMON\"}]]}";
        WheelDo<SymbolDo> wheelDo = JSON.parseObject(dd, new TypeReference<WheelDo<SymbolDo>>() {
        });
        DynamicOperationConfigRecordPo recordPo = new DynamicOperationConfigRecordPo();
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
        dynamicOperationEventConfigService.addConfigRecord(recordPo, OperationEventGameEnum.MARIA_MAKILING_DYNAMIC);
    }
}
