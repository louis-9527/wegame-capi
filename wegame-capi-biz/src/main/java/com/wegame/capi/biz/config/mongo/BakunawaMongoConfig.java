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
@ConditionalOnProperty(name = BAKUNAWA_PROP, havingValue = TRUE_VAL)
public class BakunawaMongoConfig {

    private static final String bakunawaMongoTemplate = "bakunawaMongoTemplate";
    private static final String bakunawaPrimaryMongoTemplate= "bakunawaPrimaryMongoTemplate";
    public static final String bakunawaTransactionManager = "bakunawaTransactionManager";

    @Value("${spring.data.mongodb.dynamic.bakunawa_mdb.uri}")
    String bakunawaMdbUri;
    @Value("${spring.data.mongodb.dynamic.bakunawa_mdb.db}")
    String bakunawaMdb;

    /**
     * bean格式，游戏名称小写 + MongoTemplate
     */
    @Bean(bakunawaMongoTemplate)
    public MongoTemplate bakunawaMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, bakunawaMongoTemplate);
        return new MongoTemplate(MongoClients.create(bakunawaMdbUri), bakunawaMdb);
    }

    /**
     * bean格式，游戏名称小写 + PrimaryMongoTemplate
     */
    @Bean(bakunawaPrimaryMongoTemplate)
    public MongoTemplate bakunawaPrimaryMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, bakunawaPrimaryMongoTemplate);
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(bakunawaMdbUri), bakunawaMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = bakunawaTransactionManager)
    public MongoTransactionManager bakunawaTransactionManager(@Autowired MongoTemplate bakunawaMongoTemplate) {
        log.info(BUILD_BEAN_FORMAT, bakunawaTransactionManager);
        MongoTransactionManager manager = new MongoTransactionManager(bakunawaMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
