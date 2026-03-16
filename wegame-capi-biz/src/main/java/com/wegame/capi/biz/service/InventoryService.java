package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.InventoryTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.client.RedisTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
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

    // wegame:capi:inventory:{gameId}:{pid}:{channelId}
    private static final String INVENTORY_KEY_FORMAT = "wegame:capi:inventory:%s:%s:%s";

    private static final List<GameTypeEnum> SUPPORT_GAME_TYPE_LIST = Arrays.asList(GameTypeEnum.MALAKAS);

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public boolean deductInventory(GameTypeEnum gameType, int pid, int channelId, InventoryTypeEnum inventoryType, long deductInventoryAmount) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(SUPPORT_GAME_TYPE_LIST.contains(gameType), ErrorCode.ERR_INTER);
        if (Objects.isNull(inventoryType) || deductInventoryAmount <= 0) {
            return true;
        }
        String key = String.format(INVENTORY_KEY_FORMAT, gameType.getId(), pid, channelId);
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(DEDUCT_INVENTORY_LUA_SCRIPT, Long.class);
        List<Object> args = Arrays.asList(inventoryType.getCode(), deductInventoryAmount);
        try {
            Long newInventory = redisTemplate.execute(script, Collections.singletonList(key), args.toArray());
            boolean ret = Objects.nonNull(newInventory) && newInventory >= 0;
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
    public boolean increaseInventory(GameTypeEnum gameType, InventoryTypeEnum inventoryType, long increaseInventoryAmount) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(SUPPORT_GAME_TYPE_LIST.contains(gameType), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(inventoryType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(increaseInventoryAmount > 0L, ErrorCode.ERR_INTER);
        String key = String.format(INVENTORY_KEY_FORMAT, gameType.getId());
        try {
            long newInventory = redisTemplate.opsForHash().increment(key, inventoryType.getCode(), increaseInventoryAmount);
            log.info("increaseInventory success, key: {}, inventoryType: {}, newInventory: {}, increaseInventoryAmount: {}", key, inventoryType, newInventory, increaseInventoryAmount);
            return true;
        } catch (RedisTimeoutException e) {
            log.error("increaseInventory error, found RedisTimeoutException, key: {}, inventoryType: {}, increaseInventoryAmount: {}", key, inventoryType, increaseInventoryAmount, e);
            return false;
        } catch (Exception e) {
            log.error("increaseInventory error, found Exception, key: {}, inventoryType: {}, increaseInventoryAmount: {}", key, inventoryType, increaseInventoryAmount, e);
            return false;
        }
    }

    public Map<Object, Object> getInventory(GameTypeEnum gameType, int pid, int channelId) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(SUPPORT_GAME_TYPE_LIST.contains(gameType), ErrorCode.ERR_INTER);
        String key = String.format(INVENTORY_KEY_FORMAT, gameType.getId(), pid, channelId);
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        log.info("inventory info after get: key: {}, inventory: {}", key, map);
        return map;
    }
}
