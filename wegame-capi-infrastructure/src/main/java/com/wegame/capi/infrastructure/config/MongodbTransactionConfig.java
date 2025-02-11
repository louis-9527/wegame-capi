package com.wegame.capi.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class MongodbTransactionConfig {

    @Autowired
    MongoTemplate mongoTemplate;
    //    @Autowired
    //    MongoTemplate malakasMongoTemplate;

//    @Bean(name = "mariamakilingTransactionManager")
//    public MongoTransactionManager mariamakilingTransactionManager() {
//        return new MongoTransactionManager(mongoTemplate.getMongoDbFactory());
//    }

    //    @Bean(name = "malakasTransactionManager")
    //    public MongoTransactionManager malakasTransactionManager() {
    //        return new MongoTransactionManager(malakasMongoTemplate.getMongoDatabaseFactory());
    //    }
}
