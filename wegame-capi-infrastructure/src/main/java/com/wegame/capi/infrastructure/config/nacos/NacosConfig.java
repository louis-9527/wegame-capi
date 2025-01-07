package com.wegame.capi.infrastructure.config.nacos;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Nacos配置
 */
@Data
@RefreshScope
@Configuration
public class NacosConfig {

//    // 通用///////
//    /**
//     * 服务器限流流量(每秒/流量),默认为100
//     */
//    @Value("${sever.common.serverRateLimitPermitsPerSecond:100}")
//    Double serverRateLimitPermitsPerSecond;
//
//    // 开关类
//    @Value("${sever.common.switch.serverRateLimitSwitch:false}")
//    boolean serverRateLimitSwitch;
//    @Value("${sever.common.switch.mqConsumerLogSwitch:true}")
//    boolean mqConsumerLogSwitch;
//    @Value("${sever.common.switch.tmSendApiErrorMessageSwitch:false}")
//    boolean tmSendApiErrorMessageSwitch;


    @Value("${example.config.value:0}")
    String exampleConfigValue;
}
