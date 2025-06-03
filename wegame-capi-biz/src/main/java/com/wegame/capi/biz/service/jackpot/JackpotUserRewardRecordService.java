package com.wegame.capi.biz.service.jackpot;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.CacheConstants;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordTypesQuery;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.builder.jackpot.JackpotUserRewardRecordQueryBuilder.*;
import static com.wegame.capi.biz.converter.jackpot.JackpotUserRewardRecordConverter.*;

/**
 * Jackpot用户中奖记录服务
 *
 * @author genereate
 */
@Service
public class JackpotUserRewardRecordService extends AbstractService {

    public JackpotUserRewardRecordDo get(Long id, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        Query query = new Query(criteria);
        JackpotUserRewardRecordPo po = getMongoTemplate(jackpotType.getGameType()).findOne(
                query,
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return poToDo(po);
    }

    public JackpotUserRewardRecordDo selectOne(JackpotUserRewardRecordQuery query, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo po = getMongoTemplate(jackpotType.getGameType()).findOne(
                buildMongodbQuery(query),
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return poToDo(po);
    }

    public List<Long> listId(JackpotUserRewardRecordQuery query, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM);
        Query recordQuery = buildMongodbQuery(query);
        recordQuery.fields().include("_id");
        List<JackpotUserRewardRecordPo> poList = getMongoTemplate(jackpotType.getGameType()).find(
                recordQuery,
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return Optional.ofNullable(poList)
                .orElse(Collections.emptyList())
                .stream()
                .map(JackpotUserRewardRecordPo::getId)
                .collect(Collectors.toList());
    }

    public List<JackpotUserRewardRecordDo> list(JackpotUserRewardRecordQuery query, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<JackpotUserRewardRecordPo> poList = getMongoTemplate(jackpotType.getGameType()).find(buildMongodbQuery(query),
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return poToDoList(poList);
    }


    /**
     * 根据奖池类型返回固定条数的列表
     * @param query
     * @param jackpotType
     * @return
     */
    @Cacheable(value = CacheConstants.JACKPOT_USER_REWARD_RECORD_LIST,
            key = "#query.getCacheKey() + ':' + #jackpotType.gameType.id")
    public List<JackpotUserRewardRecordDo> listByTypes(JackpotUserRewardRecordTypesQuery query, JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(query, ErrorCode.ERR_PARAM);
        List<JackpotUserRewardRecordPo> recordPoList = getMongoTemplate(jackpotType.getGameType())
                .find(buildMongodbQuery(query),
                        JackpotUserRewardRecordPo.class,
                        jackpotType.getJackpotUserRewardRewardCollectionName());
        return poToDoList(recordPoList);
    }

    @Cacheable(value = CacheConstants.JACKPOT_USER_REWARD_HIGHEST_RECORD, key = "#jackpotType.gameType.id")
    public JackpotUserRewardRecordDo getHighestUserRewardRecord(JackpotTypeEnum jackpotType) {
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo maxRecord = getMongoTemplate(jackpotType.getGameType()).findOne(buildHighestRecordQuery(),
                JackpotUserRewardRecordPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return poToDo(maxRecord);
    }

    @CacheEvict(value = CacheConstants.JACKPOT_USER_REWARD_HIGHEST_RECORD, key = "#jackpotType.gameType.id")
    public void evictHighestUserRewardRecordCache(JackpotTypeEnum jackpotType) {
        // do nothing
    }

    public Long add(JackpotTypeEnum jackpotType, JackpotUserRewardRecordDo recordDo) {
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo jackpotUserRewardRecordPo = doToPo(recordDo, OptEnum.ADD);
        AssertUtil.assertNotNull(jackpotUserRewardRecordPo, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo insert = getMongoTemplate(jackpotType.getGameType()).insert(jackpotUserRewardRecordPo,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return insert.getId();
    }

    public Boolean update(JackpotTypeEnum jackpotType, JackpotUserRewardRecordDo recordDo) {
        AssertUtil.assertNotNull(jackpotType, ErrorCode.ERR_PARAM);
        JackpotUserRewardRecordPo po = doToPo(recordDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_PARAM);
        Query query = buildMongodbQuery(JackpotUserRewardRecordQuery.builder().id(recordDo.getId()).build());
        UpdateResult result = getMongoTemplate(jackpotType.getGameType()).updateFirst(query,
                buildUpdate(po),
                JackpotRewardPoolPo.class,
                jackpotType.getJackpotUserRewardRewardCollectionName());
        return result.getModifiedCount() > 0;
    }
}