package com.wegame.capi.biz.listener;

import com.wegame.capi.biz.service.RewardService;
import com.wegame.capi.biz.service.backend.GameMaintainConfigService;
import com.wegame.capi.infrastructure.config.properties.GameProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    GameProperty gameProperty;
    @Autowired
    RewardService rewardService;
    @Autowired
    GameMaintainConfigService gameMaintainConfigService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 执行业务预热操作
        log.info("Application started. Performing business warm-up start...");
        businessWarmUp();
        log.info("Application started. Performing business warm-up start...");
    }

    private void businessWarmUp() {
        try {
            gameProperty.enabledGameTypes().forEach(gameType -> {
                rewardService.getWithCache(gameType);
            });
            gameProperty.enabledGameTypes().forEach(gameType -> {
                gameMaintainConfigService.selectByGameId(gameType.getId());
            });
        } catch (Exception e) {
            log.error("businessWarmUp error", e);
        }
    }
}
