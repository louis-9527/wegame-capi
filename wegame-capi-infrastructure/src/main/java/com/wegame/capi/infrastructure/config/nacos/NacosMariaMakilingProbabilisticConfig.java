package com.wegame.capi.infrastructure.config.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Nacos配置: Mysterious游戏概率配置
 *  * todo: 配置正确性校验
 */
@Component
@ConfigurationProperties(prefix = "maria-makiling-probabilistic-config")
public class NacosMariaMakilingProbabilisticConfig {
    /**
     * nacos json配置，字段命名不可更改/字段命名不可更改/字段命名不可更改<br/><br/>
     *
     * key：元素id + "_" + 元素名称<br/>
     * value：List<Double> 概率列表<br/>
     */
    private Map<String, List<BigDecimal>> probabilisticConfigMap = new HashMap<>();

    public Map<String, List<BigDecimal>> getProbabilisticConfigMap() {
        return probabilisticConfigMap;
    }
}
