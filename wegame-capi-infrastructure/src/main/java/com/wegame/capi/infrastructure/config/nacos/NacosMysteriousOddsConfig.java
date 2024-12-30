package com.wegame.capi.infrastructure.config.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Nacos配置: Mysterious游戏赔率配置
 *
 * todo: 配置正确性校验
 */
@Component
@ConfigurationProperties(prefix = "mysterious-odds-config")
public class NacosMysteriousOddsConfig {
    /**
     * key：元素id + "_" + 元素名称
     * value：<key, val>:
     * key赔率类型（THREE：三连，FOUR：四连，FIVE：五连）
     * val赔率
     */
    private Map<String, Map<String, Double>> oddsConfigMap = new HashMap<>();

    public Map<String, Map<String, Double>> getOddsConfigMap() {
        return oddsConfigMap;
    }

}
