package com.wegame.capi.biz.service.operation;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.operation.OperationEventQueryBuilder;
import com.wegame.capi.biz.converter.operation.OperationEventConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEffectEventCatchDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.dao.mapper.domain.operation.OperationEventPo;
import com.wegame.capi.dao.mapper.domain.operation.OperationEventQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 干预事件服务
 */
@Slf4j
@Service
public class OperationEventService extends AbstractService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public Long add(OperationEventDo eventDo, OperationEventTypeEnum gameEnum) {
        try {
            OperationEventPo po = OperationEventConvert.doToPo(eventDo, OptEnum.ADD);
            AssertUtil.assertNotNull(po, ErrorCode.ERR_INTER);
            OperationEventPo insertPo = getMongoTemplate(gameEnum.getGameType()).insert(po, gameEnum.getEventCollectionName());
            String catch_key = String.format(Constants.OPERATION_EVENT_CACHE_KEY, eventDo.getGameId(), eventDo.getPlayerName());
            redisTemplate.delete(catch_key);
            return insertPo.getId();
        } catch (DuplicateKeyException e) {
            return eventDo.getId();
        } catch (Exception e) {
            log.error("add error, errMsg: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Boolean update(OperationEventDo eventDo, OperationEventTypeEnum dynamicOperationGame) {
        Criteria criteria = new Criteria();
        criteria.and("_id").is(eventDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(dynamicOperationGame.getGameType()).updateFirst(query,
                OperationEventQueryBuilder.buildUpdateById(eventDo),
                OperationEventPo.class,
                dynamicOperationGame.getEventCollectionName());
        return result.getModifiedCount() > 0;
    }

    public List<OperationEventDo> queryList(OperationEventQuery operationQuery, OperationEventTypeEnum dynamicOperationGame) {
        List<OperationEventPo> poList = getMongoTemplate(dynamicOperationGame.getGameType()).find(OperationEventQueryBuilder.buildQuery(operationQuery), OperationEventPo.class, dynamicOperationGame.getEventCollectionName());
        return OperationEventConvert.poToDoList(poList);
    }

    public List<DynamicOperationEffectEventCatchDo> getEffectList(String playName, OperationEventTypeEnum dynamicOperationGame) {
        String catch_key = String.format(Constants.OPERATION_EVENT_CACHE_KEY, dynamicOperationGame.getGameType().getId(), playName);
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

        List<OperationEventPo> operationPoList = getMongoTemplate(dynamicOperationGame.getGameType()).find(OperationEventQueryBuilder.buildQueryEffectList(playName, dynamicOperationGame.getGameType().getId()), OperationEventPo.class, dynamicOperationGame.getEventCollectionName());
        if (CollectionUtils.isEmpty(operationPoList)) {
            Map<Long, String> catchMap = new HashMap<>();
            catchMap.put(-1L, "-1");
            this.saveCatch(catch_key, catchMap);
            return new ArrayList<>();
        }
        List<DynamicOperationEffectEventCatchDo> resultList = new ArrayList<>(entries.size());
        Map<Long, String> catchMap = new HashMap<>();
        for (OperationEventPo operationPo : operationPoList) {
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

    public OperationEventDo getById(Long id, OperationEventTypeEnum gameEnum) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        Query query = new Query(criteria);
        // todo fix @tutu
        query.fields().include("_id", "eventId", "playerName", "bonus", "minBonus", "finishBonus", "betType", "maxBonus", "eventKind", "gameId", "channelId", "effectedTime", "expiredTime", "eventStatus", "eventSubStatus", "reportStatus");
        OperationEventPo operationPo = getMongoTemplate(gameEnum.getGameType()).findOne(
                query,
                OperationEventPo.class,
                gameEnum.getEventCollectionName());
        return OperationEventConvert.poToDo(operationPo);
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
