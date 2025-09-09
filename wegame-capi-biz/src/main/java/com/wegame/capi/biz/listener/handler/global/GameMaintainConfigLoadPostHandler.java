package com.wegame.capi.biz.listener.handler.global;

import com.wegame.capi.biz.listener.handler.AbstractPostHandler;
import com.wegame.capi.biz.service.backend.GameMaintainConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(1)
@Component
public class GameMaintainConfigLoadPostHandler extends AbstractPostHandler {

    @Autowired
    GameMaintainConfigService gameMaintainConfigService;

    @Override
    public void handle() {
        try {
            gameProperty.enabledGameTypes().forEach(gameType -> {
                gameMaintainConfigService.selectByGameId(gameType.getId());
            });
        } catch (Exception e) {
            log.error("GameMaintainConfigLoadPostExecutor error", e);
        }
    }
}
