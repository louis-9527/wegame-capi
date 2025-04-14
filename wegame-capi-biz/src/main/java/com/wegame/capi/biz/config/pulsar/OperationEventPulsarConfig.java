package com.wegame.capi.biz.config.pulsar;

import com.wegame.capi.biz.listener.AbsOperationEventPulsarListener;
import com.wegame.capi.biz.listener.MariamakilingOperationEventPulsarListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.wegame.capi.common.Constants.MARIA_MAKILING_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 干预事件Pulsar配置
 */
@Slf4j
@Configuration
public class OperationEventPulsarConfig {
    private static final String operationEventPulsarClient = "operationEventPulsarClient";
    private static final String mariaMakilingOperationEventConsumer = "mariaMakilingOperationEventConsumer";
    // todo fix
    @Value("${dynamic.operation.pulsar_url}")
    String dynamicOperationTopicUrl;
    @Value("${dynamic.operation.pulsar_topic}")
    String dynamicOperationTopic;
    @Value("${dynamic.operation.pulsar_group.maria_makiling}")
    String dynamicOperationGroupMariaMakiling;
    @Value("${dynamic.operation.pulsar.auth.issuer_url}")
    String dynamicOperationAuthIssuerUrl;
    @Value("${dynamic.operation.pulsar.auth.credentials_file_path}")
    String dynamicOperationAuthCredentialsFilePath;
    @Value("${dynamic.operation.pulsar.auth.audience}")
    String dynamicOperationAuthAudience;

    @Bean(name =operationEventPulsarClient, destroyMethod = "close")
    public PulsarClient operationEventPulsarClient() throws Exception {
        if(StringUtils.isEmpty(dynamicOperationAuthIssuerUrl)){
            return PulsarClient.builder()
                    .serviceUrl(dynamicOperationTopicUrl)
                    .keepAliveInterval(30, TimeUnit.SECONDS)
                    .build();
        }

        Authentication authentication = AuthenticationFactoryOAuth2.clientCredentials(
                new URL(dynamicOperationAuthIssuerUrl),
                Objects.requireNonNull(getClass().getResource(dynamicOperationAuthCredentialsFilePath)),
                dynamicOperationAuthAudience
        );
        return PulsarClient.builder()
                .serviceUrl(dynamicOperationTopicUrl)
                .keepAliveInterval(30, TimeUnit.SECONDS)
                .authentication(authentication)
                .build();
    }



    @Bean(name = mariaMakilingOperationEventConsumer, destroyMethod = "close")
    @ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
    public Consumer<byte[]> mariaMakilingOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                                  @Autowired MariamakilingOperationEventPulsarListener mariamakilingOperationEventPulsarListener) throws PulsarClientException {
        String deadLetterTopic = dynamicOperationTopic;
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(deadLetterTopic)
                .subscriptionName(dynamicOperationGroupMariaMakiling)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mariamakilingOperationEventPulsarListener)
                .subscribe();
    }
}
