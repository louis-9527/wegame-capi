package com.wegame.capi.biz.config.pulsar.operation;

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
@ConditionalOnProperty(name = {DYNAMIC_OPERATION_SWITCH, DYNAMIC_NP_OPERATION_SWITCH}, havingValue = TRUE_VAL)
public class OperationEventNpPulsarConfig {
    @Value("${dynamic.np.operation.pulsar_url}")
    String dynamicOperationTopicUrl;
    @Value("${dynamic.np.operation.pulsar_topic}")
    String dynamicOperationTopic;
    @Value("${dynamic.np.operation.pulsar.auth.issuer_url}")
    String dynamicOperationAuthIssuerUrl;
    @Value("${dynamic.np.operation.pulsar.auth.credentials_file_path}")
    String dynamicOperationAuthCredentialsFilePath;
    @Value("${dynamic.np.operation.pulsar.auth.audience}")
    String dynamicOperationAuthAudience;
    // 增加默认值 ！！！ 增加默认值 ！！！ 增加默认值 ！！！
    @Value("${dynamic.np.operation.pulsar_group.maria_makiling:cd_Np_MariaMakiling}")
    String dynamicOperationGroupMariaMakiling;
    @Value("${dynamic.np.operation.pulsar_group.malakas:cd_Np_Malakas}")
    String dynamicOperationGroupMalakas;
    @Value("${dynamic.np.operation.pulsar_group.ibong:cd_Np_Ibong}")
    String dynamicOperationGroupIbong;
    @Value("${dynamic.np.operation.pulsar_group.lamang:cd_Np_Lamang}")
    String dynamicOperationGroupLamang;
    @Value("${dynamic.np.operation.pulsar_group.bakunawa:cd_Np_Bakunawa}")
    String dynamicOperationGroupBakunawa;
    @Value("${dynamic.np.operation.pulsar_group.diwata_mayari:cd_Np_DiwataMayari}")
    String dynamicOperationGroupDiwataMayari;
    @Value("${dynamic.np.operation.pulsar_group.kapre:cd_Np_Kapre}")
    String dynamicOperationGroupKapre;
    @Value("${dynamic.np.operation.pulsar_group.mangkukulam:cd_Np_Mangkukulam}")
    String dynamicOperationGroupMangkukulam;
    @Value("${dynamic.np.operation.pulsar_group.sirena:cd_Np_Sirena}")
    String dynamicOperationGroupSirena;
    @Value("${dynamic.np.operation.pulsar_group.bayanihan:cd_Np_Bayanihan}")
    String dynamicOperationGroupBayanihan;

