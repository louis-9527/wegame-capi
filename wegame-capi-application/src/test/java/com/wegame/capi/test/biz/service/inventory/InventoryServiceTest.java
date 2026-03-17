package com.wegame.capi.test.biz.service.inventory;

import com.wegame.base.utils.FastJsonUtils;
import com.wegame.base.utils.ThreadMdcUtil;
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

import java.util.Map;

@Slf4j
public class InventoryServiceTest extends BaseTest {

    @Autowired(required = false)
    InventoryService inventoryService;

    private static final UserInfo userInfo = UserInfo.builder()
            .id("10000")
            .pId("30")
            .channelId("3050")
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
        Map<InventoryTypeEnum, Long> map = inventoryService.getInventory(GameTypeEnum.MALAKAS, userInfo.getPId(), userInfo.getChannelId());
        log.info("getInventory ret: {}", FastJsonUtils.toJSONString(map));
    }

}
