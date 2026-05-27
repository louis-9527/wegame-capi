package com.wegame.capi.biz.config.es;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import static com.wegame.capi.common.Constants.*;

@Slf4j
@Configuration
@Conditional(EsConfigEnableCondition.class)
public class EsRestClientConfig {
    @Value("${spring.elasticsearch.rest.uris}")
    private String uris;
    @Value("${spring.elasticsearch.rest.username}")
    private String username;
    @Value("${spring.elasticsearch.rest.password}")
    private String password;
    @Value("${spring.elasticsearch.rest.connection-timeout}")
    private Integer connectTimeOut;
    @Value("${spring.elasticsearch.rest.read-timeout}")
    private Integer readTimeOut;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        log.info(BUILD_BEAN_FORMAT, "restHighLevelClient");
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        // 1. 连接池管理器
        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClientBuilder.create()
                .setDefaultCredentialsProvider(credentialsProvider)
                // 最大连接数
                .setMaxConnTotal(200)
                // 每路由最大并发（通常=ES节点数*5）
                .setMaxConnPerRoute(200)
                // 保持长活（默认就是 keep-alive，这里再写一次）
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectionRequestTimeout(readTimeOut * 1200)
                        .setConnectTimeout(connectTimeOut * 1000)
                        .setSocketTimeout(10_000)
                        .setContentCompressionEnabled(true) // gzip
                        .build())
                .setKeepAliveStrategy((r, c) -> 2 * 60 * 1000)
                // 定期清理过期连接
                .setConnectionManagerShared(false);
        //                .evictExpiredConnections()
        //                .evictIdleConnections(30L, TimeUnit.SECONDS);
        // 2. 构建 RestClient
        RestClientBuilder lowLevelBuilder = RestClient.builder(HttpHost.create(uris)).setHttpClientConfigCallback(h -> httpClientBuilder);
        // 3. 返回高级客户端（Spring 单例）
        return new RestHighLevelClient(lowLevelBuilder);
    }
}
