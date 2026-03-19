package com.wegame.capi.test.biz.service.inventory;

import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.config.domain.MalakasConfig;
import com.wegame.capi.biz.service.inventory.InventoryService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.InventoryTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class InventoryServiceTest extends BaseTest {

    @Autowired(required = false)
    InventoryService inventoryService;
    @Autowired
    MalakasConfig malakasConfig;

    private static final UserInfo userInfo = UserInfo.builder()
            .id("10000")
            .pId("50")
            .channelId("1050")
            .playerId("123456")
            .playerName("123456")
            .userName("system")
            .build();

    @Before
    public void beforeTest() {
        ThreadMdcUtil.setTraceIdIfAbsent();
    }

    @Test
    public void test_getInventory() {
        UserInfoContext.setUserInfo(userInfo);
        String poolName = getPoolName(userInfo);
        Map<Object, Object> map = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName);
        log.info("test_getInventory ret: {}", FastJsonUtils.toJSONString(map));
    }

    @Test
    public void test_increaseInventory() {
        UserInfoContext.setUserInfo(userInfo);
        String poolName = getPoolName(userInfo);
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            inventoryService.increaseInventory(GameTypeEnum.MALAKAS, poolName, item, 1000000000);
            Map<Object, Object> map = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName);
            log.info("test_getInventory ret: {}", FastJsonUtils.toJSONString(map));
        });
    }

    @Test
    public void test_deductInventory() {
        UserInfoContext.setUserInfo(userInfo);
        String poolName = getPoolName(userInfo);
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            inventoryService.deductInventory(GameTypeEnum.MALAKAS, poolName, item, 9);
            Map<Object, Object> map = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName);
            log.info("test_deductInventory ret: {}", FastJsonUtils.toJSONString(map));
        });
    }

    @Test
    public void test_all() {
        UserInfoContext.setUserInfo(userInfo);
        String poolName = getPoolName(userInfo);
        Map<Object, Object> map2 = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName);
        log.info("test_all ret: {}", map2);
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            inventoryService.increaseInventory(GameTypeEnum.MALAKAS, poolName, item, 1000000000);
            Map<Object, Object> map = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName);
            log.info("test_all ret: {}", map);
        });
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            inventoryService.deductInventory(GameTypeEnum.MALAKAS, poolName, item, 7);
            Map<Object, Object> map = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName);
            log.info("test_all ret: {}", FastJsonUtils.toJSONString(map));
        });
    }
    
    private String getPoolName(UserInfo userInfo) {
        return malakasConfig.getInventoryControlConfig().getInventoryConfig().getInventoryPoolName(userInfo.getPId(), userInfo.getChannelId());
    }

}
