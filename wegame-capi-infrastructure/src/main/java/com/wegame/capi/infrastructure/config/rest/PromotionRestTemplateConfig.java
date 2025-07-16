package com.wegame.capi.infrastructure.config.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
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

    @Value("${httpclient.promotion.connectMaxTotal:500}")
    int connectMaxTotal;
    @Value("${httpclient.promotion.connectDefaultMaxPerRoute:500}")
    int connectDefaultMaxPerRoute;
    @Value("${httpclient.promotion.connectTimeout:200}")
    int connectTimeout;
    @Value("${httpclient.promotion.readTimeout:1000}")
    int readTimeout;
    @Value("${httpclient.promotion.connectionRequestTimeout:100}")
    int connectionRequestTimeout;
    @Value("${httpclient.promotion.timeToLive:55}")
    int timeToLive;
    @Value("${httpclient.promotion.maxIdleTime:50}")
    int maxIdleTime;

    @Bean(value = PROMOTION_CONNECTION_MANAGER_BEAN, destroyMethod = COMMON_CLOSE_METHOD)
    PoolingHttpClientConnectionManager promotionConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(timeToLive, TimeUnit.SECONDS);
        manager.setDefaultSocketConfig(SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .build());
        manager.setMaxTotal(connectMaxTotal);
        manager.setDefaultMaxPerRoute(connectDefaultMaxPerRoute);
        manager.setValidateAfterInactivity(30 * 1000);
        return manager;
    }

    @Bean(PROMOTION_REST_TEMPLATE_BEAN)
    public RestTemplate promotionRestTemplate(@Autowired PoolingHttpClientConnectionManager promotionConnectionManager) {
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(promotionConnectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setSocketTimeout(readTimeout)
                        .setConnectTimeout(connectTimeout)
                        .setConnectionRequestTimeout(connectionRequestTimeout)
                        .build())
                // 连接最大存活时间
                .setConnectionTimeToLive(timeToLive, TimeUnit.SECONDS)
                //                // 设置空闲连接保活时间略小于下游超时时间。 下游超时时间59s
                .evictIdleConnections(maxIdleTime, TimeUnit.SECONDS)
                .evictExpiredConnections()
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                // 启用系统TCP Keep-Alive
                .useSystemProperties()
                // 添加保活策略
                .setKeepAliveStrategy((response, context) -> {
                    // 保持连接存活小于下游59秒超时
                    return timeToLive * 1000;
                })
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        factory.setConnectionRequestTimeout(connectionRequestTimeout);
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