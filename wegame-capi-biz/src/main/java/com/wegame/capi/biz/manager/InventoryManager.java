package com.wegame.capi.biz.manager;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.inventory.InventoryService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.InventoryTypeEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.inventoryThreadPoolExecutor;

@Log4j2
@Component
public class InventoryManager {

    @Autowired
    InventoryService inventoryService;

    @Async(inventoryThreadPoolExecutor)
    public void increaseInventory(BetStatusEnum betStatus, BetRecordDo betRecordDo) {
        try {
            AssertUtil.assertNotNull(betStatus, ErrorCode.ERR_INTER);
            AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_INTER);
            if (Objects.isNull(betRecordDo.getInventory()) || Objects.equals(betStatus, BetStatusEnum.ING)) {
                return;
            }
            Long increaseInventoryVal = Objects.equals(betStatus, BetStatusEnum.SUCCESS)
                    ? betRecordDo.getInventory().getInventoryContributionAmount()
                    : betRecordDo.getPayOutAmount();
            log.info("before increaseInventory, betRecordId: {}, betStatus: {}, increaseInventoryVal: {}", betRecordDo.getId(), betStatus, increaseInventoryVal);
            inventoryService.increaseInventory(GameTypeEnum.valueOfId(betRecordDo.getGameId()),
                    betRecordDo.getInventory().getInventoryPoolName(),
                    InventoryTypeEnum.valueOfCode(betRecordDo.getInventory().getInventoryType()),
                    increaseInventoryVal);
        } catch (Exception e) {
            log.error("increaseInventory error", e);
        }
    }
}
