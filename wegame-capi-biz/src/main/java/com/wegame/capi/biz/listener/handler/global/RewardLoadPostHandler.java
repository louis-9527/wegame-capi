package com.wegame.capi.biz.listener.handler.global;

import com.wegame.capi.biz.listener.handler.AbstractPostHandler;
import com.wegame.capi.biz.service.RewardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Component
public class RewardLoadPostHandler extends AbstractPostHandler {

    @Autowired
    RewardService rewardService;

    @Override
    public void handle() {
        try {
            gameProperty.enabledGameTypes().forEach(gameType -> {
                rewardService.getWithCache(gameType);
            });
        } catch (Exception e) {
            log.error("RewardLoadPostExecutor error", e);
        }
    }
}
