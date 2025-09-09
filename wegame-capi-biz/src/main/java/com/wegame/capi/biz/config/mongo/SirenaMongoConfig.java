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
@ConditionalOnProperty(name = SIRENA_PROP, havingValue = TRUE_VAL)
public class SirenaMongoConfig {

    private static final String sirenaMongoTemplate = "sirenaMongoTemplate";
    private static final String sirenaPrimaryMongoTemplate = "sirenaPrimaryMongoTemplate";
    public static final String sirenaTransactionManager = "sirenaTransactionManager";

    @Value("${spring.data.mongodb.dynamic.sirena_mdb.uri}")
    String sirenaMdbUri;
    @Value("${spring.data.mongodb.dynamic.sirena_mdb.primary_uri}")
    String sirenaMdbPrimaryUri;
    @Value("${spring.data.mongodb.dynamic.sirena_mdb.db}")
    String sirenaMdb;

    @Bean(sirenaMongoTemplate)
    public MongoTemplate sirenaMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, sirenaMongoTemplate);
        return new MongoTemplate(MongoClients.create(sirenaMdbUri), sirenaMdb);
    }

    @Bean(sirenaPrimaryMongoTemplate)
    public MongoTemplate sirenaPrimaryMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, sirenaPrimaryMongoTemplate);
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(sirenaMdbPrimaryUri), sirenaMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = sirenaTransactionManager)
    public MongoTransactionManager sirenaTransactionManager(@Autowired MongoTemplate sirenaMongoTemplate) {
        log.info(BUILD_BEAN_FORMAT, sirenaTransactionManager);
        MongoTransactionManager manager = new MongoTransactionManager(sirenaMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
