package com.wegame.capi.infrastructure.config.nacos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nacos配置: Mysterious游戏赔率配置
 */
@Component
@ConfigurationProperties(prefix = "mysterious-odds-config")
public class NacosMysteriousOddsConfig {
    /**
     * key：元素id + "_" + 元素名称
     * value：List<Double> 赔率列表
     */
    private Map<String, List<Odds>> oddsMap = new HashMap<>();

    public Map<String, List<Odds>> getOddsMap() {
        return oddsMap;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Odds implements Serializable {
        /**
         * 赔率类型：THREE：三连，FOUR：四连，FIVE：五连
         */
        private String type;

        /**
         * 赔率
         */
        private Double value;
    }
}
