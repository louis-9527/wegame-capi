package com.wegame.capi.infrastructure.config.nacos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Nacos配置: Mysterious游戏概率配置
 * * todo: 配置正确性校验
 */
@Component
@ConfigurationProperties(prefix = "maria-makiling-config")
public class NacosMariaMakilingConfig {

    private OddsConfig oddsConfig;
    private ProbabilisticConfig probabilisticConfig;

    public Map<String, Map<String, BigDecimal>> getOddsConfig() {
        return oddsConfig.getCommonOddsConfigMap();
    }

    public Map<String, List<BigDecimal>> getCommonProbabilisticConfig() {
        return probabilisticConfig.getCommonProbabilisticConfigMap();
    }

    public Map<String, List<BigDecimal>> getFreeProbabilisticConfig() {
        return probabilisticConfig.getFreeProbabilisticConfigMap();
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OddsConfig implements Serializable {
        /**
         * 赔率表
         * <p>
         * nacos json配置，字段命名不可更改/字段命名不可更改/字段命名不可更改<br/><br/>
         * <p>
         * key：元素id<br/>
         * value：<key, val>: <br/>
         * key赔率类型（THREE：三连，FOUR：四连，FIVE：五连）<br/>
         * val赔率<br/>
         */
        private Map<String, Map<String, BigDecimal>> commonOddsConfigMap;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProbabilisticConfig implements Serializable {
        /**
         * nacos json配置，字段命名不可更改/字段命名不可更改/字段命名不可更改<br/><br/>
         * <p>
         * key：元素id<br/>
         * value：List<Double> 概率列表<br/>
         */
        private Map<String, List<BigDecimal>> commonProbabilisticConfigMap;

        /**
         * nacos json配置，字段命名不可更改/字段命名不可更改/字段命名不可更改<br/><br/>
         * <p>
         * key：元素id<br/>
         * value：List<Double> 概率列表<br/>
         */
        private Map<String, List<BigDecimal>> freeProbabilisticConfigMap;
    }

}
