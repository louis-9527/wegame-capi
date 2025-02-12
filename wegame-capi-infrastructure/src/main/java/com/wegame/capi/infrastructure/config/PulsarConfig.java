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
public class PulsarConfig {

    @Value("${pulsar.service-url}")
    private String pulsarUrl;

//    @Value("${pulsar.listener-threads}")
//    private Integer listenerThreads;

    @Bean(destroyMethod = "close")
    public PulsarClient pulsarClient() throws Exception {
        return PulsarClient.builder()
                .serviceUrl(pulsarUrl)
//                .listenerThreads(listenerThreads)
                .build();
    }
}
