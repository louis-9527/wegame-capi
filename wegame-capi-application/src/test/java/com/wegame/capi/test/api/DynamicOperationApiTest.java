package com.wegame.capi.test.api;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wegame.capi.api.DynamicOperationApi;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsBetRecordReq;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsStatusReq;
import com.wegame.capi.api.domain.res.dynamicoperation.UpdateEventsStatusRes;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;

public class DynamicOperationApiTest extends BaseTest {
    @Autowired
    DynamicOperationApi dynamicOperationApi;

    @Test
    public void test_updateEventsStatus() {
        UpdateEventsStatusRes updateEventsStatusRes = dynamicOperationApi.updateEventsStatus(buildUpdateEventsStatusReq());
        // todo 断言
        System.out.println(JSON.toJSONString(updateEventsStatusRes));
    }

    private static UpdateEventsStatusReq buildUpdateEventsStatusReq() {
        UpdateEventsStatusReq req = new UpdateEventsStatusReq();
        req.setEventId("1000");
        req.setRequestTime(new Date());
        req.setExecuteBouns(BigDecimal.valueOf(1000));
        req.setExecuteTime(new Date());
        req.setEventStatus(1);
        UpdateEventsBetRecordReq recordReq = new UpdateEventsBetRecordReq();
        recordReq.setBetNo("67875fb3bdc643552706aed6");
        recordReq.setBetAmount(BigDecimal.valueOf(200));
        recordReq.setOrderTime(new Date());
        recordReq.setSettlementTime(new Date());
        recordReq.setPayoutAmount(BigDecimal.valueOf(1000));
        req.setBetNos(Lists.newArrayList("67875fb3bdc643552706aed6"));
        req.setBetRecords(Lists.newArrayList(recordReq));
        return req;
    }
}
