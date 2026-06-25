package com.wegame.capi.biz.config.pulsar.operation;

import com.wegame.capi.biz.listener.operation.*;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.wegame.capi.biz.config.pulsar.operation.PulsarConfigHelper.buildSubscriptionName;
import static com.wegame.capi.common.Constants.*;

/**
 * 干预事件Pulsar配置
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = {DYNAMIC_OPERATION_SWITCH, DYNAMIC_INTERNAL_OPERATION_SWITCH}, havingValue = TRUE_VAL)
public class OperationEventInternalPulsarConfig {
    private static final String key = "cd_Internal";
    @Value("${spring.application.name}")
    String appName;
    @Value("${spring.application.env}")
    String env;
    @Value("${dynamic.internal.operation.pulsar_url}")
    String dynamicOperationTopicUrl;
    @Value("${dynamic.internal.operation.pulsar_topic}")
    String dynamicOperationTopic;
    @Value("${dynamic.internal.operation.pulsar.auth.issuer_url}")
    String dynamicOperationAuthIssuerUrl;
    @Value("${dynamic.internal.operation.pulsar.auth.credentials_file_path}")
    String dynamicOperationAuthCredentialsFilePath;
    @Value("${dynamic.internal.operation.pulsar.auth.audience}")
    String dynamicOperationAuthAudience;


    @Bean(name = "operationEventInternalPulsarClient", destroyMethod = COMMON_CLOSE_METHOD)
    public PulsarClient operationEventInternalPulsarClient() throws Exception {
        log.info(BUILD_BEAN_FORMAT, "operationEventInternalPulsarClient");
        if (StringUtils.isEmpty(dynamicOperationAuthIssuerUrl)) {
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


    @Bean(name = "mariaMakilingOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MariamakilingOperationEventPulsarListener.class)
    public Consumer<byte[]> mariaMakilingOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                        @Autowired MariamakilingOperationEventPulsarListener mariamakilingOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "mariaMakilingOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.MARIA_MAKILING))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mariamakilingOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "malakasOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MalakasOperationEventPulsarListener.class)
    public Consumer<byte[]> malakasOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                  @Autowired MalakasOperationEventPulsarListener malakasOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "malakasOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.MALAKAS))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(malakasOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "ibongOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(IbongOperationEventPulsarListener.class)
    public Consumer<byte[]> ibongOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                @Autowired IbongOperationEventPulsarListener ibongOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "ibongOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.IBONG_ADARNA))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(ibongOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "bakunawaOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = BAKUNAWA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(BakunawaOperationEventPulsarListener.class)
    public Consumer<byte[]> bakunawaOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                   @Autowired BakunawaOperationEventPulsarListener bakunawaOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "bakunawaOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.BAKUNAWA))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(bakunawaOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "lamangOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(LamangOperationEventPulsarListener.class)
    public Consumer<byte[]> lamangOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                 @Autowired LamangOperationEventPulsarListener lamangOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "lamangOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.LAM_ANG))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(lamangOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "diwataMayariOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(DiwataMayariOperationEventPulsarListener.class)
    public Consumer<byte[]> diwataMayariOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                       @Autowired DiwataMayariOperationEventPulsarListener diwataMayariOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "diwataMayariOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.DIWATA_MAYARI))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(diwataMayariOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "kapreOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = KAPRE_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(KapreOperationEventPulsarListener.class)
    public Consumer<byte[]> kapreOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                @Autowired KapreOperationEventPulsarListener kapreOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "kapreOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.KAPRE))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(kapreOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "mangkukulamOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MangkukulamOperationEventPulsarListener.class)
    public Consumer<byte[]> mangkukulamOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                      @Autowired MangkukulamOperationEventPulsarListener mangkukulamOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "mangkukulamOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.MANGKUKULAM))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mangkukulamOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "sirenaOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = SIRENA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(SirenaOperationEventPulsarListener.class)
    public Consumer<byte[]> sirenaOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                 @Autowired SirenaOperationEventPulsarListener sirenaOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "sirenaOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.SIRENA))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(sirenaOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "atlantisOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = ATLANTIS_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(AtlantisOperationEventPulsarListener.class)
    public Consumer<byte[]> atlantisOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                   @Autowired AtlantisOperationEventPulsarListener atlantisOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "atlantisOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.ATLANTIS))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(atlantisOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "bayanihanOperationEventInternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = BAYANIHAN_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(BayanihanOperationEventPulsarListener.class)
    public Consumer<byte[]> bayanihanOperationEventInternalConsumer(@Autowired PulsarClient operationEventInternalPulsarClient,
                                                                    @Autowired BayanihanOperationEventPulsarListener bayanihanOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "bayanihanOperationEventInternalConsumer");
        return operationEventInternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.BAYANIHAN))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(bayanihanOperationEventPulsarListener)
                .subscribe();
    }

}
