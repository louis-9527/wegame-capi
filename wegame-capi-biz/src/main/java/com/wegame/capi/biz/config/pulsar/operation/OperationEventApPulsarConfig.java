package com.wegame.capi.biz.config.pulsar.operation;

import com.wegame.capi.biz.listener.operation.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@ConditionalOnProperty(name = {DYNAMIC_OPERATION_SWITCH, DYNAMIC_AP_SWITCH}, havingValue = TRUE_VAL)
public class OperationEventApPulsarConfig {

    private static final String operationEventApPulsarClient = "operationEventApPulsarClient";
    private static final String mariaMakilingOperationEventApConsumer = "mariaMakilingOperationEventApConsumer";
    private static final String malakasOperationEventApConsumer = "malakasOperationEventApConsumer";
    private static final String ibongOperationEventApConsumer = "ibongOperationEventApConsumer";
    private static final String lamangOperationEventApConsumer = "lamangOperationEventApConsumer";
    private static final String bakunawaOperationEventApConsumer = "bakunawaOperationEventApConsumer";
    private static final String diwataMayariOperationEventApConsumer = "diwataMayariOperationEventApConsumer";
    private static final String kapreOperationEventApConsumer = "kapreOperationEventApConsumer";
    private static final String mangkukulamOperationEventApConsumer = "mangkukulamOperationEventApConsumer";


    @Value("${dynamic.ap.operation.pulsar_url}")
    String dynamicOperationTopicUrl;
    @Value("${dynamic.ap.operation.pulsar_topic}")
    String dynamicOperationTopic;
    @Value("${dynamic.ap.operation.pulsar.auth.issuer_url}")
    String dynamicOperationAuthIssuerUrl;
    @Value("${dynamic.ap.operation.pulsar.auth.credentials_file_path}")
    String dynamicOperationAuthCredentialsFilePath;
    @Value("${dynamic.ap.operation.pulsar.auth.audience}")
    String dynamicOperationAuthAudience;
    // 增加默认值 ！！！ 增加默认值 ！！！ 增加默认值 ！！！
    @Value("${dynamic.ap.operation.pulsar_group.maria_makiling:cd_Ap_MariaMakiling}")
    String dynamicOperationGroupMariaMakiling;
    @Value("${dynamic.ap.operation.pulsar_group.malakas:cd_Ap_Malakas}")
    String dynamicOperationGroupMalakas;
    @Value("${dynamic.ap.operation.pulsar_group.ibong:cd_Ap_Ibong}")
    String dynamicOperationGroupIbong;
    @Value("${dynamic.ap.operation.pulsar_group.lamang:cd_Ap_Lamang}")
    String dynamicOperationGroupLamang;
    @Value("${dynamic.ap.operation.pulsar_group.bakunawa:cd_Ap_Bakunawa}")
    String dynamicOperationGroupBakunawa;
    @Value("${dynamic.ap.operation.pulsar_group.diwata_mayari:cd_Ap_DiwataMayari}")
    String dynamicOperationGroupDiwataMayari;
    @Value("${dynamic.ap.operation.pulsar_group.kapre:cd_Ap_Kapre}")
    String dynamicOperationGroupKapre;
    @Value("${dynamic.ap.operation.pulsar_group.mangkukulam:cd_Ap_Mangkukulam}")
    String dynamicOperationGroupMangkukulam;

    @Bean(name = operationEventApPulsarClient, destroyMethod = COMMON_CLOSE_METHOD)
    public PulsarClient operationEventApPulsarClient() throws Exception {
        log.info(BUILD_BEAN_FORMAT, operationEventApPulsarClient);
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

    @Bean(name = mariaMakilingOperationEventApConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MariamakilingOperationEventPulsarListener.class)
    public Consumer<byte[]> mariaMakilingOperationEventApConsumer(@Autowired PulsarClient operationEventApPulsarClient,
                                                                        @Autowired MariamakilingOperationEventPulsarListener mariamakilingOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, mariaMakilingOperationEventApConsumer);
        return operationEventApPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMariaMakiling)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mariamakilingOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = malakasOperationEventApConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MalakasOperationEventPulsarListener.class)
    public Consumer<byte[]> malakasOperationEventApConsumer(@Autowired PulsarClient operationEventApPulsarClient,
                                                                  @Autowired MalakasOperationEventPulsarListener malakasOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, malakasOperationEventApConsumer);
        return operationEventApPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMalakas)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(malakasOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = ibongOperationEventApConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(IbongOperationEventPulsarListener.class)
    public Consumer<byte[]> ibongOperationEventApConsumer(@Autowired PulsarClient operationEventApPulsarClient,
                                                                @Autowired IbongOperationEventPulsarListener ibongOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, ibongOperationEventApConsumer);
        return operationEventApPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupIbong)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(ibongOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = lamangOperationEventApConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(LamangOperationEventPulsarListener.class)
    public Consumer<byte[]> lamangOperationEventApConsumer(@Autowired PulsarClient operationEventApPulsarClient,
                                                         @Autowired LamangOperationEventPulsarListener lamangOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, lamangOperationEventApConsumer);
        return operationEventApPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupLamang)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(lamangOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = bakunawaOperationEventApConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = BAKUNAWA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(BakunawaOperationEventPulsarListener.class)
    public Consumer<byte[]> bakunawaOperationEventApConsumer(@Autowired PulsarClient operationEventApPulsarClient,
                                                           @Autowired BakunawaOperationEventPulsarListener bakunawaOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, bakunawaOperationEventApConsumer);
        return operationEventApPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupBakunawa)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(bakunawaOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = diwataMayariOperationEventApConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(DiwataMayariOperationEventPulsarListener.class)
    public Consumer<byte[]> diwataMayariOperationEventApConsumer(@Autowired PulsarClient operationEventApPulsarClient,
                                                               @Autowired DiwataMayariOperationEventPulsarListener diwataMayariOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, diwataMayariOperationEventApConsumer);
        return operationEventApPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupDiwataMayari)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(diwataMayariOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = kapreOperationEventApConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = KAPRE_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(KapreOperationEventPulsarListener.class)
    public Consumer<byte[]> kapreOperationEventApConsumer(@Autowired PulsarClient operationEventApPulsarClient,
                                                        @Autowired KapreOperationEventPulsarListener kapreOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, kapreOperationEventApConsumer);
        return operationEventApPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupKapre)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(kapreOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = mangkukulamOperationEventApConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MangkukulamOperationEventPulsarListener.class)
    public Consumer<byte[]> mangkukulamOperationEventApConsumer(@Autowired PulsarClient operationEventApPulsarClient,
                                                              @Autowired MangkukulamOperationEventPulsarListener mangkukulamOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, mangkukulamOperationEventApConsumer);
        return operationEventApPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMangkukulam)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mangkukulamOperationEventPulsarListener)
                .subscribe();
    }
}
