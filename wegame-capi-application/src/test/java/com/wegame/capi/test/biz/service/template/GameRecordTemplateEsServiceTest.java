package com.wegame.capi.test.biz.service.template;

import com.wegame.capi.biz.domain.biz.template.malakas.MalakasBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.template.malakas.MalakasGameRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.template.malakas.MalakasGameRecordTemplateListEsDo;
import com.wegame.capi.biz.service.template.es.BetRecordTemplateEsService;
import com.wegame.capi.biz.service.template.es.GameRecordTemplateEsService;
import com.wegame.capi.client.enums.TemplateTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

@Slf4j
public class GameRecordTemplateEsServiceTest extends BaseTest {

    @Autowired
    BetRecordTemplateEsService<MalakasBetRecordTemplateEsDo> betRecordTemplateEsService;
    @Autowired
    GameRecordTemplateEsService<MalakasGameRecordTemplateListEsDo> gameRecordTemplateEsService;

    @Test
    public void test_add() {
        MalakasGameRecordTemplateEsDo x = new MalakasGameRecordTemplateEsDo();
        x.setId(1000L);
        x.setBetRecordId(12345L);
        x.setIndex(1);
        MalakasGameRecordTemplateEsDo y = new MalakasGameRecordTemplateEsDo();
        x.setId(1001L);
        x.setBetRecordId(123456L);
        x.setIndex(2);
        MalakasGameRecordTemplateListEsDo listEsDo = MalakasGameRecordTemplateListEsDo.builder()
                .betRecordId(1000L)
                .list(Arrays.asList(x, y))
                .build();
        boolean isSuc = gameRecordTemplateEsService.add(TemplateTypeEnum.MALAKAS, listEsDo);
        Assert.assertNotNull(isSuc);
    }

    @Test
    public void test_findById() {
        MalakasGameRecordTemplateListEsDo listEsDo = gameRecordTemplateEsService.findById(1000L, MalakasGameRecordTemplateListEsDo.class);
        log.info("test_findById ret: {}", listEsDo);
        Assert.assertNotNull(listEsDo);
    }
}
