package com.wegame.capi.test.biz.service.template;

import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.biz.domain.biz.template.lamang.LamangBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.GameRecordService;
import com.wegame.capi.biz.service.template.BetRecordTemplateService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BetRecordTemplateServiceTest extends BaseTest {
    @Autowired
    BetRecordTemplateService<LamangBetRecordTemplateEsDo> betRecordTemplateService;

    @Test
    public void test_randomTemplateWithNoCache(){
        String indexName = "lamang_bet_record_template";
        for (long i = 0; i < 100; i++) {
            LamangBetRecordTemplateEsDo esDo = betRecordTemplateService.randomTemplateWithNoCache(indexName, BetTypeEnum.NORMAL_BET, new BigDecimal(0), new BigDecimal(10), LamangBetRecordTemplateEsDo.class);
            log.info("esDo id: {}, {}", esDo.getId(), esDo);
        }

        for (long i = 0; i < 10; i++) {
            LamangBetRecordTemplateEsDo esDo = betRecordTemplateService.randomTemplateWithNoCache(indexName, BetTypeEnum.NORMAL_BET, new BigDecimal(-100), new BigDecimal(-1), LamangBetRecordTemplateEsDo.class);
            log.info("esDo: {}", esDo);
        }
    }
}
