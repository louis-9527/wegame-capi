//package com.wegame.capi.infrastructure.config;
//
//import com.mongodb.TransactionOptions;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.MongoTransactionManager;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * @author apple
// */
//@Slf4j
//@Configuration
//public class MongodbTransactionConfig {
//
//    public static final String mariamakilingTransactionManager = "mariamakilingTransactionManager";
//    @Autowired
//    MongoTemplate mariamakilingMongoTemplate;
//
//    @Bean(name = mariamakilingTransactionManager)
//    public MongoTransactionManager mariamakilingTransactionManager() {
//        MongoTransactionManager manager = new MongoTransactionManager(mariamakilingMongoTemplate.getMongoDbFactory());
//        TransactionOptions options = TransactionOptions.builder()
//                .maxCommitTime(20L, TimeUnit.SECONDS)
//                .build();
//        manager.setOptions(options);
//        return manager;
//    }
//}
