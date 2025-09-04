package com.wegame.capi.test.biz.service;

import com.wegame.capi.biz.service.template.es.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.dao.repo.domain.MalakasBetRecordTemplatePo;
import com.wegame.capi.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BetRecordTemplateEsServiceTest extends BaseTest {

    @Autowired
    BetRecordTemplateEsService betRecordTemplateEsService;

    @Test
    public void test_add() {
        MalakasBetRecordTemplatePo po = new MalakasBetRecordTemplatePo();
        po.setId(1000L);
        po.setBetType(BetTypeEnum.NORMAL_BET.getCode());
        po.setBetSubType(BetSubTypeEnum.NORMAL_BET.getCode());
        po.setHash("42314234");
        po.setMultiple(12.3);
        po.setGameId(GameTypeEnum.MALAKAS.getId());
        long id = betRecordTemplateEsService.add(po, TemplateTypeEnum.MALAKAS);
        Assert.assertNotNull(id);
    }

}
