//package com.wegame.capi.test.biz.module.atlantis;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import com.wegame.base.utils.FastJsonUtils;
//import com.wegame.capi.biz.domain.biz.operation.GameRecordTemplateDo;
//import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
//import com.wegame.capi.biz.domain.biz.template.atlantis.AtlantisBetRecordTemplateEsDo;
//import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
//import com.wegame.capi.common.utils.JsonGzipUtil;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//
//public class AtlantisTemplateTest {
//
//
//    private static final Logger log = LoggerFactory.getLogger(AtlantisTemplateTest.class);
//
//    @Test
//    public void templateTest() {
//        String result=" {\n" +
//                "\t\t\"avgMultiple\": 0,\n" +
//                "\t\t\"betSubType\": \"BUY_BET\",\n" +
//                "\t\t\"betType\": \"BUY_BET\",\n" +
//                "\t\t\"bytes\": \"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\",\n" +
//                "\t\t\"gameId\": \"zhumeng010\",\n" +
//                "\t\t\"hash\": \"-1393046637\",\n" +
//                "\t\t\"id\": 2045036625545256970,\n" +
//                "\t\t\"multiple\": 0.468,\n" +
//                "\t\t\"nextBetRecordId\": 0,\n" +
//                "\t\t\"tag\": \"\",\n" +
//                "\t\t\"templateType\": \"COMMON\"\n" +
//                "\t}";
//
//        AtlantisBetRecordTemplateEsDo template= FastJsonUtils.fromJSONString(result, AtlantisBetRecordTemplateEsDo.class);
//
//        List<GameRecordTemplateDo> gameRecordTemplateDos = FastJsonUtils.fromJSONString(JsonGzipUtil.decompress(template.getBytes()), new TypeReference<List<GameRecordTemplateDo>>() {
//        });
//        for (GameRecordTemplateDo templateDo : gameRecordTemplateDos) {
//
//
//            WheelDo<SymbolDo> symbolDoWheelDo = JSON.parseObject(JSON.toJSONString(templateDo.getWheel()), new TypeReference<WheelDo<SymbolDo>>() {
//            });
//            log.info("symbolDoWheelDo:{}",symbolDoWheelDo);
//
//        }
//
////        log.info("dos:{}", gameRecordTemplateDos);
//
//
//    }
//
//
//}
