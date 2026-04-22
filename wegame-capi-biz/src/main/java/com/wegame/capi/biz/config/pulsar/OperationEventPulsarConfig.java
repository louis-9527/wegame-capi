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
import org.springframework.context.annotation.Profile;

import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.wegame.capi.common.Constants.*;

/**
 * 干预事件Pulsar配置
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = DYNAMIC_OPERATION_SWITCH, havingValue = TRUE_VAL)
public class OperationEventPulsarConfig {
    private static final String operationEventPulsarClient = "operationEventPulsarClient";
    private static final String mariaMakilingOperationEventConsumer = "mariaMakilingOperationEventConsumer";
    private static final String malakasOperationEventConsumer = "malakasOperationEventConsumer";
    private static final String ibongOperationEventConsumer = "ibongOperationEventConsumer";
    private static final String bakunawaOperationEventConsumer = "bakunawaOperationEventConsumer";
    private static final String lamangOperationEventConsumer = "lamangOperationEventConsumer";
    private static final String diwataMayariOperationEventConsumer = "diwataMayariOperationEventConsumer";
    private static final String kapreOperationEventConsumer = "kapreOperationEventConsumer";

    private static final String mangkukulamOperationEventConsumer = "mangkukulamOperationEventConsumer";
    private static final String sirenaOperationEventConsumer = "sirenaOperationEventConsumer";
    private static final String atlantisOperationEventConsumer = "atlantisOperationEventConsumer";

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
    // 增加默认值 ！！！ 增加默认值 ！！！ 增加默认值 ！！！
    @Value("${dynamic.internal.operation.pulsar_group.maria_makiling:cd_MariaMakiling}")
    String dynamicOperationGroupMariaMakiling;
    @Value("${dynamic.internal.operation.pulsar_group.malakas:cd_Malakas}")
    String dynamicOperationGroupMalakas;
    @Value("${dynamic.internal.operation.pulsar_group.ibong:cd_Ibong}")
    String dynamicOperationGroupIbong;
    @Value("${dynamic.internal.operation.pulsar_group.bakunawa:cd_Bakunawa}")
    String dynamicOperationGroupBakunawa;
    @Value("${dynamic.internal.operation.pulsar_group.lamang:cd_Lamang}")
    String dynamicOperationGroupLamang;
    @Value("${dynamic.internal.operation.pulsar_group.lamang:cd_DiwataMayari}")
    String dynamicOperationGroupDiwataMayari;
    @Value("${dynamic.internal.operation.pulsar_group.kapre:cd_Kapre}")
    String dynamicOperationGroupKapre;
    @Value("${dynamic.internal.operation.pulsar_group.mangkukulam:cd_Mangkukulam}")
    String dynamicOperationGroupMangkukulam;
    @Value("${dynamic.internal.operation.pulsar_group.sirena:cd_Sirena}")
    String dynamicOperationGroupSirena;
    @Value("${dynamic.internal.operation.pulsar_group.atlantis:cd_Atlantis}")
    String dynamicOperationGroupAtlantis;


    @Bean(name = operationEventPulsarClient, destroyMethod = COMMON_CLOSE_METHOD)
    public PulsarClient operationEventPulsarClient() throws Exception {
        log.info(BUILD_BEAN_FORMAT, operationEventPulsarClient);
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


    @Bean(name = mariaMakilingOperationEventConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MariamakilingOperationEventPulsarListener.class)
    public Consumer<byte[]> mariaMakilingOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                                @Autowired MariamakilingOperationEventPulsarListener mariamakilingOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, mariaMakilingOperationEventConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMariaMakiling)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mariamakilingOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = malakasOperationEventConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MalakasOperationEventPulsarListener.class)
    public Consumer<byte[]> malakasOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                          @Autowired MalakasOperationEventPulsarListener malakasOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, malakasOperationEventConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMalakas)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(malakasOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = ibongOperationEventConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(IbongOperationEventPulsarListener.class)
    public Consumer<byte[]> ibongOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                        @Autowired IbongOperationEventPulsarListener ibongOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, ibongOperationEventConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupIbong)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(ibongOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = bakunawaOperationEventConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = BAKUNAWA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(BakunawaOperationEventPulsarListener.class)
    public Consumer<byte[]> bakunawaOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                           @Autowired BakunawaOperationEventPulsarListener bakunawaOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, bakunawaOperationEventConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupBakunawa)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(bakunawaOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = lamangOperationEventConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(LamangOperationEventPulsarListener.class)
    public Consumer<byte[]> lamangOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                         @Autowired LamangOperationEventPulsarListener lamangOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, lamangOperationEventConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupLamang)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(lamangOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = diwataMayariOperationEventConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(DiwataMayariOperationEventPulsarListener.class)
    public Consumer<byte[]> diwataMayariOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                               @Autowired DiwataMayariOperationEventPulsarListener diwataMayariOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, diwataMayariOperationEventConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupDiwataMayari)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(diwataMayariOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = kapreOperationEventConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = KAPRE_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(KapreOperationEventPulsarListener.class)
    public Consumer<byte[]> kapreOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                               @Autowired KapreOperationEventPulsarListener kapreOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, kapreOperationEventConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupKapre)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(kapreOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = mangkukulamOperationEventConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MangkukulamOperationEventPulsarListener.class)
    public Consumer<byte[]> mangkukulamOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                              @Autowired MangkukulamOperationEventPulsarListener mangkukulamOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, mangkukulamOperationEventConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMangkukulam)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mangkukulamOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = sirenaOperationEventConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = SIRENA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(SirenaOperationEventPulsarListener.class)
    public Consumer<byte[]> sirenaOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                               @Autowired SirenaOperationEventPulsarListener  sirenaOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, sirenaOperationEventConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupSirena)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(sirenaOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = atlantisOperationEventConsumer, destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = ATLANTIS_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(AtlantisOperationEventPulsarListener.class)
    public Consumer<byte[]> atlantisOperationEventConsumer(@Autowired PulsarClient operationEventPulsarClient,
                                                         @Autowired AtlantisOperationEventPulsarListener  atlantisOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, atlantisOperationEventConsumer);
        return operationEventPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupAtlantis)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(atlantisOperationEventPulsarListener)
                .subscribe();
    }

}
