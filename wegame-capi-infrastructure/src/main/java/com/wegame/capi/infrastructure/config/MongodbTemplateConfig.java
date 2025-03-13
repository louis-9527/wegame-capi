package com.wegame.capi.infrastructure.config;

import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author apple
 */
@Slf4j
@Configuration
public class MongodbTemplateConfig {
    public static final String MONGOTEMPLATE = "MongoTemplate";
    public static final String PRIMARY_MONGOTEMPLATE = "PrimaryMongoTemplate";

    private static final String mariamakilingMongoTemplate = "mariamakilingMongoTemplate";
    private static final String mariamakilingPrimaryMongoTemplate= "mariamakilingPrimaryMongoTemplate";

    private static final String malakasMongoTemplate = "malakasMongoTemplate";
    private static final String malakasPrimaryMongoTemplate= "malakasPrimaryMongoTemplate";


    @Value("${spring.data.mongodb.dynamic.maria_makiling_mdb.uri}")
    String mariaMakilingMdbUri;
    @Value("${spring.data.mongodb.dynamic.maria_makiling_mdb.db}")
    String mariaMakilingMdb;
    @Value("${spring.data.mongodb.dynamic.malakas_mdb.uri}")
    String malakasMdbUri;
    @Value("${spring.data.mongodb.dynamic.malakas_mdb.db}")
    String malakasMdb;

    /**
     * bean格式，游戏名称小写 + MongoTemplate
     */
    @Bean(mariamakilingMongoTemplate)
    public MongoTemplate mariamakilingMongoTemplate() {
        return new MongoTemplate(MongoClients.create(mariaMakilingMdbUri), mariaMakilingMdb);
    }

    /**
     * bean格式，游戏名称小写 + PrimaryMongoTemplate
     */
    @Bean(mariamakilingPrimaryMongoTemplate)
    public MongoTemplate mariamakilingPrimaryMongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(mariaMakilingMdbUri), mariaMakilingMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }

    /**
     * bean格式，游戏名称小写 + MongoTemplate
     * @return
     */
    @Bean(malakasMongoTemplate)
    public MongoTemplate malakasMongoTemplate() {
        return new MongoTemplate(MongoClients.create(malakasMdbUri), malakasMdb);
    }

    @Bean(malakasPrimaryMongoTemplate)
    public MongoTemplate malakasPrimaryMongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(malakasMdbUri), malakasMdb);
        mongoTemplate.setReadPreference(ReadPreference.primary());
        return mongoTemplate;
    }
}
