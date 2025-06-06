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

import static com.wegame.capi.common.Constants.*;

@Slf4j
@Configuration
@ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
public class MariamakilingMongoConfig {

    private static final String mariamakilingMongoTemplate = "mariamakilingMongoTemplate";
    private static final String mariamakilingPrimaryMongoTemplate = "mariamakilingPrimaryMongoTemplate";
    public static final String mariamakilingTransactionManager = "mariamakilingTransactionManager";

    @Value("${spring.data.mongodb.dynamic.maria_makiling_mdb.uri}")
    String mariaMakilingMdbUri;
    @Value("${spring.data.mongodb.dynamic.maria_makiling_mdb.primary_uri}")
    String mariaMakilingMdbPrimaryUri;
    @Value("${spring.data.mongodb.dynamic.maria_makiling_mdb.db}")
    String mariaMakilingMdb;

    /**
     * bean格式，游戏名称小写 + MongoTemplate
     */
    @Bean(mariamakilingMongoTemplate)
    public MongoTemplate mariamakilingMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, mariamakilingMongoTemplate);
        return new MongoTemplate(MongoClients.create(mariaMakilingMdbUri), mariaMakilingMdb);
    }

    /**
     * bean格式，游戏名称小写 + PrimaryMongoTemplate
     */
    @Bean(mariamakilingPrimaryMongoTemplate)
    public MongoTemplate mariamakilingPrimaryMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, mariamakilingPrimaryMongoTemplate);
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(mariaMakilingMdbPrimaryUri), mariaMakilingMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = mariamakilingTransactionManager)
    public MongoTransactionManager mariamakilingTransactionManager(@Autowired MongoTemplate mariamakilingMongoTemplate) {
        log.info(BUILD_BEAN_FORMAT, mariamakilingTransactionManager);
        MongoTransactionManager manager = new MongoTransactionManager(mariamakilingMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
