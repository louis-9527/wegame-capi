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
@ConditionalOnProperty(name = BAYANIHAN_PROP, havingValue = TRUE_VAL)
public class BayanihanMongoConfig {

    private static final String bayanihanMongoTemplate = "bayanihanMongoTemplate";
    private static final String bayanihanPrimaryMongoTemplate = "bayanihanPrimaryMongoTemplate";
    public static final String bayanihanTransactionManager = "bayanihanTransactionManager";

    @Value("${spring.data.mongodb.dynamic.bayanihan_mdb.uri}")
    String bayanihanMdbUri;
    @Value("${spring.data.mongodb.dynamic.bayanihan_mdb.primary_uri}")
    String bayanihanMdbPrimaryUri;
    @Value("${spring.data.mongodb.dynamic.bayanihan_mdb.db}")
    String bayanihanMdb;

    @Bean(bayanihanMongoTemplate)
    public MongoTemplate bayanihanMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, bayanihanMongoTemplate);
        return new MongoTemplate(MongoClients.create(bayanihanMdbUri), bayanihanMdb);
    }

    @Bean(bayanihanPrimaryMongoTemplate)
    public MongoTemplate bayanihanPrimaryMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, bayanihanPrimaryMongoTemplate);
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(bayanihanMdbPrimaryUri), bayanihanMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = bayanihanTransactionManager)
    public MongoTransactionManager bayanihanTransactionManager(@Autowired MongoTemplate bayanihanMongoTemplate) {
        log.info(BUILD_BEAN_FORMAT, bayanihanTransactionManager);
        MongoTransactionManager manager = new MongoTransactionManager(bayanihanMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
