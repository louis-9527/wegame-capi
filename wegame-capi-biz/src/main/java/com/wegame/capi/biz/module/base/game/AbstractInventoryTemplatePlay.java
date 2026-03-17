package com.wegame.capi.biz.module.base.game;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.service.inventory.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 库存控制服务
 */
@Slf4j
public abstract class AbstractInventoryTemplatePlay<R extends ToVoConverter> implements Game<R> {

    @Autowired
    InventoryService inventoryService;




}
