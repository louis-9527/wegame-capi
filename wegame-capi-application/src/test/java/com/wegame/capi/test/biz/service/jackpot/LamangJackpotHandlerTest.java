package com.wegame.capi.test.biz.service.jackpot;

import com.alibaba.fastjson.JSON;
import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotDrawAndAwardDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeDo;
import com.wegame.capi.biz.module.base.jackpot.AbsJackpotHandler;
import com.wegame.capi.biz.module.base.jackpot.JackpotHandlerContext;
import com.wegame.capi.biz.module.base.jackpot.LamangJackpotHandler;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.UUID;

public class LamangJackpotHandlerTest extends BaseTest {
    @Autowired
    private JackpotHandlerContext jackpotHandlerContext;

    @Test
    public void testCalculateWinPrize(){
        Operator operator = Operator.builder()
                .adminId(1L)
                .userId(1L)
                .playerId("slottest01")
                .name("slottest01")
                .build();
        OperatorContext.setOperator(operator);

        MDC.put(ThreadMdcUtil.TRACE_ID, UUID.randomUUID().toString());
        JackpotDrawAndAwardDo build = JackpotDrawAndAwardDo.builder()
                .pId("50")
                .channelId("1050")
                .playerId("slottest01")
                .playerName("slottest01")
                .userId(1L)
                .betRecordId(1L)
                .betId("1")
                .betAmount(10000L)
                .gameCount(new HashMap<Long, Integer>() {{
                    put(1L, 5);
                }}).build();
        AbsJackpotHandler handler = jackpotHandlerContext.getHandler(JackpotTypeEnum.LAM_ANG);
        JackpotWinPrizeDo jackpotWinPrizeDo = handler.calculateWinPrize(build);
        System.out.printf("result:"+ JSON.toJSONString(jackpotWinPrizeDo));
    }
}
