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
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataMayariMongoConfig {

    private static final String diwatamayariMongoTemplate = "diwatamayariMongoTemplate";
    private static final String diwatamayariPrimaryMongoTemplate = "diwatamayariPrimaryMongoTemplate";
    public static final String diwatamayariTransactionManager = "diwatamayariTransactionManager";

    @Value("${spring.data.mongodb.dynamic.diwata_mayari_mdb.uri}")
    String mdiwataMayariMdbUri;
    @Value("${spring.data.mongodb.dynamic.diwata_mayari_mdb.primary_uri}")
    String diwataMayariMdbPrimaryUri;
    @Value("${spring.data.mongodb.dynamic.diwata_mayari_mdb.db}")
    String diwataMayariMdb;

    @Bean(diwatamayariMongoTemplate)
    public MongoTemplate diwataMayariMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, diwatamayariMongoTemplate);
        return new MongoTemplate(MongoClients.create(mdiwataMayariMdbUri), diwataMayariMdb);
    }

    @Bean(diwatamayariPrimaryMongoTemplate)
    public MongoTemplate diwataMayariPrimaryMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, diwatamayariPrimaryMongoTemplate);
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(diwataMayariMdbPrimaryUri), diwataMayariMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = diwatamayariTransactionManager)
    public MongoTransactionManager diwataMayariTransactionManager(@Autowired MongoTemplate diwatamayariMongoTemplate) {
        log.info(BUILD_BEAN_FORMAT, diwatamayariTransactionManager);
        MongoTransactionManager manager = new MongoTransactionManager(diwatamayariMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
