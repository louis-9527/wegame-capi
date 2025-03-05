package com.wegame.capi.biz.service;

import com.mongodb.ReadPreference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.GameRecordQueryBuilder;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 投注记录服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class GameRecordService<T> {

    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;

    public boolean batchAdd(List<T> gameRecordList, GameTypeEnum gameType, Class<T> tClass) {
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(tClass, ErrorCode.ERR_INTER);
        BulkOperations bulkOps = getMongoTemplate(gameType).bulkOps(BulkOperations.BulkMode.UNORDERED, tClass);
        bulkOps.insert(gameRecordList);
        bulkOps.execute();
        return true;
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<T> list(GameRecordQuery query, GameTypeEnum gameType, Class<T> clazz) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        if (!query.isFromPrimary()) {
            return getMongoTemplate(gameType).find(GameRecordQueryBuilder.buildMongodbQuery(query), clazz);
        } else {
            return getMongoTemplate(gameType).execute(BetRecordPo.class, collection -> {
                collection.withReadPreference(ReadPreference.primary());
                return getMongoTemplate(gameType).find(GameRecordQueryBuilder.buildMongodbQuery(query), clazz);
            });
        }
    }

    ///////////////  above auto generated ///////////////
    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }
}