package com.wegame.capi.biz.service;

import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.GameRecordQueryBuilder;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.wegame.capi.infrastructure.config.MongodbTemplateConfig.MONGOTEMPLATE;
import static com.wegame.capi.infrastructure.config.MongodbTemplateConfig.PRIMARY_MONGOTEMPLATE;
import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.gameRecordPutCacheThreadPoolExecutor;

/**
 * 游戏记录服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class GameRecordService<T> extends AbstractService {

    public boolean batchAdd(List<T> gameRecordList, GameTypeEnum gameType, Class<T> tClass) {
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(tClass, ErrorCode.ERR_INTER);
        BulkOperations bulkOps = getMongoTemplate(gameType).bulkOps(BulkOperations.BulkMode.UNORDERED, tClass);
        bulkOps.insert(gameRecordList);
        bulkOps.execute();
        return true;
    }

    @Async(gameRecordPutCacheThreadPoolExecutor)
    @CachePut(value = CacheConstants.GAME_RECORD, key = "#gameType.name + '_' + #cacheKey")
    public List<T> asyncPutCache(List<T> gameRecordList, GameTypeEnum gameType, String cacheKey) {
        return gameRecordList;
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
            return getPrimaryMongoTemplate(gameType).find(GameRecordQueryBuilder.buildMongodbQuery(query), clazz);
        }
    }

    public Long count(GameRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        if (!query.isFromPrimary()) {
            return getMongoTemplate(gameType).count(GameRecordQueryBuilder.buildMongodbQuery(query), gameType.getGameRecordCollectionName());
        } else {
            return getPrimaryMongoTemplate(gameType).count(GameRecordQueryBuilder.buildMongodbQuery(query), gameType.getGameRecordCollectionName());
        }
    }
}