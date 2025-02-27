package com.wegame.capi.biz.service;

import com.mongodb.MongoWriteException;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.DynamicOperationQueryBuilder;
import com.wegame.capi.biz.converter.DynamicOperationConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.client.enums.DynamicOperationGameEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationPo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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


    public Long add(DynamicOperationEventDo dynamicOperationMsgDo, GameTypeEnum gameType) {
        dynamicOperationMsgDo.setId(snowflakeIdGenerator.nextId());
        DynamicOperationPo dynamicOperationPo = DynamicOperationConvert.convertPo(dynamicOperationMsgDo, OptEnum.ADD);
        if (Objects.isNull(dynamicOperationPo)) {
            return null;
        }
        try {
            DynamicOperationGameEnum dynamicOperationGameEnum = DynamicOperationGameEnum.valueOfGameType(gameType);
            DynamicOperationPo insert = getMongoTemplate(gameType).insert(dynamicOperationPo, dynamicOperationGameEnum.getEventCollectionName());
            String key = String.format(Constants.DYNAMIC_OPERATION_EVENT_EXIST_KEY, gameType.getId(), dynamicOperationMsgDo.getPlayerName());
            // 缓存事件信息
            redisTemplate.opsForHash().put(key, String.valueOf(insert.getId()), dynamicOperationMsgDo.getEffectedTime() + "_" + dynamicOperationMsgDo.getExpiredTime());
            return insert.getId();

        } catch (MongoWriteException error) {
            if (error.getCode() == 11000) {
                return null;
            }
            throw error;
        }

    }

    public Boolean update(DynamicOperationEventDo eventDo, GameTypeEnum gameType) {
        DynamicOperationGameEnum dynamicOperationGameEnum = DynamicOperationGameEnum.valueOfGameType(gameType);

        Criteria criteria = new Criteria();
        criteria.and("_id").is(eventDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(gameType).updateFirst(query,
                DynamicOperationQueryBuilder.buildUpdateById(eventDo),
                DynamicOperationPo.class,
                dynamicOperationGameEnum.getEventCollectionName());
        if (result.getModifiedCount() > 0) {
            if (eventDo.getEventStatus() != null && (eventDo.getEventStatus() == DynamicOperationEventStatusEnum.FINISH_NORMAL || eventDo.getEventStatus() == DynamicOperationEventStatusEnum.FINISH_EXPIRED)) {
                String key = String.format(Constants.DYNAMIC_OPERATION_EVENT_EXIST_KEY, gameType.getId(), eventDo.getPlayerName());
                redisTemplate.opsForHash().delete(key, String.valueOf(eventDo.getId()));
                return true;
            }
        }
        return false;

    }

    public List<DynamicOperationEventDo> queryList(DynamicOperationQuery operationQuery, GameTypeEnum gameType) {
        DynamicOperationGameEnum dynamicOperationGameEnum = DynamicOperationGameEnum.valueOfGameType(gameType);

        List<DynamicOperationPo> operationPoList = getMongoTemplate(gameType).find(DynamicOperationQueryBuilder.buildQuery(operationQuery), DynamicOperationPo.class, dynamicOperationGameEnum.getEventCollectionName());
        // 判断干预金额
        return DynamicOperationConvert.convertDos(operationPoList);
    }


    /**
     * 获取当前玩家在生效中的干预时间,并按时间取第一条
     *
     * @return
     */
    public List<DynamicOperationEventDo> getBuyUserNameAndTime(DynamicOperationQuery operationQuery, GameTypeEnum gameType) {
        DynamicOperationGameEnum dynamicOperationGameEnum = DynamicOperationGameEnum.valueOfGameType(gameType);
        List<DynamicOperationPo> operationPoList = getMongoTemplate(gameType).find(DynamicOperationQueryBuilder.buildQueryBuyUserNameAndTime(operationQuery), DynamicOperationPo.class, dynamicOperationGameEnum.getEventCollectionName());
        // 判断干预金额
        return DynamicOperationConvert.convertDos(operationPoList);
    }

    public DynamicOperationEventDo getById(Long id, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        DynamicOperationGameEnum dynamicOperationGameEnum = DynamicOperationGameEnum.valueOfGameType(gameType);

        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        Query query = new Query(criteria);
        query.fields().include("_id", "eventId", "playerName", "bonus", "eventKind", "operatorGameId", "channelId", "effectedTime", "expiredTime", "eventStatus", "reportStatus");
        DynamicOperationPo operationPo = getMongoTemplate(gameType).findOne(
                query,
                DynamicOperationPo.class,
                dynamicOperationGameEnum.getEventCollectionName());
        return DynamicOperationConvert.convertDo(operationPo);
    }


    public DynamicOperationEventStatusEnum getEventStatus(Long bonus, Long use) {
        long lowerBound = (long) (bonus * 0.9);
        if (use >= lowerBound) {
            return DynamicOperationEventStatusEnum.FINISH_NORMAL;
        }
        return DynamicOperationEventStatusEnum.NONE;
    }


    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }


}
