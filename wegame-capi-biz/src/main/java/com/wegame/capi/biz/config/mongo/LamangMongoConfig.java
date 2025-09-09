package com.wegame.capi.biz.config.mongo;

import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

import static com.wegame.capi.common.Constants.*;

@Slf4j
@Configuration
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangMongoConfig {

    private static final String lamangMongoTemplate = "lamangMongoTemplate";
    private static final String lamangPrimaryMongoTemplate= "lamangPrimaryMongoTemplate";
    public static final String lamangTransactionManager = "lamangTransactionManager";

    @Value("${spring.data.mongodb.dynamic.lamang_mdb.uri}")
    String lamangMdbUri;
    @Value("${spring.data.mongodb.dynamic.lamang_mdb.primary_uri}")
    String lamangMdbPrimaryUri;
    @Value("${spring.data.mongodb.dynamic.lamang_mdb.db}")
    String lamangMdb;

    /**
     * bean格式，游戏名称小写 + MongoTemplate
     */
    @Bean(lamangMongoTemplate)
    public MongoTemplate lamangMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, lamangMongoTemplate);
        return new MongoTemplate(MongoClients.create(lamangMdbUri), lamangMdb);
    }

    /**
     * bean格式，游戏名称小写 + PrimaryMongoTemplate
     */
    @Bean(lamangPrimaryMongoTemplate)
    public MongoTemplate lamangPrimaryMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, lamangPrimaryMongoTemplate);
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(lamangMdbPrimaryUri), lamangMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = lamangTransactionManager)
    public MongoTransactionManager lamangTransactionManager(@Autowired MongoTemplate lamangMongoTemplate) {
        log.info(BUILD_BEAN_FORMAT, lamangTransactionManager);
        MongoTransactionManager manager = new MongoTransactionManager(lamangMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
