package com.wegame.capi.biz.service;

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

    /**
     * 新增
     *
     * @param betRecordDo 投注记录
     * @return 主键id
     */
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
            log.info("addBetRecord id:{}", item.getId());
            bulkOps.insert(item);
        });
        bulkOps.execute();
        long endTime = System.currentTimeMillis();
        log.info("BetRecordService#batchInsert cost time:{}ms", endTime - startTime);
        return true;
    }

    /**
     * 修改
     *
     * @param betRecordDo 投注记录
     * @return true/false
     */
    public boolean update(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betRecordDo.getId(), ErrorCode.ERR_PARAM, "request param \'id\' is null");
        AssertUtil.assertNotNull(betRecordDo.getPlayerId(), ErrorCode.ERR_PARAM, "request param \'playerId\' is null");
        Criteria criteria = new Criteria();
        criteria.and("_id").is(betRecordDo.getId());
        criteria.and("playerId").is(betRecordDo.getPlayerId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(gameType).updateFirst(query,
                BetRecordQueryBuilder.buildMongoUpdate(betRecordDo),
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        long endTime = System.currentTimeMillis();
        log.info("BetRecordService#update cost time:{}ms", endTime - startTime);
        return Objects.nonNull(result) && result.getModifiedCount() > 0;
    }

    /**
     * 批量新增
     *
     * @param betRecordDoList 投注记录
     * @return 主键id
     */
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

    /**
     * 查询：根据主键id查询
     *
     * @param id       主键id
     * @param playerId 玩家id
     * @return BetRecordDo 投注记录
     */
    @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.name + '_' + #playerId + '_'  + #id")
    public BetRecordDo getWithCache(Long id, String playerId, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        criteria.and("playerId").is(playerId);
        Query query = new Query(criteria);
        BetRecordPo betRecordPo = getMongoTemplate(gameType).findOne(query,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        log.info("BetRecordService#get cost time:{}", System.currentTimeMillis() - startTime);
        return BetRecordConverter.poToDo(betRecordPo);
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id       主键id
     * @param playerId 玩家id
     * @return BetRecordDo 投注记录
     */
    public BetRecordDo get(Long id, String playerId, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        criteria.and("playerId").is(playerId);
        Query query = new Query(criteria);
        BetRecordPo betRecordPo = getMongoTemplate(gameType).findOne(query,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        log.info("BetRecordService#get cost time:{}", System.currentTimeMillis() - startTime);
        return BetRecordConverter.poToDo(betRecordPo);
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id 主键id
     * @return BetRecordDo 投注记录
     */
    public BetRecordDo getById(Long id, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        Query query = new Query(criteria);
        BetRecordPo betRecordPo = getMongoTemplate(gameType).findOne(query,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        log.info("BetRecordService#get cost time:{}", System.currentTimeMillis() - startTime);
        return BetRecordConverter.poToDo(betRecordPo);
    }

    /**
     * 查询列表
     *
     * @param query 查询参数
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<BetRecordDo> list(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<BetRecordPo> recordPoList = getMongoTemplate(gameType).find(BetRecordQueryBuilder.buildMongodbQuery(query),
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        return BetRecordConverter.poToDoList(recordPoList);
    }

    /**
     * 查询Id列表
     *
     * @param query 查询参数
     * @return List<Long> 游戏记录Id列表
     */
    public List<Long> listId(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        Query recordQuery = BetRecordQueryBuilder.buildMongodbQuery(query);
        recordQuery.fields().include("_id");
        List<BetRecordPo> betRecordPoList = getMongoTemplate(gameType).find(recordQuery,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        return Optional.ofNullable(betRecordPoList).orElse(Collections.emptyList()).stream().map(BetRecordPo::getId).collect(Collectors.toList());
    }

    public List<Long> listNotExistIdList(List<Long> idList, GameTypeEnum gameType) {
        Query query = Query.query(Criteria.where("_id").in(idList));
        List<Long> existingIdList = getMongoTemplate(gameType).find(query, BetRecordPo.class)
                .stream()
                .map(BetRecordPo::getId)
                .collect(Collectors.toList());
        return idList.stream()
                .filter(id -> !existingIdList.contains(id))
                .collect(Collectors.toList());
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
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

    //    private BetRecordBatchOptResultDo buildExceptionResultDo(List<BetRecordDo> betRecordDoList, List<Long> normalFailIdList, Exception e, OptEnum optEnum) {
    //        if (e instanceof BulkOperationException) {
    //            BulkOperationException boe = (BulkOperationException) e;
    //            List<Long> dupKeyIdList = Lists.newArrayList();
    //            for (BulkWriteError error : boe.getErrors()) {
    //                // 获取每个错误对应的文档索引
    //                int index = error.getIndex();
    //                BetRecordDo betRecordDo = betRecordDoList.get(index);
    //                Long id = betRecordDo.getId();
    //                // 11000是MongoDB中唯一索引冲突的错误码
    //                if (error.getCode() == 11000) {
    //                    // 主键冲突
    //                    dupKeyIdList.add(id);
    //                } else {
    //                    normalFailIdList.add(id);
    //                }
    //            }
    //            return BetRecordBatchOptResultDo.builder()
    //                    .duplicateKeyIdList(dupKeyIdList)
    //                    .failIdList(normalFailIdList)
    //                    .successCount(OptEnum.ADD.equals(optEnum) ? (long) boe.getResult().getInsertedCount() : (long) boe.getResult().getMatchedCount())
    //                    .build();
    //        } else {
    //            return BetRecordBatchOptResultDo.builder()
    //                    .failIdList(betRecordDoList.stream().map(BetRecordDo::getId).collect(Collectors.toList()))
    //                    .successCount(0L)
    //                    .build();
    //        }
    //    }
}