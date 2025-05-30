package com.wegame.capi.api.base;

import com.wegame.capi.common.annotation.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.wegame.base.config.HttpClientConfig.COMMON_REST_TEMPLATE_BEAN;
import static com.wegame.capi.infrastructure.config.rest.FreeSpinRestTemplateConfig.FREE_SPIN_REST_TEMPLATE_BEAN;
import static com.wegame.capi.infrastructure.config.rest.PromotionRestTemplateConfig.PROMOTION_REST_TEMPLATE_BEAN;
import static com.wegame.capi.infrastructure.config.rest.WalletRestTemplateConfig.WALLET_REST_TEMPLATE_BEAN;

/**
 * base客户端
 */
@Slf4j
@Client
@Component
public class BaseApi {
    @Autowired
    @Qualifier(COMMON_REST_TEMPLATE_BEAN)
    public RestTemplate commonRestTemplate;
    @Autowired
    @Qualifier(WALLET_REST_TEMPLATE_BEAN)
    public RestTemplate walletRestTemplate;
    @Autowired
    @Qualifier(PROMOTION_REST_TEMPLATE_BEAN)
    public RestTemplate promotionRestTemplate;
    @Autowired
    @Qualifier(FREE_SPIN_REST_TEMPLATE_BEAN)
    public RestTemplate freeSpinRestTemplate;
}


