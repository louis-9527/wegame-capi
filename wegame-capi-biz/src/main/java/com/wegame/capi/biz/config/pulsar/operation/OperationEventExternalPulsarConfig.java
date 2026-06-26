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
 * 干预事件Pulsar外部配置
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = {DYNAMIC_OPERATION_ENABLE, DYNAMIC_OPERATION_EXTERNAL_ENABLE}, havingValue = TRUE_VAL)
public class OperationEventExternalPulsarConfig {
    private static final String key = "cd_External";
    @Value("${spring.application.name}")
    String appName;
    @Value("${spring.application.env}")
    String env;
    @Value("${dynamic.operation.external.pulsar_url}")
    String dynamicOperationTopicUrl;
    @Value("${dynamic.operation.external.pulsar_topic}")
    String dynamicOperationTopic;
    @Value("${dynamic.operation.external.pulsar_auth.issuer_url}")
    String dynamicOperationAuthIssuerUrl;
    @Value("${dynamic.operation.external.pulsar_auth.credentials_file_path}")
    String dynamicOperationAuthCredentialsFilePath;
    @Value("${dynamic.operation.external.pulsar_auth.audience}")
    String dynamicOperationAuthAudience;

    @Bean(name = "operationEventExternalPulsarClient", destroyMethod = COMMON_CLOSE_METHOD)
    public PulsarClient operationEventExternalPulsarClient() throws Exception {
        log.info(BUILD_BEAN_FORMAT, "operationEventExternalPulsarClient");
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

    @Bean(name = "mariaMakilingOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MariamakilingOperationEventPulsarListener.class)
    public Consumer<byte[]> mariaMakilingOperationEventExternalConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                                        @Autowired MariamakilingOperationEventPulsarListener mariamakilingOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "mariaMakilingOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.MARIA_MAKILING))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mariamakilingOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "malakasOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MalakasOperationEventPulsarListener.class)
    public Consumer<byte[]> malakasOperationEventExternalConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                                  @Autowired MalakasOperationEventPulsarListener malakasOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "malakasOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.MALAKAS))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(malakasOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "ibongOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(IbongOperationEventPulsarListener.class)
    public Consumer<byte[]> ibongOperationEventExternalConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                                @Autowired IbongOperationEventPulsarListener ibongOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "ibongOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.IBONG_ADARNA))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(ibongOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = "lamangOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(LamangOperationEventPulsarListener.class)
    public Consumer<byte[]> lamangOperationEventExternalConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                                 @Autowired LamangOperationEventPulsarListener lamangOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "lamangOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.LAM_ANG))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(lamangOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = "bakunawaOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = BAKUNAWA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(BakunawaOperationEventPulsarListener.class)
    public Consumer<byte[]> bakunawaOperationEventExternalConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                                   @Autowired BakunawaOperationEventPulsarListener bakunawaOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "bakunawaOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.BAKUNAWA))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(bakunawaOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = "diwataMayariOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(DiwataMayariOperationEventPulsarListener.class)
    public Consumer<byte[]> diwataMayariOperationEventExternalConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                                       @Autowired DiwataMayariOperationEventPulsarListener diwataMayariOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "diwataMayariOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.DIWATA_MAYARI))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(diwataMayariOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = "kapreOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = KAPRE_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(KapreOperationEventPulsarListener.class)
    public Consumer<byte[]> kapreOperationEventExternalConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                                @Autowired KapreOperationEventPulsarListener kapreOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "kapreOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.KAPRE))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(kapreOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = "mangkukulamOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MangkukulamOperationEventPulsarListener.class)
    public Consumer<byte[]> mangkukulamOperationEventExternalConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                                      @Autowired MangkukulamOperationEventPulsarListener mangkukulamOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "mangkukulamOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.MANGKUKULAM))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mangkukulamOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "sirenaOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = SIRENA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(SirenaOperationEventPulsarListener.class)
    public Consumer<byte[]> sirenaOperationEventExternalConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                                 @Autowired SirenaOperationEventPulsarListener sirenaOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "sirenaOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.SIRENA))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(sirenaOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = "atlantisOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = ATLANTIS_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(AtlantisOperationEventPulsarListener.class)
    public Consumer<byte[]> atlantisOperationEventConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                           @Autowired AtlantisOperationEventPulsarListener atlantisOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "atlantisOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.ATLANTIS))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(atlantisOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "bayanihanOperationEventExternalConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = BAYANIHAN_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(BayanihanOperationEventPulsarListener.class)
    public Consumer<byte[]> bayanihanOperationEventExternalConsumer(@Autowired PulsarClient operationEventExternalPulsarClient,
                                                                    @Autowired BayanihanOperationEventPulsarListener bayanihanOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "bayanihanOperationEventExternalConsumer");
        return operationEventExternalPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(buildSubscriptionName(appName, env, key, GameTypeEnum.BAYANIHAN))
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(bayanihanOperationEventPulsarListener)
                .subscribe();
    }
}
