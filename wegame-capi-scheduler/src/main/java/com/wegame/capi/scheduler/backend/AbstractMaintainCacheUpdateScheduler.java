package com.wegame.capi.scheduler.backend;

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
public abstract class AbstractMaintainCacheUpdateScheduler extends AbstractScheduler implements Runnable {

    abstract GameTypeEnum gameType();

    @Autowired
    GameMaintainConfigService gameMaintainConfigService;


    @Override
    public void run() {
        GameTypeEnum gameType = gameType();
        if (Objects.nonNull(gameType)) {
            GameMaintainConfigDo config = gameMaintainConfigService.selectByGameIdWithNoCache(gameType.getId());
            gameMaintainConfigService.putCache(gameType.getId(), config);
        }
    }
}
