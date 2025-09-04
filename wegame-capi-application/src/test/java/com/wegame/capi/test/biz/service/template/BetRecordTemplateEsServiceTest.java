package com.wegame.capi.test.biz.service.template;

import com.wegame.capi.biz.service.template.es.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.biz.domain.biz.template.malakas.MalakasBetRecordTemplateEsDo;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BetRecordTemplateEsServiceTest extends BaseTest {

    @Autowired
    BetRecordTemplateEsService<MalakasBetRecordTemplateEsDo> betRecordTemplateEsService;

    @Test
    public void test_add() {
        MalakasBetRecordTemplateEsDo po = new MalakasBetRecordTemplateEsDo();
        po.setId(1000L);
        po.setBetType(BetTypeEnum.NORMAL_BET.getCode());
        po.setBetSubType(BetSubTypeEnum.NORMAL_BET.getCode());
        po.setTemplateType(TemplateTypeEnum.MALAKAS.name());
        po.setHash("42314234");
        po.setMultiple(12.3);
        po.setGameId(GameTypeEnum.MALAKAS.getId());
        boolean isSuc = betRecordTemplateEsService.add(TemplateTypeEnum.MALAKAS, po);
        log.info("test_add ret: {}", isSuc);
        Assert.assertNotNull(isSuc);
    }

    @Test
    public void test_findById() {
        MalakasBetRecordTemplateEsDo templateDo = betRecordTemplateEsService.findById(1000L, MalakasBetRecordTemplateEsDo.class);
        log.info("test_findById ret: {}", templateDo);
        Assert.assertNotNull(templateDo);
    }
}
