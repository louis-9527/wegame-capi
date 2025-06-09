package com.wegame.capi.scheduler.biz.common;

import com.wegame.capi.biz.domain.backend.GameMaintainConfigDo;
import com.wegame.capi.biz.service.backend.GameMaintainConfigService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public abstract class AbstractMaintainCacheUpdateScheduler
        extends AbstractScheduler
        implements Runnable {

    abstract GameTypeEnum gameType();

    @Autowired
    GameMaintainConfigService gameMaintainConfigService;

    @Override
    public void run() {
        GameTypeEnum gameType = gameType();
        if (Objects.isNull(gameType)) {
            return;
        }
        GameMaintainConfigDo config = gameMaintainConfigService.selectByGameIdWithNoCache(gameType.getId());
        if (Objects.isNull(config)) {
            return;
        }
        gameMaintainConfigService.putCache(gameType.getId(), config);
    }
}