    @Bean(name = "operationEventNpPulsarClient", destroyMethod = COMMON_CLOSE_METHOD)
    public PulsarClient operationEventNpPulsarClient() throws Exception {
        log.info(BUILD_BEAN_FORMAT, "operationEventNpPulsarClient");
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

    @Bean(name = "mariaMakilingOperationEventNpConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MariamakilingOperationEventPulsarListener.class)
    public Consumer<byte[]> mariaMakilingOperationEventNpConsumer(@Autowired PulsarClient operationEventNpPulsarClient,
                                                                  @Autowired MariamakilingOperationEventPulsarListener mariamakilingOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "mariaMakilingOperationEventNpConsumer");
        return operationEventNpPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMariaMakiling)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mariamakilingOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "malakasOperationEventNpConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MalakasOperationEventPulsarListener.class)
    public Consumer<byte[]> malakasOperationEventNpConsumer(@Autowired PulsarClient operationEventNpPulsarClient,
                                                            @Autowired MalakasOperationEventPulsarListener malakasOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "malakasOperationEventNpConsumer");
        return operationEventNpPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMalakas)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(malakasOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "ibongOperationEventNpConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(IbongOperationEventPulsarListener.class)
    public Consumer<byte[]> ibongOperationEventNpConsumer(@Autowired PulsarClient operationEventNpPulsarClient,
                                                          @Autowired IbongOperationEventPulsarListener ibongOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "ibongOperationEventNpConsumer");
        return operationEventNpPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupIbong)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(ibongOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "lamangOperationEventNpConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(LamangOperationEventPulsarListener.class)
    public Consumer<byte[]> lamangOperationEventNpConsumer(@Autowired PulsarClient operationEventNpPulsarClient,
                                                           @Autowired LamangOperationEventPulsarListener lamangOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "lamangOperationEventNpConsumer");
        return operationEventNpPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupLamang)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(lamangOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = "bakunawaOperationEventNpConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = BAKUNAWA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(BakunawaOperationEventPulsarListener.class)
    public Consumer<byte[]> bakunawaOperationEventNpConsumer(@Autowired PulsarClient operationEventNpPulsarClient,
                                                             @Autowired BakunawaOperationEventPulsarListener bakunawaOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "bakunawaOperationEventNpConsumer");
        return operationEventNpPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupBakunawa)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(bakunawaOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = "diwataMayariOperationEventNpConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(DiwataMayariOperationEventPulsarListener.class)
    public Consumer<byte[]> diwataMayariOperationEventNpConsumer(@Autowired PulsarClient operationEventNpPulsarClient,
                                                                 @Autowired DiwataMayariOperationEventPulsarListener diwataMayariOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "diwataMayariOperationEventNpConsumer");
        return operationEventNpPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupDiwataMayari)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(diwataMayariOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "kapreOperationEventNpConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = KAPRE_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(KapreOperationEventPulsarListener.class)
    public Consumer<byte[]> kapreOperationEventNpConsumer(@Autowired PulsarClient operationEventNpPulsarClient,
                                                          @Autowired KapreOperationEventPulsarListener kapreOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "kapreOperationEventNpConsumer");
        return operationEventNpPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupKapre)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(kapreOperationEventPulsarListener)
                .subscribe();
    }


    @Bean(name = "mangkukulamOperationEventNpConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(MangkukulamOperationEventPulsarListener.class)
    public Consumer<byte[]> mangkukulamOperationEventNpConsumer(@Autowired PulsarClient operationEventNpPulsarClient,
                                                                @Autowired MangkukulamOperationEventPulsarListener mangkukulamOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "mangkukulamOperationEventNpConsumer");
        return operationEventNpPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupMangkukulam)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(mangkukulamOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "sirenaOperationEventNpConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = SIRENA_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(SirenaOperationEventPulsarListener.class)
    public Consumer<byte[]> sirenaOperationEventNpConsumer(@Autowired PulsarClient operationEventNpPulsarClient,
                                                           @Autowired SirenaOperationEventPulsarListener sirenaOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "sirenaOperationEventNpConsumer");
        return operationEventNpPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupSirena)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(sirenaOperationEventPulsarListener)
                .subscribe();
    }

    @Bean(name = "bayanihanOperationEventNpConsumer", destroyMethod = COMMON_CLOSE_METHOD)
    @ConditionalOnProperty(name = BAYANIHAN_PROP, havingValue = TRUE_VAL)
    @ConditionalOnBean(BayanihanOperationEventPulsarListener.class)
    public Consumer<byte[]> bayanihanOperationEventNpConsumer(@Autowired PulsarClient operationEventNpPulsarClient,
                                                              @Autowired BayanihanOperationEventPulsarListener bayanihanOperationEventPulsarListener) throws PulsarClientException {
        log.info(BUILD_BEAN_FORMAT, "bayanihanOperationEventNpConsumer");
        return operationEventNpPulsarClient.newConsumer(Schema.BYTES)
                .topic(dynamicOperationTopic)
                .subscriptionName(dynamicOperationGroupBayanihan)
                .subscriptionType(SubscriptionType.Shared)
                .messageListener(bayanihanOperationEventPulsarListener)
                .subscribe();
    }
}
