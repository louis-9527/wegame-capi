//package com.wegame.capi.infrastructure.config.nacos;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Nacos配置: Mysterious游戏赔率配置
// * <p>
// * 配置正确性校验
// */
//@Component
//@ConfigurationProperties(prefix = "maria-makiling-config")
//public class NacosMariaMakilingOddsConfig {
//    /**
//     * 正常玩/非购买免费玩赔率表
//     *
//     * nacos json配置，字段命名不可更改/字段命名不可更改/字段命名不可更改<br/><br/>
//     * <p>
//     * key：元素id<br/>
//     * value：<key, val>: <br/>
//     * key赔率类型（THREE：三连，FOUR：四连，FIVE：五连）<br/>
//     * val赔率<br/>
//     */
//    private Map<String, Map<String, BigDecimal>> oddsConfigMap = new HashMap<>();
//
//
//    /**
//     * 购买免费玩赔率表
//     *
//     * nacos json配置，字段命名不可更改/字段命名不可更改/字段命名不可更改<br/><br/>
//     * <p>
//     * key：元素id<br/>
//     * value：<key, val>: <br/>
//     * key赔率类型（THREE：三连，FOUR：四连，FIVE：五连）<br/>
//     * val赔率<br/>
//     */
//    private Map<String, Map<String, BigDecimal>> buyOddsConfigMap = new HashMap<>();
//
//    public Map<String, Map<String, BigDecimal>> getOddsConfigMap() {
//        return oddsConfigMap;
//    }
//
//    public Map<String, Map<String, BigDecimal>> getBuyOddsConfigMap() {
//        return buyOddsConfigMap;
//    }
//
//
//}
