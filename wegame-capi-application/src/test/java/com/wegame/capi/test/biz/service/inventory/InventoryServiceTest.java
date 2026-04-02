package com.wegame.capi.test.biz.service.inventory;

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
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            Object val = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName, item);
            log.info("test_getInventory val: {}", val);
        });
    }

    @Test
    public void test_increaseInventory() {
        UserInfoContext.setUserInfo(userInfo);
        String poolName = getPoolName(userInfo);
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            inventoryService.increaseInventory(GameTypeEnum.MALAKAS, poolName, item, 1000000000);
            Object val = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName, item);
            log.info("test_increaseInventory ret: {}", val);
        });
    }

    @Test
    public void test_deductInventory() {
        UserInfoContext.setUserInfo(userInfo);
        String poolName = getPoolName(userInfo);
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            boolean ret = inventoryService.deductInventory(GameTypeEnum.MALAKAS, poolName, item, 7);
            Object val = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName, item);
            log.info("test_deductInventory ret: {}, val: {}", ret, val);
        });
    }

    @Test
    public void test_all() {
        UserInfoContext.setUserInfo(userInfo);
        String poolName = getPoolName(userInfo);
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            Object val = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName, item);
            log.info("test_all val: {}", val);
        });
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            inventoryService.increaseInventory(GameTypeEnum.MALAKAS, poolName, item, 1000000000);
            Object val = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName, item);
            log.info("test_all ret: {}", val);
        });
        Arrays.stream(InventoryTypeEnum.values()).forEach(item -> {
            boolean ret = inventoryService.deductInventory(GameTypeEnum.MALAKAS, poolName, item, 7);
            Object val = inventoryService.getInventory(GameTypeEnum.MALAKAS, poolName, item);
            log.info("test_all ret: {}, val: {}", ret, val);
        });
    }

    private String getPoolName(UserInfo userInfo) {
        return malakasConfig.getInventoryControlConfig().getInventoryConfig().getInventoryPoolName(userInfo.getPId(), userInfo.getChannelId());
    }

}
