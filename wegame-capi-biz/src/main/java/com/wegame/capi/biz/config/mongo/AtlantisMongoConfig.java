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
@ConditionalOnProperty(name = ATLANTIS_PROP, havingValue = TRUE_VAL)
public class AtlantisMongoConfig {

    private static final String atlantisMongoTemplate = "atlantisMongoTemplate";
    private static final String atlantisPrimaryMongoTemplate = "atlantisPrimaryMongoTemplate";
    public static final String atlantisTransactionManager = "atlantisTransactionManager";

    @Value("${spring.data.mongodb.dynamic.atlantis_mdb.uri}")
    String atlantisMdbUri;
    @Value("${spring.data.mongodb.dynamic.atlantis_mdb.primary_uri}")
    String atlantisMdbPrimaryUri;
    @Value("${spring.data.mongodb.dynamic.atlantis_mdb.db}")
    String atlantisMdb;

    @Bean(atlantisMongoTemplate)
    public MongoTemplate atlantisMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, atlantisMongoTemplate);
        return new MongoTemplate(MongoClients.create(atlantisMdbUri), atlantisMdb);
    }

    @Bean(atlantisPrimaryMongoTemplate)
    public MongoTemplate atlantisPrimaryMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, atlantisPrimaryMongoTemplate);
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(atlantisMdbPrimaryUri), atlantisMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = atlantisTransactionManager)
    public MongoTransactionManager atlantisTransactionManager(@Autowired MongoTemplate atlantisMongoTemplate) {
        log.info(BUILD_BEAN_FORMAT, atlantisTransactionManager);
        MongoTransactionManager manager = new MongoTransactionManager(atlantisMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
