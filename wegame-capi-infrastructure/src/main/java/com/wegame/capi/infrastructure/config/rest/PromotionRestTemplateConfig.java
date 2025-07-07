package com.wegame.capi.infrastructure.config.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.wegame.capi.common.Constants.COMMON_CLOSE_METHOD;

@Slf4j
@Configuration
public class PromotionRestTemplateConfig {
    public static final String PROMOTION_CONNECTION_MANAGER_BEAN = "promotionConnectionManager";
    public static final String PROMOTION_REST_TEMPLATE_BEAN = "promotionRestTemplate";

    // promotion resTemplate
    @Value("${httpclient.promotion.connectMaxTotal:500}")
    int promotionConnectMaxTotal;
    @Value("${httpclient.promotion.connectDefaultMaxPerRoute:500}")
    int promotionConnectDefaultMaxPerRoute;
    @Value("${httpclient.promotion.connectTimeout:1000}")
    int promotionConnectTimeout;
    @Value("${httpclient.promotion.readTimeout:1000}")
    int promotionReadTimeout;
    @Value("${httpclient.promotion.connectionRequestTimeout:200}")
    int promotionConnectionRequestTimeout;
    // 连接验证配置（设置为30分钟，与预期有效期匹配）
    @Value("${httpclient.promotion.validateAfterInactivity:1800000}")
    int validateAfterInactivity;
    @Value("${httpclient.promotion.timeToLive:60}")
    int promotionTimeToLive;
    @Value("${httpclient.promotion.maxIdleTime:60}")
    int maxIdleTime;

    @Bean(value = PROMOTION_CONNECTION_MANAGER_BEAN, destroyMethod = COMMON_CLOSE_METHOD)
    PoolingHttpClientConnectionManager promotionConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(promotionTimeToLive, TimeUnit.MINUTES);
        manager.setMaxTotal(promotionConnectMaxTotal);
        manager.setDefaultMaxPerRoute(promotionConnectDefaultMaxPerRoute);
        manager.setValidateAfterInactivity(validateAfterInactivity);
        return manager;
    }

    @Bean(PROMOTION_REST_TEMPLATE_BEAN)
    public RestTemplate promotionRestTemplate(@Autowired PoolingHttpClientConnectionManager promotionConnectionManager) {
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(promotionConnectionManager)
                // 连接最大存活时间
                .setConnectionTimeToLive(promotionTimeToLive, TimeUnit.MINUTES)
                .evictIdleConnections(maxIdleTime, TimeUnit.MINUTES)
                .evictExpiredConnections()
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(promotionConnectTimeout);
        factory.setReadTimeout(promotionReadTimeout);
        factory.setConnectionRequestTimeout(promotionConnectionRequestTimeout);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.TEXT_HTML,
                MediaType.TEXT_PLAIN,
                MediaType.IMAGE_JPEG,
                new MediaType("text", "javascript")
        ));
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }
}