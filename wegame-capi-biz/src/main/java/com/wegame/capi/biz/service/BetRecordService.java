package com.wegame.capi.biz.service;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.BetRecordQueryBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.reposity.domain.business.BetRecordPo;
import com.wegame.capi.dao.reposity.domain.business.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.converter.BetRecordConverter.*;

/**
 * 投注记录服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class BetRecordService extends AbstractService {

    public Long add(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        BetRecordPo po = getMongoTemplate(gameType).insert(doToPo(betRecordDo, OptEnum.ADD),
                gameType.getBetRecordCollectionName());
        return po.getId();
    }

    public boolean batchAdd(List<BetRecordDo> betRecordList, GameTypeEnum gameType) {
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_INTER);
        AssertUtil.assertNotEmpty(betRecordList, ErrorCode.ERR_PARAM, "betRecordList is empty");
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM, "gameType is null");
        List<BetRecordPo> poList = doToPoList(betRecordList, OptEnum.ADD);
        BulkOperations bulkOps = getMongoTemplate(gameType).bulkOps(BulkOperations.BulkMode.UNORDERED,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        poList.forEach(bulkOps::insert);
        bulkOps.execute();
        return true;
    }

    public boolean update(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betRecordDo.getId(), ErrorCode.ERR_PARAM, "request param \'id\' is null");
        Criteria criteria = new Criteria();
        criteria.and("_id").is(betRecordDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(gameType).updateFirst(query,
                BetRecordQueryBuilder.buildMongoUpdate(betRecordDo),
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        return Objects.nonNull(result) && result.getModifiedCount() > 0;
    }

    public int batchUpdate(List<BetRecordDo> betRecordDoList, GameTypeEnum gameType) {
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
        BulkWriteResult bulkWriteResult = bulkOps.execute();
        int modifiedCount = bulkWriteResult.getModifiedCount();
        return modifiedCount;
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
        return poToDo(betRecordPo);
    }

    // @Cacheable(value = CacheConstants.BET_RECORD, key = "#gameType.name + '_' + #playerId + '_'  + #id")
    public BetRecordDo get(Long id, String playerId, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        criteria.and("playerId").is(playerId);
        Query query = new Query(criteria);
        BetRecordPo betRecordPo = getMongoTemplate(gameType).findOne(
                query,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        if (Objects.isNull(betRecordPo)) {
            betRecordPo = getPrimaryMongoTemplate(gameType).findOne(
                    query,
                    BetRecordPo.class,
                    gameType.getBetRecordCollectionName());
            log.warn("get betRecord ret is null, then force query from primary. id: {}, playerId: {}, betRecordPo: {}", id, playerId, betRecordPo);
            if (Objects.isNull(betRecordPo)) {
                log.error("get betRecord ret is null, id: {}, playerId: {}", id, playerId);
            }
        }
        return poToDo(betRecordPo);
    }

    public List<BetRecordDo> list(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<BetRecordPo> recordPoList = getMongoTemplate(gameType).find(BetRecordQueryBuilder.buildMongodbQuery(query),
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        return poToDoList(recordPoList);
    }

    public List<Long> listId(BetRecordQuery query, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        Query recordQuery = BetRecordQueryBuilder.buildMongodbQuery(query);
        recordQuery.fields().include("_id");
        List<BetRecordPo> betRecordPoList = getMongoTemplate(gameType).find(
                recordQuery,
                BetRecordPo.class,
                gameType.getBetRecordCollectionName());
        return Optional.ofNullable(betRecordPoList)
                .orElse(Collections.emptyList())
                .stream()
                .map(BetRecordPo::getId)
                .collect(Collectors.toList());
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
}