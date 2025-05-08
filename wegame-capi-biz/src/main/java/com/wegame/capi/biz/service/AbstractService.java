package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.infrastructure.config.properties.GameProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.Map;

import static com.wegame.capi.infrastructure.config.MongodbTemplateConfig.MONGOTEMPLATE;
import static com.wegame.capi.infrastructure.config.MongodbTemplateConfig.PRIMARY_MONGOTEMPLATE;

public abstract class AbstractService implements InitializingBean {

    @Autowired
    GameProperty gameProperty;
    @Resource
    Map<String, MongoTemplate> mongoTemplateMap;

    @Override
    public void afterPropertiesSet() {
        gameProperty.enabledGameTypes().forEach(gameType -> {
            MongoTemplate mongoTemplate = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MONGOTEMPLATE);
            AssertUtil.assertNotNull(mongoTemplate, ErrorCode.ERR_INTER);
            MongoTemplate primaryMongoTemplate = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + PRIMARY_MONGOTEMPLATE);
            AssertUtil.assertNotNull(primaryMongoTemplate, ErrorCode.ERR_INTER);
        });
    }

    public MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        return mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MONGOTEMPLATE);
    }

    public MongoTemplate getPrimaryMongoTemplate(GameTypeEnum gameType) {
        return mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + PRIMARY_MONGOTEMPLATE);
    }
}
