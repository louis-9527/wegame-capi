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
@ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
public class IbongadarnaMongoConfig {

    private static final String ibongadarnaMongoTemplate = "ibongadarnaMongoTemplate";
    private static final String ibongadarnaPrimaryMongoTemplate= "ibongadarnaPrimaryMongoTemplate";
    public static final String ibongadarnaTransactionManager = "ibongadarnaTransactionManager";

    @Value("${spring.data.mongodb.dynamic.ibong_adarna_mdb.uri}")
    String ibongAdarnaMdbUri;
    @Value("${spring.data.mongodb.dynamic.ibong_adarna_mdb.db}")
    String ibongAdarnaMdb;

    /**
     * bean格式，游戏名称小写 + MongoTemplate
     */
    @Bean(ibongadarnaMongoTemplate)
    public MongoTemplate ibongadarnaMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, ibongadarnaMongoTemplate);
        return new MongoTemplate(MongoClients.create(ibongAdarnaMdbUri), ibongAdarnaMdb);
    }

    /**
     * bean格式，游戏名称小写 + PrimaryMongoTemplate
     */
    @Bean(ibongadarnaPrimaryMongoTemplate)
    public MongoTemplate ibongadarnaPrimaryMongoTemplate() {
        log.info(BUILD_BEAN_FORMAT, ibongadarnaPrimaryMongoTemplate);
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(ibongAdarnaMdbUri), ibongAdarnaMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    @Bean(name = ibongadarnaTransactionManager)
    public MongoTransactionManager ibongadarnaTransactionManager(@Autowired MongoTemplate ibongadarnaMongoTemplate) {
        log.info(BUILD_BEAN_FORMAT, ibongadarnaTransactionManager);
        MongoTransactionManager manager = new MongoTransactionManager(ibongadarnaMongoTemplate.getMongoDbFactory());
        TransactionOptions options = TransactionOptions.builder()
                .maxCommitTime(20L, TimeUnit.SECONDS)
                .build();
        manager.setOptions(options);
        return manager;
    }
}
