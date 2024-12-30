package com.wegame.capi.infrastructure.config.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Nacos配置: Mysterious游戏概率配置
 *  * todo: 配置正确性校验
 */
@Component
@ConfigurationProperties(prefix = "mysterious-probabilistic-config")
public class NacosMysteriousProbabilisticConfig {
    /**
     * nacos json配置，字段命名不可更改/字段命名不可更改/字段命名不可更改<br/><br/>
     *
     * key：元素id + "_" + 元素名称<br/>
     * value：List<Double> 概率列表<br/>
     */
    private Map<String, List<Double>> probabilisticConfigMap = new HashMap<>();

    public Map<String, List<Double>> getProbabilisticConfigMap() {
        return probabilisticConfigMap;
    }
}
