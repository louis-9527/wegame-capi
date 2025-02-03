package com.wegame.capi.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class PulscarConfig {

    @Value("${pulscar.url}")
    private String pulsarUrl;

    @Bean
    public PulsarClient pulsarClient() throws Exception {
        return PulsarClient.builder()
                .serviceUrl(pulsarUrl)
                .build();
    }
}
