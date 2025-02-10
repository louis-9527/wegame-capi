package com.wegame.capi.biz.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordQueryBuilder;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.domain.biz.BetRecordBatchOptResultDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.updateBetRecordThreadPoolExecutor;

/**
 * 投注记录服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class BetRecordService {


    //    @Autowired
    //    public MongoTemplate mongoTemplate;

    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;

    /**
     * 新增
     *
     * @param betRecordDo 投注记录
     * @return 主键id
     */
    public Long add(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        BetRecordPo po = getMongoTemplate(gameType).insert(BetRecordConverter.doToPo(betRecordDo, OptEnum.ADD), gameType.getCollectionName());
        stopWatch.stop();
        log.info("BetRecordService#add cost time:{}", stopWatch.getLastTaskTimeMillis());
        return po.getId();
    }

    /**
     * 批量新增
     *
     * @param betRecordDoList 投注记录
     * @return 主键id
     */
    public BetRecordBatchOptResultDo batchAdd(List<BetRecordDo> betRecordDoList, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotEmpty(betRecordDoList, ErrorCode.ERR_PARAM, "betRecordDoList is empty");
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        BulkWriteResult bulkWriteResult;
        try {
            BulkOperations bulkOps = getMongoTemplate(gameType).bulkOps(BulkOperations.BulkMode.UNORDERED, gameType.getCollectionName());
            List<BetRecordPo> betRecordPos = BetRecordConverter.doToPoList(betRecordDoList, OptEnum.ADD);
            bulkOps.insert(betRecordPos);
            bulkWriteResult = bulkOps.execute();
        } catch (Exception e) {
            log.error("BetRecordService#batchAdd exception", e);
            return buildExceptionResultDo(betRecordDoList, Lists.newArrayList(), e, OptEnum.ADD);
        }
        long endTime = System.currentTimeMillis();
        log.info("BetRecordService#batchAdd cost time:{}ms", endTime - startTime);
        return BetRecordBatchOptResultDo.builder()
                .successCount(bulkWriteResult.getInsertedCount())
                .build();
    }

    /**
     * 修改
     *
     * @param betRecordDo 投注记录
     * @return true/false
     */
    @Async(updateBetRecordThreadPoolExecutor)
    @CacheEvict(value = CacheConstants.BET_RECORD, key = "#gameType.collectionName +'-'+ #betRecordDo.id")
    public void asyncUpdate(BetRecordDo betRecordDo, GameTypeEnum gameType) {
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
                BetRecordPo.class, gameType.getCollectionName());
        long endTime = System.currentTimeMillis();
        log.info("BetRecordService#update cost time:{}ms", endTime - startTime);
        AssertUtil.assertTrue(Objects.nonNull(result) && result.getModifiedCount() > 0, ErrorCode.ERR_INTER);
    }

    /**
     * 批量新增
     *
     * @param betRecordDoList 投注记录
     * @return 主键id
     */
    public BetRecordBatchOptResultDo batchUpdate(List<BetRecordDo> betRecordDoList, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotEmpty(betRecordDoList, ErrorCode.ERR_PARAM, "betRecordDoList is empty");
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        List<Long> failIdList = Lists.newArrayList();
        List<Long> allIdList = betRecordDoList.stream().map(BetRecordDo::getId).filter(Objects::nonNull).collect(Collectors.toList());
        BulkWriteResult bulkWriteResult;
        try {
            // 需要排除掉库中不存在的id  todo 是否可以不需要这部操作
            failIdList = listNotExistIdList(allIdList, gameType);
            if (failIdList.size() == allIdList.size()) {
                return BetRecordBatchOptResultDo.builder().successCount(0L).failIdList(failIdList).build();
            }

            BulkOperations bulkOps = getMongoTemplate(gameType).bulkOps(BulkOperations.BulkMode.UNORDERED, gameType.getCollectionName());
            betRecordDoList.forEach(betRecordDo -> {
                Criteria criteria = new Criteria();
                criteria.and("_id").is(betRecordDo.getId());
                criteria.and("playerId").is(betRecordDo.getPlayerId());
                Query query = new Query(criteria);
                bulkOps.updateOne(query, BetRecordQueryBuilder.buildMongoUpdate(betRecordDo));
            });
            bulkWriteResult = bulkOps.execute();
        } catch (Exception e) {
            log.error("BetRecordService#batchUpdate error, errMsg: {}", e.getMessage(), e);
            return buildExceptionResultDo(betRecordDoList, failIdList, e, OptEnum.UPDATE);
        }
        long endTime = System.currentTimeMillis();
        log.info("BetRecordService#batchUpdate cost time:{}ms", endTime - startTime);
        return BetRecordBatchOptResultDo.builder()
                .successCount(bulkWriteResult.getMatchedCount())
                .failIdList(failIdList)
                .build();
    }

    /**
     * 查询：根据主键id查询
     *
     * @param id       主键id
     * @param playerId 玩家id
     * @return BetRecordDo 投注记录
     */
    @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.collectionName + '_' + #playerId + '_'  + #id")
    public <T> BetRecordDo<T> getSimpleWithCache(Long id, String playerId, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        criteria.and("playerId").is(playerId);
        Query query = new Query(criteria);
        BetRecordPo betRecordPo = getMongoTemplate(gameType).findOne(query, BetRecordPo.class, gameType.getCollectionName());
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
    @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.collectionName + '_' + #playerId + '_'  + #id")
    public <T> BetRecordDo<T> get(Long id, String playerId, GameTypeEnum gameType) {
        long startTime = System.currentTimeMillis();
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        criteria.and("playerId").is(playerId);
        Query query = new Query(criteria);
        BetRecordPo betRecordPo = getMongoTemplate(gameType).findOne(query, BetRecordPo.class, gameType.getCollectionName());
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
        List<BetRecordPo> recordPoList = getMongoTemplate(gameType).find(BetRecordQueryBuilder.buildMongodbQuery(query), BetRecordPo.class, gameType.getCollectionName());
        return BetRecordConverter.poToDoList(recordPoList);
    }

    /**
     * 分页查询列表
     *
     * @param query 查询参数
     * @return List<BetRecordDo> 游戏记录列表
     */
    public List<BetRecordDo> listForPage(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<BetRecordPo> recordPoList = getMongoTemplate(gameType).find(BetRecordQueryBuilder.buildMongodbQueryForPage(query), BetRecordPo.class, gameType.getCollectionName());
        return BetRecordConverter.poToDoList(recordPoList);
    }

    /**
     * 数量统计
     *
     * @param query 查询参数
     * @return 数量
     */
    public long count(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        return getMongoTemplate(gameType).count(BetRecordQueryBuilder.buildMongodbQuery(query), BetRecordPo.class, gameType.getCollectionName());
    }

    ///////////////  above auto generated ///////////////

    private List<Long> listNotExistIdList(List<Long> allIdList, GameTypeEnum gameType) {
        List<Long> existIdList = getMongoTemplate(gameType).findDistinct(new Query(Criteria.where("_id").in(allIdList)), "_id", gameType.getCollectionName(), Long.class);
        if (CollectionUtil.isEmpty(existIdList)) {
            return allIdList;
        }

        List<Long> normalFailIdList = Lists.newArrayList();
        HashSet<Long> allIdSet = Sets.newHashSet(allIdList);
        HashSet<Long> existIdSet = Sets.newHashSet(existIdList);
        if (existIdSet.size() != allIdSet.size()) {
            normalFailIdList.addAll(Sets.difference(allIdSet, existIdSet));
        }
        return normalFailIdList;
    }

    private BetRecordBatchOptResultDo buildExceptionResultDo(List<BetRecordDo> betRecordDoList, List<Long> normalFailIdList, Exception e, OptEnum optEnum) {
        if (e instanceof BulkOperationException) {
            BulkOperationException boe = (BulkOperationException) e;
            List<Long> dupKeyIdList = Lists.newArrayList();
            for (BulkWriteError error : boe.getErrors()) {
                // 获取每个错误对应的文档索引
                int index = error.getIndex();
                BetRecordDo betRecordDo = betRecordDoList.get(index);
                Long id = betRecordDo.getId();
                // 11000是MongoDB中唯一索引冲突的错误码
                if (error.getCode() == 11000) {
                    // 主键冲突
                    dupKeyIdList.add(id);
                } else {
                    normalFailIdList.add(id);
                }
            }
            return BetRecordBatchOptResultDo.builder()
                    .duplicateKeyIdList(dupKeyIdList)
                    .failIdList(normalFailIdList)
                    .successCount(OptEnum.ADD.equals(optEnum) ? (long) boe.getResult().getInsertedCount() : (long) boe.getResult().getMatchedCount())
                    .build();
        } else {
            return BetRecordBatchOptResultDo.builder()
                    .failIdList(betRecordDoList.stream().map(BetRecordDo::getId).collect(Collectors.toList()))
                    .successCount(0L)
                    .build();
        }
    }

    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }
}