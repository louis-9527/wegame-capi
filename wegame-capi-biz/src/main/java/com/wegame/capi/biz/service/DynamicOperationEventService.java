package com.wegame.capi.biz.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoWriteException;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.DynamicOperationQueryBuilder;
import com.wegame.capi.biz.converter.DynamicOperationConvert;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.game.GameEventDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEffectEventCatchDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.DynamicOperationGameEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationPo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DynamicOperationEventService {

    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private Map<String, MongoTemplate> mongoTemplateMap;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public Long add(DynamicOperationEventDo dynamicOperationMsgDo, DynamicOperationGameEnum dynamicOperationGame) {
        dynamicOperationMsgDo.setId(snowflakeIdGenerator.nextId());
        DynamicOperationPo dynamicOperationPo = DynamicOperationConvert.convertPo(dynamicOperationMsgDo, OptEnum.ADD);
        if (Objects.isNull(dynamicOperationPo)) {
            return null;
        }
        try {
            DynamicOperationPo insert = getMongoTemplate(dynamicOperationGame.getGameType()).insert(dynamicOperationPo, dynamicOperationGame.getEventCollectionName());
            String catch_key = String.format(Constants.DYNAMIC_OPERATION_EVENT_CATCH_KEY, dynamicOperationMsgDo.getGameId(), dynamicOperationMsgDo.getPlayerName());
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
    public List<DynamicOperationEffectEventCatchDo> getEffectList(String playName, DynamicOperationGameEnum dynamicOperationGame) {
        String catch_key = String.format(Constants.DYNAMIC_OPERATION_EVENT_CATCH_KEY, dynamicOperationGame.getGameType().getId(), playName);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(catch_key);
        if (!entries.isEmpty()) {
            if (entries.containsKey("-1")) {
                return new ArrayList<>();
            }
            List<DynamicOperationEffectEventCatchDo> resultList = new ArrayList<>(entries.size());
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                resultList.add(JSON.parseObject((String) entry.getValue(), DynamicOperationEffectEventCatchDo.class));
            }
            return resultList.stream()
                    .sorted(Comparator.comparing(DynamicOperationEffectEventCatchDo::getEffectedTime))
                    .collect(Collectors.toList());
        }

        List<DynamicOperationPo> operationPoList = getMongoTemplate(dynamicOperationGame.getGameType()).find(DynamicOperationQueryBuilder.buildQueryEffectList(playName, dynamicOperationGame.getGameType().getId()), DynamicOperationPo.class, dynamicOperationGame.getEventCollectionName());
        if (CollectionUtils.isEmpty(operationPoList)) {
            Map<Long, String> catchMap = new HashMap<>();
            catchMap.put(-1L, "-1");
            this.saveCatch(catch_key, catchMap);
            return new ArrayList<>();
        }
        List<DynamicOperationEffectEventCatchDo> resultList = new ArrayList<>(entries.size());
        Map<Long, String> catchMap = new HashMap<>();
        for (DynamicOperationPo operationPo : operationPoList) {
            DynamicOperationEffectEventCatchDo build = DynamicOperationEffectEventCatchDo.builder()
                    .id(operationPo.getId())
                    .effectedTime(operationPo.getEffectedTime())
                    .expiredTime(operationPo.getExpiredTime()).build();
            resultList.add(build);
            catchMap.put(operationPo.getId(), JSON.toJSONString(build));
        }
        this.saveCatch(catch_key, catchMap);
        return resultList.stream()
                .sorted(Comparator.comparing(DynamicOperationEffectEventCatchDo::getEffectedTime))
                .collect(Collectors.toList());
    }

    public DynamicOperationEventDo getById(Long id, DynamicOperationGameEnum dynamicOperationGame) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);

        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        Query query = new Query(criteria);
        query.fields().include("_id", "eventId", "playerName", "bonus", "minBonus", "finishBonus", "betType", "maxBonus", "eventKind", "gameId", "channelId", "effectedTime", "expiredTime", "eventStatus", "eventSubStatus", "reportStatus");
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


    private void saveCatch(String catch_key, Map<Long, String> catchMap) {
        // 构造 Lua 脚本
        String luaScript =
                "for i = 1, #ARGV - 1, 2 do " +
                        "redis.call('HSET', KEYS[1], tostring(ARGV[i]), tostring(ARGV[i+1])) " +
                        "end " +
                        "redis.call('EXPIRE', KEYS[1], tonumber(ARGV[#ARGV]))";
        List<Object> args = new ArrayList<>();
        for (Map.Entry<Long, String> entry : catchMap.entrySet()) {
            args.add(entry.getKey());
            args.add(entry.getValue());
        }
        args.add(24 * 60 * 60); // 24小时转换成秒
        redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, Long.class),
                Collections.singletonList(catch_key),
                args.toArray() // 关键：转换为 String[]
        );

    }


}
