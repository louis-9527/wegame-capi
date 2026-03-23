package com.wegame.capi.api.domain.req.redirect;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

// 测试主类
public class JacksonSerializeTest {
    public static void main(String[] args) throws Exception {
        // 1. 创建 Jackson 核心序列化工具（和 RestTemplate 内部使用的是同一个类）
        ObjectMapper objectMapper = new ObjectMapper();

        // 2. 构造测试用的 PromotionForwardReq 对象
        PromotionForwardReq req = new PromotionForwardReq();
        // 设置父类 BaseReq 的字段
        req.setPId("P123456");
        req.setPlayerName("test_player_001");
        // 设置 data 字段（包含多个键值对）
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("gameId", "G7890");
        dataMap.put("redirectUrl", "https://game.example.com/forward");
        dataMap.put("timestamp", 1711888888888L);
        dataMap.put("isTest", true);
        req.setData(dataMap);

        // 3. 执行序列化（模拟 RestTemplate 内部的序列化逻辑）
        String jsonResult = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(req); // 格式化输出，便于查看

        // 4. 打印结果
        System.out.println("Jackson 序列化结果（@JsonUnwrapped 生效）：");
        System.out.println(jsonResult);
    }
}
