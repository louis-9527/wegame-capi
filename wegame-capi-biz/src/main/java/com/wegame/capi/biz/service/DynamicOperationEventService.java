package com.wegame.capi.biz.service;

import com.google.common.collect.Lists;
import com.mongodb.MongoWriteException;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.DynamicOperationQueryBuilder;
import com.wegame.capi.biz.converter.DynamicOperationConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.DynamicOperationGameEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.DynamicOperationEventKindEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationPo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class DynamicOperationEventService {

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private Map<String, MongoTemplate> mongoTemplateMap;
    @Autowired
    private RedisTemplate redisTemplate;


    public Long add(DynamicOperationEventDo dynamicOperationMsgDo, DynamicOperationGameEnum dynamicOperationGame) {
        dynamicOperationMsgDo.setId(snowflakeIdGenerator.nextId());
        DynamicOperationPo dynamicOperationPo = DynamicOperationConvert.convertPo(dynamicOperationMsgDo, OptEnum.ADD);
        if (Objects.isNull(dynamicOperationPo)) {
            return null;
        }
        try {
            DynamicOperationPo insert = getMongoTemplate(dynamicOperationGame.getGameType()).insert(dynamicOperationPo, dynamicOperationGame.getEventCollectionName());
            String catch_key = String.format(Constants.DYNAMIC_OPERATION_EVENT_EXIST_KEY, dynamicOperationMsgDo.getGameId(), dynamicOperationMsgDo.getPlayerName(), dynamicOperationMsgDo.getBetType().getCode());
            redisTemplate.delete(catch_key);
            return insert.getId();
        } catch (MongoWriteException error) {
            if (error.getCode() == 11000) {
                return null;
            }
            throw error;
        }

    }

    public Boolean update(DynamicOperationEventDo eventDo, DynamicOperationGameEnum dynamicOperationGame) {

        Criteria criteria = new Criteria();
        criteria.and("_id").is(eventDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(dynamicOperationGame.getGameType()).updateFirst(query,
                DynamicOperationQueryBuilder.buildUpdateById(eventDo),
                DynamicOperationPo.class,
                dynamicOperationGame.getEventCollectionName());
        return result.getModifiedCount() > 0;

    }

    public List<DynamicOperationEventDo> queryList(DynamicOperationQuery operationQuery, DynamicOperationGameEnum dynamicOperationGame) {

        List<DynamicOperationPo> operationPoList = getMongoTemplate(dynamicOperationGame.getGameType()).find(DynamicOperationQueryBuilder.buildQuery(operationQuery), DynamicOperationPo.class, dynamicOperationGame.getEventCollectionName());
        // 判断干预金额
        return DynamicOperationConvert.convertDos(operationPoList);
    }


    /**
     * 返回干预事件
     * <id,pair<生效时间、过期时间></>></>
     *
     * @return
     */
    public Map<Long, Pair<Long, Long>> getEffectList(String playName, BetTypeEnum betType, DynamicOperationGameEnum dynamicOperationGame) {
        String catch_key = String.format(Constants.DYNAMIC_OPERATION_EVENT_EXIST_KEY, dynamicOperationGame.getGameType().getId(), playName, betType.getCode());
        Map<String, String> entries = redisTemplate.opsForHash().entries(catch_key);
        if (!entries.isEmpty()) {
            if (entries.containsKey("-1")) {
                return new HashMap<>();
            }
            Map<Long, Pair<Long, Long>> resultMap = new HashMap<>();
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                String[] timeArray = entry.getValue().split("_");
                resultMap.put(Long.valueOf(entry.getKey()), Pair.of(Long.valueOf(timeArray[0]), Long.valueOf(timeArray[1])));
            }
            return resultMap;
        }
        List<DynamicOperationPo> operationPoList = getMongoTemplate(dynamicOperationGame.getGameType()).find(DynamicOperationQueryBuilder.buildQueryEffectList(playName, dynamicOperationGame.getGameType().getId(), betType), DynamicOperationPo.class, dynamicOperationGame.getEventCollectionName());
        if (CollectionUtils.isEmpty(operationPoList)) {
            redisTemplate.opsForHash().put(catch_key, "-1", null);
            return new HashMap<>();
        }
        Map<Long, Pair<Long, Long>> resultMap = new HashMap<>();
        Map<String, String> catchMap = new HashMap<>();
        for (DynamicOperationPo operationPo : operationPoList) {
            resultMap.put(operationPo.getId(), Pair.of(operationPo.getEffectedTime(), operationPo.getExpiredTime()));
            catchMap.put(String.valueOf(operationPo.getId()), operationPo.getEffectedTime() + "_" + operationPo.getExpiredTime());
        }
        redisTemplate.opsForHash().putAll(catch_key, catchMap);
        return resultMap;
    }

    public DynamicOperationEventDo getById(Long id, DynamicOperationGameEnum dynamicOperationGame) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);

        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        Query query = new Query(criteria);
        query.fields().include("_id", "eventId", "playerName", "bonus", "minBonus", "finishBonus", "maxBonus", "eventKind", "gameId", "channelId", "effectedTime", "expiredTime", "eventStatus", "reportStatus");
        DynamicOperationPo operationPo = getMongoTemplate(dynamicOperationGame.getGameType()).findOne(
                query,
                DynamicOperationPo.class,
                dynamicOperationGame.getEventCollectionName());
        return DynamicOperationConvert.convertDo(operationPo);
    }



    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }


}
