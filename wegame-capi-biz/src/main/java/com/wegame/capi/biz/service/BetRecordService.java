package com.wegame.capi.biz.service;

import cn.hutool.core.lang.Opt;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordQueryBuilder;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 投注记录服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class BetRecordService {

    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;

    public Long add(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        BetRecordPo po = getMongoTemplate(gameType).insert(BetRecordConverter.doToPo(betRecordDo, OptEnum.ADD),
                gameType.getBetRecordCollectionName());
        long endTime = System.currentTimeMillis();
        log.info("addBetRecord id:{}", betRecordDo.getId());
        log.info("BetRecordService#add cost time:{}", endTime - startTime);
        return po.getId();
    }

    public boolean batchAdd(List<BetRecordDo> betRecordList, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM, "betRecordList is empty");
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        List<BetRecordPo> poList = BetRecordConverter.doToPoList(betRecordList, OptEnum.ADD);
        BulkOperations bulkOps = getMongoTemplate(gameType).bulkOps(BulkOperations.BulkMode.UNORDERED,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        poList.forEach(item -> {
            // todo delete log
            log.info("addBetRecord id:{}", item.getId());
            bulkOps.insert(item);
        });
        bulkOps.execute();
        long endTime = System.currentTimeMillis();
        log.info("BetRecordService#batchInsert cost time:{}ms", endTime - startTime);
        return true;
    }

    public boolean update(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betRecordDo.getId(), ErrorCode.ERR_PARAM, "request param \'id\' is null");
        Criteria criteria = new Criteria();
        criteria.and("_id").is(betRecordDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(gameType).updateFirst(query,
                BetRecordQueryBuilder.buildMongoUpdate(betRecordDo),
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        long endTime = System.currentTimeMillis();
        log.info("BetRecordService#update cost time:{}ms", endTime - startTime);
        return Objects.nonNull(result) && result.getModifiedCount() > 0;
    }

    public boolean batchUpdate(List<BetRecordDo> betRecordDoList, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotEmpty(betRecordDoList, ErrorCode.ERR_PARAM, "betRecordDoList is empty");
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        BulkOperations bulkOps = getMongoTemplate(gameType).bulkOps(BulkOperations.BulkMode.UNORDERED,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        betRecordDoList.forEach(betRecordDo -> {
            Criteria criteria = new Criteria();
            criteria.and("_id").is(betRecordDo.getId());
            Query query = new Query(criteria);
            bulkOps.updateOne(query, BetRecordQueryBuilder.buildMongoUpdate(betRecordDo));
        });
        bulkOps.execute();
        long endTime = System.currentTimeMillis();
        log.info("BetRecordService#batchUpdate cost time:{}ms", endTime - startTime);
        return true;
    }

    public BetRecordDo get(Long id, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        Query query = new Query(criteria);
        BetRecordPo betRecordPo = getMongoTemplate(gameType).findOne(
                query,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        return BetRecordConverter.poToDo(betRecordPo);
    }

    @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.name + '_' + #playerId + '_'  + #id")
    public BetRecordDo getWithCache(Long id, String playerId, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        criteria.and("playerId").is(playerId);
        Query query = new Query(criteria);
        BetRecordPo betRecordPo = getMongoTemplate(gameType).findOne(
                query,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        log.info("BetRecordService#get cost time:{}", System.currentTimeMillis() - startTime);
        return BetRecordConverter.poToDo(betRecordPo);
    }

    public List<BetRecordDo> list(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<BetRecordPo> recordPoList = getMongoTemplate(gameType).find(BetRecordQueryBuilder.buildMongodbQuery(query),
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        return BetRecordConverter.poToDoList(recordPoList);
    }

    public List<Long> listId(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        Query recordQuery = BetRecordQueryBuilder.buildMongodbQuery(query);
        recordQuery.fields().include("_id");
        List<BetRecordPo> betRecordPoList = getMongoTemplate(gameType).find(
                recordQuery,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        return Optional.ofNullable(betRecordPoList).orElse(Collections.emptyList()).stream().map(BetRecordPo::getId).collect(Collectors.toList());
    }

    public List<Long> listNotExistIdList(List<Long> idList, GameTypeEnum gameType) {
        Query query = Query.query(Criteria.where("_id").in(idList));
        List<BetRecordPo> poList = getMongoTemplate(gameType).find(query, BetRecordPo.class);
        List<Long> existingIdList = Optional.ofNullable(poList).orElse(Collections.emptyList())
                .stream()
                .map(BetRecordPo::getId)
                .collect(Collectors.toList());
        return idList.stream()
                .filter(id -> !existingIdList.contains(id))
                .collect(Collectors.toList());
    }

    public long count(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return getMongoTemplate(gameType).count(BetRecordQueryBuilder.buildMongodbQuery(query),
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
    }

    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }

    ///////////////  above auto generated ///////////////
}