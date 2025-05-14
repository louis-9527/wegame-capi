package com.wegame.capi.infrastructure.config.mongo;

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

import static com.wegame.capi.common.Constants.MALAKAS_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Configuration
@ConditionalOnProperty(name = MALAKAS_PROP, havingValue = TRUE_VAL)
public class MalakasMongoConfig {

    private static final String malakasMongoTemplate = "malakasMongoTemplate";
    private static final String malakasPrimaryMongoTemplate = "malakasPrimaryMongoTemplate";
    public static final String malakasTransactionManager = "malakasTransactionManager";

    @Value("${spring.data.mongodb.dynamic.malakas_mdb.uri}")
    String malakasMdbUri;
    @Value("${spring.data.mongodb.dynamic.malakas_mdb.primary_uri}")
    String malakasMdbPrimaryUri;
    @Value("${spring.data.mongodb.dynamic.malakas_mdb.db}")
    String malakasMdb;

    @Bean(malakasMongoTemplate)
    public MongoTemplate malakasMongoTemplate() {
        return new MongoTemplate(MongoClients.create(malakasMdbUri), malakasMdb);
    }

    @Bean(malakasPrimaryMongoTemplate)
    public MongoTemplate malakasPrimaryMongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(malakasMdbPrimaryUri), malakasMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = malakasTransactionManager)
    public MongoTransactionManager malakasTransactionManager(@Autowired MongoTemplate malakasMongoTemplate) {
        MongoTransactionManager manager = new MongoTransactionManager(malakasMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
