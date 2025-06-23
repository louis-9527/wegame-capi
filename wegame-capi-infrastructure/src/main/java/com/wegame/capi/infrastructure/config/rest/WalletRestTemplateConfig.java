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
public class WalletRestTemplateConfig {
    public static final String WALLET_CONNECTION_MANAGER_BEAN = "walletConnectionManager";
    public static final String WALLET_REST_TEMPLATE_BEAN = "walletRestTemplate";
    // wallet resTemplate
    @Value("${httpclient.wallet.connectMaxTotal:1000}")
    int walletConnectMaxTotal;
    @Value("${httpclient.wallet.connectDefaultMaxPerRoute:1000}")
    int walletConnectDefaultMaxPerRoute;
    @Value("${httpclient.wallet.connectTimeout:1000}")
    int walletConnectTimeout;
    @Value("${httpclient.wallet.readTimeout:800}")
    int walletReadTimeout;
    @Value("${httpclient.wallet.connectionRequestTimeout:800}")
    int walletConnectionRequestTimeout;
    @Value("${httpclient.wallet.timeToLive:90}")
    int walletTimeToLive;

    @Bean(value = WALLET_CONNECTION_MANAGER_BEAN, destroyMethod = COMMON_CLOSE_METHOD)
    PoolingHttpClientConnectionManager walletConnectionManager() {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(walletTimeToLive, TimeUnit.MINUTES);
        manager.setMaxTotal(walletConnectMaxTotal);
        manager.setDefaultMaxPerRoute(walletConnectDefaultMaxPerRoute);
        manager.setValidateAfterInactivity(200000);
        return manager;
    }

    @Bean(WALLET_REST_TEMPLATE_BEAN)
    public RestTemplate walletRestTemplate(@Autowired PoolingHttpClientConnectionManager walletConnectionManager) {
        //        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(walletTimeToLive, TimeUnit.MINUTES);
        //        manager.setMaxTotal(walletConnectMaxTotal);
        //        manager.setDefaultMaxPerRoute(walletConnectDefaultMaxPerRoute);
        //        manager.setValidateAfterInactivity(20000);
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(walletConnectionManager)
                // 连接最大存活时间
                .setConnectionTimeToLive(walletTimeToLive, TimeUnit.MINUTES)
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
}