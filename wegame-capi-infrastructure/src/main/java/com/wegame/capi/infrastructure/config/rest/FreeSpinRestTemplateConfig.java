package com.wegame.capi.infrastructure.config.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
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
public class FreeSpinRestTemplateConfig {
    public static final String FREE_SPIN_CONNECTION_MANAGER_BEAN = "freeSpinConnectionManager";
    public static final String FREE_SPIN_REST_TEMPLATE_BEAN = "freeSpinRestTemplate";
    // freeSpin resTemplate
    @Value("${httpclient.freeSpin.connectMaxTotal:1000}")
    int freeSpinConnectMaxTotal;
    @Value("${httpclient.freeSpin.connectDefaultMaxPerRoute:1000}")
    int freeSpinConnectDefaultMaxPerRoute;
    @Value("${httpclient.freeSpin.connectTimeout:1000}")
    int freeSpinConnectTimeout;
    @Value("${httpclient.freeSpin.readTimeout:1000}")
    int freeSpinReadTimeout;
    // 连接验证配置（设置为30分钟，与预期有效期匹配）
    @Value("${httpclient.freeSpin.validateAfterInactivity:1800000}")
    int validateAfterInactivity;
    @Value("${httpclient.freeSpin.connectionRequestTimeout:200}")
    int freeSpinConnectionRequestTimeout;
    @Value("${httpclient.freeSpin.timeToLive:60}")
    int freeSpinTimeToLive;
    @Value("${httpclient.freeSpin.maxIdleTime:60}")
    int maxIdleTime;

    @Bean(value = FREE_SPIN_CONNECTION_MANAGER_BEAN, destroyMethod = COMMON_CLOSE_METHOD)
    PoolingHttpClientConnectionManager freeSpinConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(freeSpinTimeToLive, TimeUnit.MINUTES);
        manager.setMaxTotal(freeSpinConnectMaxTotal);
        manager.setDefaultMaxPerRoute(freeSpinConnectDefaultMaxPerRoute);
        manager.setValidateAfterInactivity(validateAfterInactivity);
        return manager;
    }

    @Bean(FREE_SPIN_REST_TEMPLATE_BEAN)
    public RestTemplate freeSpinRestTemplate(@Autowired PoolingHttpClientConnectionManager freeSpinConnectionManager) {
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(freeSpinConnectionManager)
                // 连接最大存活时间
                .setConnectionTimeToLive(freeSpinTimeToLive, TimeUnit.MINUTES)
                .evictIdleConnections(maxIdleTime, TimeUnit.MINUTES)
                .evictExpiredConnections()
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(freeSpinConnectTimeout);
        factory.setReadTimeout(freeSpinReadTimeout);
        factory.setConnectionRequestTimeout(freeSpinConnectionRequestTimeout);
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