package com.wegame.capi.infrastructure.config.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * Nacos配置: Mysterious游戏概率配置
 */
@Component
@ConfigurationProperties(prefix = "mysterious-probabilistic-config")
public class NacosMysteriousProbabilisticConfig {
    /**
     * key：元素id + "_" + 元素名称
     * value：List<Double> 概率列表
     */
    private Map<String, List<Double>> configMap = new HashMap<>();

    public Map<String, List<Double>> getConfigMap() {
        return configMap;
    }
}
