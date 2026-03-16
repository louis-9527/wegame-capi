package com.wegame.capi.biz.module.base.game;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 抽象库存控制模版类游戏
 *
 * @param <R>
 */
@Slf4j
public abstract class AbstractInventoryTemplateGame<R extends ToVoConverter> extends AbstractGame<R> {

    @Autowired
    public InventoryService inventoryService;

}
