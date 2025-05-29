package com.wegame.capi.infrastructure.config;

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

@Slf4j
@Configuration
public class RestTemplateConfig {

    public static final String WALLET_REST_TEMPLATE_BEAN = "walletRestTemplate";
    public static final String PROMOTION_REST_TEMPLATE_BEAN = "promotionRestTemplate";
    // wallet resTemplate
    @Value("${httpclient.common.connectMaxTotal:5000}")
    int walletConnectMaxTotal;
    @Value("${httpclient.common.connectDefaultMaxPerRoute:1000}")
    int walletConnectDefaultMaxPerRoute;
    @Value("${httpclient.wallet.connectTimeout:1000}")
    int walletConnectTimeout;
    @Value("${httpclient.wallet.readTimeout:800}")
    int walletReadTimeout;
    @Value("${httpclient.wallet.connectionRequestTimeout:800}")
    int walletConnectionRequestTimeout;

    // promotion resTemplate
    @Value("${httpclient.common.connectMaxTotal:5000}")
    int promotionConnectMaxTotal;
    @Value("${httpclient.common.connectDefaultMaxPerRoute:500}")
    int promotionConnectDefaultMaxPerRoute;
    @Value("${httpclient.promotion.connectTimeout:1000}")
    int promotionConnectTimeout;
    @Value("${httpclient.promotion.readTimeout:200}")
    int promotionReadTimeout;
    @Value("${httpclient.promotion.connectionRequestTimeout:200}")
    int promotionConnectionRequestTimeout;

    @Bean(WALLET_REST_TEMPLATE_BEAN)
    public RestTemplate walletRestTemplate() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(30, TimeUnit.MINUTES);
        manager.setMaxTotal(walletConnectMaxTotal);
        manager.setDefaultMaxPerRoute(walletConnectDefaultMaxPerRoute);
        manager.setValidateAfterInactivity(20000);
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(manager)
                // 连接最大存活时间
                .setConnectionTimeToLive(30, TimeUnit.MINUTES)
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(walletConnectTimeout);
        factory.setReadTimeout(walletReadTimeout);
        factory.setConnectionRequestTimeout(walletConnectionRequestTimeout);
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

    @Bean(PROMOTION_REST_TEMPLATE_BEAN)
    public RestTemplate promotionRestTemplate() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(30, TimeUnit.MINUTES);
        manager.setMaxTotal(promotionConnectMaxTotal);
        manager.setDefaultMaxPerRoute(promotionConnectDefaultMaxPerRoute);
        manager.setValidateAfterInactivity(20000);
        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(manager).build();
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

    private void warmUpConnections(PoolingHttpClientConnectionManager manager, int numConnections, HttpHost target) {
        for (int i = 0; i < numConnections; i++) {
            try {
                manager.requestConnection(new HttpRoute(target), null);
            } catch (Exception e) {
                log.error("warmUpConnections error", e);
            }
        }
    }
}