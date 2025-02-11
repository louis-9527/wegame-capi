package com.wegame.capi.biz.service;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.GameRecordQueryBuilder;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @Autowired
//    Map<String, MongoTemplate> mongoTemplateMap;
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 批量新增
     *
     * @param gameRecordList 投注记录
     * @return 主键id
     */
    public boolean batchInsert(List<T> gameRecordList, GameTypeEnum gameType) {
        if (CollectionUtil.isEmpty(gameRecordList)) {
            return false;
        }
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM, "gameRecordList is empty");
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        mongoTemplate.insertAll(gameRecordList);
        long endTime = System.currentTimeMillis();
        log.info("GameRecordService#batchInsert cost time:{}ms", endTime - startTime);
        return true;
    }

    // todo cache
    public List<T> listByBetRecordIdWithCache(Long betRecordId, GameTypeEnum gameType, Class<T> clazz) {
        GameRecordQuery query = new GameRecordQuery();
        query.setBetRecordId(betRecordId);
        query.setOrderByClause("_id");
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return mongoTemplate.find(GameRecordQueryBuilder.buildMongodbQuery(query), clazz);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<T> list(GameRecordQuery query, GameTypeEnum gameType, Class<T> clazz) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return mongoTemplate.find(GameRecordQueryBuilder.buildMongodbQuery(query), clazz);
    }

    ///////////////  above auto generated ///////////////
    //    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
    //        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
    //        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
    //        return template;
    //    }
}