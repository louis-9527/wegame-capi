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
@ConditionalOnProperty(name = KAPRE_PROP, havingValue = TRUE_VAL)
public class KapreMongoConfig {

    private static final String kapreMongoTemplate = "kapreMongoTemplate";
    private static final String kaprePrimaryMongoTemplate = "kaprePrimaryMongoTemplate";
    public static final String kapreTransactionManager = "kapreTransactionManager";

    @Value("${spring.data.mongodb.dynamic.kapre_mdb.uri}")
    String kapreMdbUri;
    @Value("${spring.data.mongodb.dynamic.kapre_mdb.primary_uri}")
    String kapreMdbPrimaryUri;
    @Value("${spring.data.mongodb.dynamic.kapre_mdb.db}")
    String kapreMdb;

    @Bean(kapreMongoTemplate)
    public MongoTemplate kapreMongoTemplate() {
        log.debug(BUILD_BEAN_FORMAT, kapreMongoTemplate);
        return new MongoTemplate(MongoClients.create(kapreMdbUri), kapreMdb);
    }

    @Bean(kaprePrimaryMongoTemplate)
    public MongoTemplate kaprePrimaryMongoTemplate() {
        log.debug(BUILD_BEAN_FORMAT, kaprePrimaryMongoTemplate);
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(kapreMdbPrimaryUri), kapreMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = kapreTransactionManager)
    public MongoTransactionManager kapreTransactionManager(@Autowired MongoTemplate kapreMongoTemplate) {
        log.debug(BUILD_BEAN_FORMAT, kapreTransactionManager);
        MongoTransactionManager manager = new MongoTransactionManager(kapreMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
