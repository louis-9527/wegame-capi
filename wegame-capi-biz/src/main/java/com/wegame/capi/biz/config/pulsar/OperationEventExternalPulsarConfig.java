package com.wegame.capi.biz.config.pulsar;

import com.wegame.capi.biz.listener.operation.*;
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

import static com.wegame.capi.common.Constants.*;

/**
 * 干预事件Pulsar外部配置
 */
@Slf4j
@Configuration
public class OperationEventExternalPulsarConfig {
    private static final String operationEventExternalPulsarClient = "operationEventExternalPulsarClient";
    private static final String mariaMakilingOperationEventExternalConsumer = "mariaMakilingOperationEventExternalConsumer";
    private static final String malakasOperationEventExternalConsumer = "malakasOperationEventExternalConsumer";
    private static final String ibongOperationEventExternalConsumer = "ibongOperationEventExternalConsumer";
    private static final String lamangOperationEventExternalConsumer = "lamangOperationEventExternalConsumer";
    private static final String bakunawaOperationEventExternalConsumer = "bakunawaOperationEventExternalConsumer";
    private static final String diwataMayariOperationEventExternalConsumer = "diwataMayariOperationEventExternalConsumer";
    private static final String kapreOperationEventExternalConsumer = "kapreOperationEventExternalConsumer";
    private static final String mangkukulamOperationEventExternalConsumer = "mangkukulamOperationEventExternalConsumer";


    @Value("${dynamic.external.operation.pulsar_url}")
    String dynamicOperationTopicUrl;
    @Value("${dynamic.external.operation.pulsar_topic}")
    String dynamicOperationTopic;
    // 增加默认值 ！！！ 增加默认值 ！！！ 增加默认值 ！！！
    @Value("${dynamic.external.operation.pulsar_group.maria_makiling:cd_External_MariaMakiling}")
    String dynamicOperationGroupMariaMakiling;
    @Value("${dynamic.external.operation.pulsar_group.malakas:cd_External_Malakas}")
    String dynamicOperationGroupMalakas;
    @Value("${dynamic.external.operation.pulsar_group.ibong:cd_External_Ibong}")
    String dynamicOperationGroupIbong;
    @Value("${dynamic.external.operation.pulsar_group.lamang:cd_Lamang}")
    String dynamicOperationGroupLamang;
    @Value("${dynamic.external.operation.pulsar_group.bakunawa:cd_Bakunawa}")
    String dynamicOperationGroupBakunawa;
    @Value("${dynamic.external.operation.pulsar_group.lamang:cd_DiwataMayari}")
    String dynamicOperationGroupDiwataMayari;
    @Value("${dynamic.external.operation.pulsar_group.kapre:cd_Kapre}")
    String dynamicOperationGroupKapre;
    @Value("${dynamic.external.operation.pulsar_group.mangkukulam:_Mangkukulam}")
    String dynamicOperationGroupMangkukulam;
    @Value("${dynamic.external.operation.pulsar.auth.issuer_url}")
    String dynamicOperationAuthIssuerUrl;
    @Value("${dynamic.external.operation.pulsar.auth.credentials_file_path}")
    String dynamicOperationAuthCredentialsFilePath;
    @Value("${dynamic.external.operation.pulsar.auth.audience}")
    String dynamicOperationAuthAudience;

    @Bean(name = operationEventExternalPulsarClient, destroyMethod = COMMON_CLOSE_METHOD)
    public PulsarClient operationEventExternalPulsarClient() throws Exception {
        log.info(BUILD_BEAN_FORMAT, operationEventExternalPulsarClient);
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

    @Bean(name = mariaMakilingOperationEventExternalConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MariamakilingOperationEventPulsarListener.class)
    public Consumer<byte[]> mariaMakilingOperationEventExternalConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                                @Autowired MariamakilingOperationEventPulsarListener mariamakilingOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, mariaMakilingOperationEventExternalConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMariaMakiling)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mariamakilingOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = malakasOperationEventExternalConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MalakasOperationEventPulsarListener.class)
    public Consumer<byte[]> malakasOperationEventExternalConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                          @Autowired MalakasOperationEventPulsarListener malakasOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, malakasOperationEventExternalConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMalakas)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(malakasOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = ibongOperationEventExternalConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(IbongOperationEventPulsarListener.class)
    public Consumer<byte[]> ibongOperationEventExternalConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                        @Autowired IbongOperationEventPulsarListener ibongOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, ibongOperationEventExternalConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupIbong)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(ibongOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = lamangOperationEventExternalConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(LamangOperationEventPulsarListener.class)
    public Consumer<byte[]> lamangOperationEventExternalConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                         @Autowired LamangOperationEventPulsarListener lamangOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, lamangOperationEventExternalConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupLamang)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(lamangOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = bakunawaOperationEventExternalConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = BAKUNAWA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(BakunawaOperationEventPulsarListener.class)
    public Consumer<byte[]> bakunawaOperationEventExternalConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                           @Autowired BakunawaOperationEventPulsarListener bakunawaOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, bakunawaOperationEventExternalConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupBakunawa)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(bakunawaOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = diwataMayariOperationEventExternalConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(DiwataMayariOperationEventPulsarListener.class)
    public Consumer<byte[]> diwataMayariOperationEventExternalConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                               @Autowired DiwataMayariOperationEventPulsarListener diwataMayariOperationEventPulsarListener) throws PulsarClientException {
        log.debug(BUILD_BEAN_FORMAT, diwataMayariOperationEventExternalConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupDiwataMayari)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(diwataMayariOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = kapreOperationEventExternalConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = KAPRE_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(KapreOperationEventPulsarListener.class)
    public Consumer<byte[]> kapreOperationEventExternalConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                        @Autowired KapreOperationEventPulsarListener kapreOperationEventPulsarListener) throws PulsarClientException {
        log.debug(BUILD_BEAN_FORMAT, kapreOperationEventExternalConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupKapre)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(kapreOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = mangkukulamOperationEventExternalConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MangkukulamOperationEventPulsarListener.class)
    public Consumer<byte[]> mangkukulamOperationEventExternalConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                              @Autowired MangkukulamOperationEventPulsarListener mangkukulamOperationEventPulsarListener) throws PulsarClientException {
        log.debug(BUILD_BEAN_FORMAT, mangkukulamOperationEventExternalConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMangkukulam)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mangkukulamOperationEventPulsarListener)
                .subscribe();
    }
}
