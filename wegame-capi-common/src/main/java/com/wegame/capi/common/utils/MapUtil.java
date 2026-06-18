package com.wegame.capi.common.utils;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    /**
     * 适配FastJSON2自定义序列化的 对象转Map
     * 完全遵循 @JSONType 自定义序列化器规则
     */
    public static <T> Map<String, Object> objectToMap(T obj) {
        if (obj == null) {
            return new HashMap<>();
        }
        // 先序列化为合规JSON，再反序列为Map，完美适配自定义序列化逻辑
        String jsonStr = JSON.toJSONString(obj);
        return JSON.parseObject(jsonStr);
    }

}
