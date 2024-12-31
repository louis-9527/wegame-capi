package com.wegame.capi.infrastructure.config.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Nacos配置: Mysterious游戏赔率配置
 * <p>
 * todo: 配置正确性校验
 */
@Component
@ConfigurationProperties(prefix = "maria-makiling-odds-config")
public class NacosMariaMakilingOddsConfig {
    /**
     * nacos json配置，字段命名不可更改/字段命名不可更改/字段命名不可更改<br/><br/>
     * <p>
     * key：元素id + "_" + 元素名称<br/>
     * value：<key, val>: <br/>
     * key赔率类型（THREE：三连，FOUR：四连，FIVE：五连）<br/>
     * val赔率<br/>
     */
    private Map<String, Map<String, Double>> oddsConfigMap = new HashMap<>();

    public Map<String, Map<String, Double>> getOddsConfigMap() {
        return oddsConfigMap;
    }

}
