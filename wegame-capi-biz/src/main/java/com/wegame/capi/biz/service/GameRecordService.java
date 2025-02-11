package com.wegame.capi.biz.service;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.GameRecordQueryBuilder;
import com.wegame.capi.biz.domain.biz.game.MariaMakilingGameRecordDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
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

    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;

    /**
     * 批量新增
     *
     * @param gameRecordList 投注记录
     * @return 主键id
     */
    public boolean batchAdd(List<T> gameRecordList, GameTypeEnum gameType) {
        if (CollectionUtil.isEmpty(gameRecordList)) {
            return false;
        }
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotEmpty(gameRecordList, ErrorCode.ERR_PARAM, "gameRecordList is empty");
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        getMongoTemplate(gameType).insertAll(gameRecordList);
        long endTime = System.currentTimeMillis();
        log.info("GameRecordService#batchAdd cost time:{}ms", endTime - startTime);
        return true;
    }

    //    /**
    //     * 查询：根据主键id查询
    //     *
    //     * @param id       主键id
    //     * @param playerId 玩家id
    //     * @return BetRecordDo 投注记录
    //     */
    //    public <T> T get(Long id, Long betRecordId, String playerId, GameTypeEnum gameType, Class<T> clazz) {
    //        long startTime = System.currentTimeMillis();
    //        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
    //        Criteria criteria = new Criteria();
    //        criteria.and("_id").is(id);
    //        criteria.and("playerId").is(playerId);
    //        criteria.and("betRecordId").is(betRecordId);
    //        Query query = new Query(criteria);
    //        T t = getMongoTemplate(gameType).findOne(query, clazz);
    //        log.info("GameRecordService#get cost time:{}", System.currentTimeMillis() - startTime);
    //        return t;
    //    }

    // todo cache
    public List<T> listByBetRecordIdWithCache(Long betRecordId, GameTypeEnum gameType, Class<T> clazz) {
        GameRecordQuery query = new GameRecordQuery();
        query.setBetRecordId(betRecordId);
        query.setOrderByClause("_id");
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return getMongoTemplate(gameType).find(GameRecordQueryBuilder.buildMongodbQuery(query), clazz);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<T> list(GameRecordQuery query, GameTypeEnum gameType, Class<T> clazz) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return getMongoTemplate(gameType).find(GameRecordQueryBuilder.buildMongodbQuery(query), clazz);
    }

    ///////////////  above auto generated ///////////////
    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }
}