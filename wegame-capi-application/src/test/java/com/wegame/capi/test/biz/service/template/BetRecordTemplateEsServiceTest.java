package com.wegame.capi.test.biz.service.template;

import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
import com.wegame.capi.biz.domain.biz.template.malakas.MalakasBetRecordTemplateEsDo;
import com.wegame.capi.biz.service.template.es.BetRecordTemplateEsService;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.TemplateTypeEnum;
import com.wegame.capi.common.utils.JsonGzipUtil;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class BetRecordTemplateEsServiceTest extends BaseTest {

    @Autowired(required = false)
    BetRecordTemplateEsService<MalakasBetRecordTemplateEsDo> betRecordTemplateEsService;

    private String readJson() throws IOException {
        String path = "/test/big-bet-record.json";
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("read big-bet-record.json.json file err！！！");
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int n;
            while ((n = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);

        }
    }

    @Test
    public void test_add() throws IOException {
        MalakasBetRecordTemplateEsDo po = new MalakasBetRecordTemplateEsDo();
        po.setId(1000L);
        po.setBetType(BetTypeEnum.NORMAL_BET.getCode());
        po.setBetSubType(BetSubTypeEnum.NORMAL_BET.getCode());
        po.setTemplateType(TemplateTypeEnum.MALAKAS.name());
        po.setHash("42314234");
        po.setMultiple(12.3);
        po.setGameId(GameTypeEnum.MALAKAS.getId());
        po.setBytes(JsonGzipUtil.compress(readJson()));
        boolean isSuc = betRecordTemplateEsService.add(TemplateTypeEnum.MALAKAS, po);
        log.info("test_add ret: {}", isSuc);
        Assert.assertNotNull(isSuc);
    }

    @Test
    public void test_findById() {
        MalakasBetRecordTemplateEsDo templateDo = betRecordTemplateEsService.findById(TemplateTypeEnum.MALAKAS, 1963141355374907392L, MalakasBetRecordTemplateEsDo.class);
        log.info("test_findById ret: {}", FastJsonUtils.toJSONString(templateDo));
        log.info("test_findById ret bytes json: {}", JsonGzipUtil.decompress(templateDo.getBytes()));
        List<GameRecordTemplateDo> list = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(templateDo.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
        });
        list.forEach(item -> {
            log.info("test_findById item: {}", item);
        });
        Assert.assertNotNull(templateDo);
    }

    @Test
    public void test_listIdByMultipleRange() {
        List<Long> idList = betRecordTemplateEsService.listIdByMultipleRange(TemplateTypeEnum.MALAKAS, BetTypeEnum.BUY_BET, BigDecimal.ZERO, new BigDecimal("17.5"), MalakasBetRecordTemplateEsDo.class);
        log.info("test_listIdByMultipleRange ret: {}", FastJsonUtils.toJSONString(idList));

        idList.forEach(item -> {
            MalakasBetRecordTemplateEsDo templateDo = betRecordTemplateEsService.findById(TemplateTypeEnum.MALAKAS, item, MalakasBetRecordTemplateEsDo.class);
            Assert.assertNotNull(templateDo);
        });

        Lists.partition(idList, 1000).forEach(item -> {
            List<MalakasBetRecordTemplateEsDo> list = betRecordTemplateEsService.listByIdList(TemplateTypeEnum.MALAKAS, item, MalakasBetRecordTemplateEsDo.class);
            Assert.assertNotNull(list);
        });
        Assert.assertNotNull(idList);
    }
}
