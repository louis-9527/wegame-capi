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
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.builder.BetRecordQueryBuilder;
import com.wegame.capi.biz.converter.BetRecordConverter;
import com.wegame.capi.biz.domain.biz.BetRecordBatchResultDo;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.BetRecordPo;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
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

import java.util.*;
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

    @Autowired
    public MongoTemplate mongoTemplate;

    /**
     * 新增
     *
     * @param betRecordDo 投注记录
     * @return 主键id
     */
    public Long add(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        BetRecordPo po = mongoTemplate.insert(BetRecordConverter.doToPo(betRecordDo, OptEnum.ADD), gameType.getCollectionName());
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
    public BetRecordBatchResultDo batchAdd(List<BetRecordDo> betRecordDoList, GameTypeEnum gameType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AssertUtil.assertNotEmpty(betRecordDoList, ErrorCode.ERR_PARAM, "betRecordDoList is empty");
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        BulkWriteResult execute;
        try {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, gameType.getCollectionName());
            List<BetRecordPo> betRecordPos = BetRecordConverter.doToPoList(betRecordDoList, OptEnum.ADD);
            bulkOps.insert(betRecordPos);
            execute = bulkOps.execute();
            log.info("batchAdd result:{}", FastJsonUtils.toJSONString(execute));
        } catch (Exception e) {
            log.error("batchAdd exception", e);
            return buildExceptionResultDo(betRecordDoList, Lists.newArrayList(), e, OptEnum.ADD);
        }
        stopWatch.stop();
        log.info("BetRecordService#batchAdd cost time:{}", stopWatch.getLastTaskTimeMillis());
        return buildBatchAddResultDo(execute);
    }

    /**
     * 批量新增
     *
     * @param betRecordDoList 投注记录
     * @return 主键id
     */
    public BetRecordBatchResultDo batchUpdate(List<BetRecordDo> betRecordDoList, GameTypeEnum gameType) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AssertUtil.assertNotEmpty(betRecordDoList, ErrorCode.ERR_PARAM, "betRecordDoList is empty");
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        List<Long> normalFailIdList = Lists.newArrayList();
        List<Long> allIdList = betRecordDoList.stream().map(BetRecordDo::getId).filter(Objects::nonNull).collect(Collectors.toList());
        BulkWriteResult execute;
        try {
            // 需要排除掉库中不存在的id
            normalFailIdList = this.listNotExistIdList(allIdList, gameType);
            if (normalFailIdList.size() == allIdList.size()) {
                return BetRecordBatchResultDo.builder().successCount(0L).normalFailIdList(normalFailIdList).build();
            }

            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, gameType.getCollectionName());
            betRecordDoList.forEach(betRecordDo -> {
                Criteria criteria = new Criteria();
                criteria.and("_id").is(betRecordDo.getId());
                criteria.and("playerId").is(betRecordDo.getPlayerId());
                Query query = new Query(criteria);
                bulkOps.updateOne(query, BetRecordQueryBuilder.buildMongoUpdate(betRecordDo));
            });
            execute = bulkOps.execute();
            log.info("batchUpdate result:{}", FastJsonUtils.toJSONString(execute));
        } catch (Exception e) {
            log.error("batchUpdate error", e);
            return this.buildExceptionResultDo(betRecordDoList, normalFailIdList, e, OptEnum.UPDATE);
        }
        stopWatch.stop();
        log.info("BetRecordService#batchUpdate cost time:{}", stopWatch.getLastTaskTimeMillis());
        return this.buildBatchUpdateResultDo(execute, normalFailIdList);
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
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betRecordDo.getId(), ErrorCode.ERR_PARAM, "request param \'id\' is null");
        AssertUtil.assertNotNull(betRecordDo.getPlayerId(), ErrorCode.ERR_PARAM, "request param \'playerId\' is null");
        Criteria criteria = new Criteria();
        criteria.and("_id").is(betRecordDo.getId());
        criteria.and("playerId").is(betRecordDo.getPlayerId());
        Query query = new Query(criteria);
        UpdateResult result = mongoTemplate.updateFirst(query,
                BetRecordQueryBuilder.buildMongoUpdate(betRecordDo),
                BetRecordPo.class, gameType.getCollectionName());
        stopWatch.stop();
        log.info("BetRecordService#update cost time:{}", stopWatch.getLastTaskTimeMillis());
        // return Objects.nonNull(result) && result.getModifiedCount() > 0;
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
        BetRecordPo betRecordPo = mongoTemplate.findOne(query, BetRecordPo.class, gameType.getCollectionName());
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
        List<BetRecordPo> recordPoList = mongoTemplate.find(BetRecordQueryBuilder.buildMongodbQuery(query), BetRecordPo.class, gameType.getCollectionName());
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
        List<BetRecordPo> recordPoList = mongoTemplate.find(BetRecordQueryBuilder.buildMongodbQueryForPage(query), BetRecordPo.class, gameType.getCollectionName());
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
        return mongoTemplate.count(BetRecordQueryBuilder.buildMongodbQuery(query), BetRecordPo.class, gameType.getCollectionName());
    }

    ///////////////  above auto generated ///////////////

    private BetRecordBatchResultDo buildExceptionResultDo(List<BetRecordDo> betRecordDoList, List<Long> normalFailIdList, Exception e, OptEnum optEnum) {
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
            return BetRecordBatchResultDo.builder()
                    .dupKeyIdList(dupKeyIdList)
                    .normalFailIdList(normalFailIdList)
                    .successCount(OptEnum.ADD.equals(optEnum) ? (long) boe.getResult().getInsertedCount() : (long) boe.getResult().getMatchedCount())
                    .build();
        } else {
            return BetRecordBatchResultDo.builder()
                    .normalFailIdList(betRecordDoList.stream().map(BetRecordDo::getId)
                            .collect(Collectors.toList()))
                    .successCount(0L)
                    .build();
        }
    }

    private List<Long> listNotExistIdList(List<Long> allIdList, GameTypeEnum gameType) {
        List<Long> existIdList = mongoTemplate.findDistinct(new Query(Criteria.where("_id").in(allIdList)), "_id", gameType.getCollectionName(), Long.class);
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

    private BetRecordBatchResultDo buildBatchAddResultDo(BulkWriteResult result) {
        return BetRecordBatchResultDo.builder()
                .successCount((long) result.getInsertedCount())
                .build();
    }

    private BetRecordBatchResultDo buildBatchUpdateResultDo(BulkWriteResult result, List<Long> normalFailIdList) {
        return BetRecordBatchResultDo.builder()
                .successCount((long) result.getMatchedCount())
                .normalFailIdList(normalFailIdList)
                .build();
    }
}