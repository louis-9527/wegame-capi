package com.wegame.capi.biz.config.es;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class EsRestClientConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "Innovatech@123")
        );
        // 1. 连接池管理器
        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClientBuilder.create()
                .setDefaultCredentialsProvider(credentialsProvider)
                // 最大连接数
                .setMaxConnTotal(200)
                // 每路由最大并发（通常=ES节点数*5）
                .setMaxConnPerRoute(200)
                // 保持长活（默认就是 keep-alive，这里再写一次）
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectionRequestTimeout(5_000)
                                .setConnectTimeout(5_000)
                                .setSocketTimeout(60_000)
                                .setContentCompressionEnabled(true) // gzip
                                .build()
                )

                .setKeepAliveStrategy((r, c) -> 15 * 60 * 1000)
                // 定期清理过期连接
                .setConnectionManagerShared(false);
//                .evictExpiredConnections()
//                .evictIdleConnections(30L, TimeUnit.SECONDS);

        // 2. 构建 RestClient
        RestClientBuilder lowLevelBuilder = RestClient.builder(
                        new HttpHost("es-sg-u7g4f8un40002xovb.public.elasticsearch.aliyuncs.com", 9200, "http"))
                .setHttpClientConfigCallback(h -> httpClientBuilder);

        // 3. 返回高级客户端（Spring 单例）
        return new RestHighLevelClient(lowLevelBuilder);
    }
}
