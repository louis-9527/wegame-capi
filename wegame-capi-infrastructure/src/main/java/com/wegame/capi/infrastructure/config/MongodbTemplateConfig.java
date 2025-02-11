//package com.wegame.capi.infrastructure.config;
//
//import com.mongodb.client.MongoClients;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
///**
// * @author apple
// */
//@Slf4j
//@Configuration
//public class MongodbTemplateConfig {
//    public static final String MONGOTEMPLATE = "MongoTemplate";
//
//    @Value("${spring.data.mongodb.dynamic.maria_makiling_mdb.uri}")
//    String mariaMakilingMdbUri;
//    @Value("${spring.data.mongodb.dynamic.maria_makiling_mdb.db}")
//    String mariaMakilingMdbDb;
//    //    @Value("${spring.data.mongodb.dynamic.malakas_mdb.uri}")
//    //    String malakasMdbUri;
//
//    /**
//     * bean格式，游戏名称小写 + MongoTemplate
//     *
//     * @return
//     */
//    @Bean("mariamakilingMongoTemplate")
//    public MongoTemplate mariamakilingMongoTemplate() {
//        return new MongoTemplate(MongoClients.create(mariaMakilingMdbUri), mariaMakilingMdbDb);
//    }
//
//    //    /**
//    //     * bean格式，游戏名称小写 + MongoTemplate
//    //     *
//    //     * @return
//    //     */
//    //    @Bean("malakasMongoTemplate")
//    //    public MongoTemplate malakasMongoTemplate() {
//    //        return new MongoTemplate(MongoClients.create(malakasMdbUri), "malakas_mdb");
//    //    }
//}
