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
@ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
public class MangkukulamMongoConfig {

    private static final String mangkukulamMongoTemplate = "mangkukulamMongoTemplate";
    private static final String mangkukulamPrimaryMongoTemplate = "mangkukulamPrimaryMongoTemplate";
    public static final String mangkukulamTransactionManager = "mangkukulamTransactionManager";

    @Value("${spring.data.mongodb.dynamic.mangkukulam_mdb.uri}")
    String mangkukulamMdbUri;
    @Value("${spring.data.mongodb.dynamic.mangkukulam_mdb.primary_uri}")
    String mangkukulamMdbPrimaryUri;
    @Value("${spring.data.mongodb.dynamic.mangkukulam_mdb.db}")
    String mangkukulamMdb;

    /**
     * bean格式，游戏名称小写 + MongoTemplate
     */
    @Bean(mangkukulamMongoTemplate)
    public MongoTemplate mangkukulamMongoTemplate() {
        log.debug(BUILD_BEAN_FORMAT, mangkukulamMongoTemplate);
        return new MongoTemplate(MongoClients.create(mangkukulamMdbUri), mangkukulamMdb);
    }

    /**
     * bean格式，游戏名称小写 + PrimaryMongoTemplate
     */
    @Bean(mangkukulamPrimaryMongoTemplate)
    public MongoTemplate mangkukulamPrimaryMongoTemplate() {
        log.debug(BUILD_BEAN_FORMAT, mangkukulamPrimaryMongoTemplate);
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(mangkukulamMdbPrimaryUri), mangkukulamMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = mangkukulamTransactionManager)
    public MongoTransactionManager mangkukulamTransactionManager(@Autowired MongoTemplate mangkukulamMongoTemplate) {
        log.debug(BUILD_BEAN_FORMAT, mangkukulamTransactionManager);
        MongoTransactionManager manager = new MongoTransactionManager(mangkukulamMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
