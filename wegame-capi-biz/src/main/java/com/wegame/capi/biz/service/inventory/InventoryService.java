package com.wegame.capi.biz.service.inventory;

import com.alibaba.fastjson.TypeReference;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.InventoryTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.client.RedisTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.inventoryThreadPoolExecutor;

/**
 * 库存服务
 */
@Slf4j
@Component
public class InventoryService {

    private static final String DEDUCT_INVENTORY_LUA_SCRIPT = "" +
            "local key = KEYS[1] \n" +
            "local field = ARGV[1] \n" +
            "local incr_value = tonumber(ARGV[2]) \n" +
            "if not incr_value then return -2 end \n" +
            "local current_value = redis.call('HGET', key, field) \n" +
            "if not current_value then \n" +
            "    current_value = 0 \n" +
            "else \n" +
            "    current_value = tonumber(current_value) \n" +
            "    if not current_value then return -2 end \n" +
            "end \n" +
            "if current_value >= incr_value then \n" +
            "    return redis.call('HINCRBY', key, field, -incr_value) \n" +
            "else \n" +
            "    return -1 \n" +
            "end";

    private static final List<GameTypeEnum> SUPPORT_GAME_TYPE_LIST = Arrays.asList(GameTypeEnum.MALAKAS);
    // wegame:capi:inventory:{gameId}:{poolName}
    private static final String INVENTORY_KEY_FORMAT = "wegame:capi:inventory:%s:%s";
    // 初始化RedisScript对象（指定返回值类型为Long）
    private static final RedisScript<Long> DECR_SCRIPT = new DefaultRedisScript<>(DEDUCT_INVENTORY_LUA_SCRIPT, Long.class);

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public boolean deductInventory(GameTypeEnum gameType, String poolName, InventoryTypeEnum inventoryType, long deductInventoryAmount) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(SUPPORT_GAME_TYPE_LIST.contains(gameType), ErrorCode.ERR_INTER);
        AssertUtil.assertStrNotEmpty(poolName, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(inventoryType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(deductInventoryAmount > 0, ErrorCode.ERR_INTER);
        String key = key(gameType, poolName);
        Integer filed = inventoryType.getCode();
        try {
            Long newInventory = redisTemplate.execute(DECR_SCRIPT, Collections.singletonList(key), filed, deductInventoryAmount);
            boolean ret = Optional.of(newInventory).map(r -> r > -1).orElse(false);
            if (ret) {
                log.info("deductInventory success, key: {}, inventoryType: {}, deductInventoryAmount: {}", key, inventoryType, deductInventoryAmount);
            } else {
                log.warn("deductInventory fail, key: {}, inventoryType: {}, deductInventoryAmount: {}", key, inventoryType, deductInventoryAmount);
            }
            return ret;
        } catch (RedisTimeoutException e) {
            log.error("deductInventory error, found RedisTimeoutException, key: {}, inventoryType: {}, deductInventoryAmount: {}", key, inventoryType, deductInventoryAmount, e);
            return false;
        } catch (Exception e) {
            log.error("deductInventory error, found Exception, key: {}, inventoryType: {}, deductInventoryAmount: {}", key, inventoryType, deductInventoryAmount, e);
            return false;
        }
    }

    @Async(inventoryThreadPoolExecutor)
    public void increaseInventory(GameTypeEnum gameType, String poolName, InventoryTypeEnum inventoryType, long increaseInventoryAmount) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(SUPPORT_GAME_TYPE_LIST.contains(gameType), ErrorCode.ERR_INTER);
        AssertUtil.assertStrNotEmpty(poolName, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(inventoryType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(increaseInventoryAmount > 0L, ErrorCode.ERR_INTER);
        String key = key(gameType, poolName);
        String field = String.valueOf(inventoryType.getCode());
        try {
            long newInventory = redisTemplate.opsForHash().increment(key, field, increaseInventoryAmount);
            log.info("increaseInventory success, key: {}, inventoryType: {}, newInventory: {}, increaseInventoryAmount: {}", key, inventoryType, newInventory, increaseInventoryAmount);
        } catch (RedisTimeoutException e) {
            log.error("increaseInventory error, found RedisTimeoutException, key: {}, inventoryType: {}, increaseInventoryAmount: {}", key, inventoryType, increaseInventoryAmount, e);
        } catch (Exception e) {
            log.error("increaseInventory error, found Exception, key: {}, inventoryType: {}, increaseInventoryAmount: {}", key, inventoryType, increaseInventoryAmount, e);
        }
    }

    public Map<InventoryTypeEnum, Long> getInventory(GameTypeEnum gameType, String poolName) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(SUPPORT_GAME_TYPE_LIST.contains(gameType), ErrorCode.ERR_INTER);
        AssertUtil.assertStrNotEmpty(poolName, ErrorCode.ERR_INTER);
        String key = key(gameType, poolName);
        try {
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            log.info("inventory info after get: key: {}, inventory: {}", key, map);
            return FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(map), new TypeReference<Map<InventoryTypeEnum, Long>>() {
            });
        } catch (RedisTimeoutException e) {
            log.error("getInventory error, found RedisTimeoutException, key: {}", key, e);
            throw new BusinessException(ErrorCode.ERR_INTER);
        } catch (Exception e) {
            log.error("getInventory error, found Exception, key: {}", key, e);
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
    }

    private String key(GameTypeEnum gameType, String poolName) {
        return String.format(INVENTORY_KEY_FORMAT, gameType.getId(), poolName);
    }
}
