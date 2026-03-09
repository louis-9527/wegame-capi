package com.wegame.capi.biz.service.bizinterfere;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.constants.BizInterfereConstants;
import com.wegame.capi.common.enums.BizInterfereSubTypeEnum;
import com.wegame.capi.common.enums.BizInterfereTypeEnum;
import com.wegame.capi.infrastructure.config.ThreadPoolConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 业务干预服务
 */
@Slf4j
@Service
public class BizInterfereService {
    private static final String DECR_ONE_REWARD_SCRIPT_STR = "" +
            "local key = KEYS[1] " +
            "local field = ARGV[1] " +
            "local incr_value = tonumber(ARGV[2]) " +
            "local current_value = redis.call('HGET', key, field) " +
            "if not current_value then current_value = 0 " +
            "else " +
            "current_value = tonumber(current_value) end " +
            "if current_value >= incr_value " +
            "then return redis.call('HINCRBY', key, field, -incr_value) " +
            "else return -1 " +
            "end";
    private final DefaultRedisScript<Long> DECR_ONE_REWARD_SCRIPT = new DefaultRedisScript<>(DECR_ONE_REWARD_SCRIPT_STR, Long.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Async(ThreadPoolConfig.bizInterfereThreadPoolExecutor)
    public Boolean asyncUpdateBizInterfere(GameTypeEnum gameType, BizInterfereTypeEnum bizInterfereType, Map<Integer, Long> bizInterfereSubTypeIncrMap) {
        // 管道实现
        String hashKey = BizInterfereConstants.buildBizInterfereRedisHashKey(gameType, bizInterfereType);
        redisTemplate.executePipelined((RedisCallback<Long>) connection -> {
            try {
                bizInterfereSubTypeIncrMap.forEach((bizInterfereSubType, incrValue) -> {
                    connection.hIncrBy(hashKey.getBytes(), bizInterfereSubType.toString().getBytes(), incrValue);
                });
            } catch (Exception e) {
                log.error("BizInterfereService#asyncUpdateBizInterfere,error,gameType:{},bizInterfereType:{},bizInterfereSubTypeIncrMap:{}"
                        , gameType.getId(), bizInterfereType.getCode(), bizInterfereSubTypeIncrMap, e);
            }
            // must be null
            return null;
        });
        return true;
    }

    public Map<BizInterfereSubTypeEnum, Long> getRewardInfoMap(GameTypeEnum gameType, BizInterfereTypeEnum bizInterfereType) {
        Map<Object, Object> entries;
        try {
            entries = redisTemplate.opsForHash().entries(BizInterfereConstants.buildBizInterfereRedisHashKey(gameType, bizInterfereType));
        } catch (Exception e) {
            log.error("BizInterfereService#getRewardInfoMap,error,gameType:{},bizInterfereType:{}"
                    , gameType.getId(), bizInterfereType.getCode(), e);
            return null;
        }
        return entries.entrySet().stream()
                .collect(Collectors.toMap(entry -> {
                    int subTypeCode = Integer.parseInt(entry.getKey().toString());
                    BizInterfereSubTypeEnum bizInterfereSubTypeEnum = BizInterfereSubTypeEnum.valueOfCode(subTypeCode);
                    AssertUtil.assertNotNull(bizInterfereSubTypeEnum, ErrorCode.ERR_INTER, "bizInterfereSubTypeEnum is null");
                    return bizInterfereSubTypeEnum;
                }, entry -> Long.parseLong(entry.getValue().toString())));
    }

    public Boolean decrOneReward(GameTypeEnum gameType, BizInterfereTypeEnum bizInterfereType, BizInterfereSubTypeEnum bizInterfereSubType, long deductValue) {
        // lua实现原子话。如果金额不够,则返回失败
        String key = BizInterfereConstants.buildBizInterfereRedisHashKey(gameType, bizInterfereType);
        Integer filed = bizInterfereSubType.getCode();
        Long result;
        try {
            result = redisTemplate.execute(DECR_ONE_REWARD_SCRIPT, Collections.singletonList(key), filed, deductValue);
        } catch (Exception e) {
            log.error("decrOneReward error, gameType:{}, bizInterfereType:{}, bizInterfereSubType:{}, deductValue:{}"
                    , gameType.getId(), bizInterfereType.getCode(), bizInterfereSubType.getCode(), deductValue, e);
            return false;
        }
        boolean ret = Optional.ofNullable(result).map(r -> r > -1).orElse(false);
        // add log
        if (ret) {
            log.info("decrOneReward success, gameType:{}, bizInterfereType:{}, bizInterfereSubType:{}, deductValue:{}, result: {}"
                    , gameType.getId(), bizInterfereType.getCode(), bizInterfereSubType.getCode(), deductValue, result);
        } else {
            log.warn("decrOneReward fail, gameType:{}, bizInterfereType:{}, bizInterfereSubType:{}, deductValue:{}, result: {}"
                    , gameType.getId(), bizInterfereType.getCode(), bizInterfereSubType.getCode(), deductValue, result);
        }
        return ret;
    }
}
