package com.wegame.capi.biz.service;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.InventoryTypeEnum;
import com.wegame.capi.common.utils.SecureRandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.defaultThreadPoolExecutor;
import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.inventoryThreadPoolExecutor;

/**
 * 库存服务
 */
@Slf4j
@Component
public class InventoryService {

    private static final String DEDUCT_INVENTORY_LUA_SCRIPT = "";

    private static final String INVENTORY_KEY_FORMAT = "wegame:capi:inventory:%s";

    private static final List<GameTypeEnum> SUPPORT_GAME_TYPE_LIST = Arrays.asList(GameTypeEnum.MALAKAS);

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public boolean deductInventory(GameTypeEnum gameType, InventoryTypeEnum inventoryType, long amount) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(SUPPORT_GAME_TYPE_LIST.contains(gameType), ErrorCode.ERR_INTER);
        if (Objects.isNull(inventoryType) || amount <= 0) {
            return true;
        }
        return false;
    }

    @Async(inventoryThreadPoolExecutor)
    public boolean increaseInventory(GameTypeEnum gameType, InventoryTypeEnum inventoryType, long amount) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(SUPPORT_GAME_TYPE_LIST.contains(gameType), ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(inventoryType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(amount > 0L, ErrorCode.ERR_INTER);
        String key = String.format(INVENTORY_KEY_FORMAT, gameType.getId());
        long inventory = redisTemplate.opsForHash().increment(key, inventoryType.getCode(), amount);
        log.info("inventory info after increase: key: {}, inventoryType: {}, inventory: {}", key, inventoryType.getCode(), inventory);
        return true;
    }

    public Map<Object, Object> getInventory(GameTypeEnum gameType) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        AssertUtil.assertTrue(SUPPORT_GAME_TYPE_LIST.contains(gameType), ErrorCode.ERR_INTER);
        String key = String.format(INVENTORY_KEY_FORMAT, gameType.getId());
        Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
        log.info("inventory info after get: key: {}, inventory: {}", key, map);
        return map;
    }
}
