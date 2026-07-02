package com.wegame.capi.biz.listener.handler.global;

import com.wegame.capi.biz.listener.handler.AbstractPostHandler;
import com.wegame.capi.biz.service.RewardService;
import com.wegame.capi.infrastructure.config.properties.GameApiProperty;
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
    @Autowired
    GameApiProperty gameApiProperty;

    @Override
    public void handle() {
        try {
            gameProperty.enabledGameTypes().forEach(gameType -> {
                for (GameApiProperty.PrivateInfo value : gameApiProperty.getPrivateInfoMap().values()) {
                    log.info("start warm up rewardInfo, gameType: {}, pid: {}, channelId: {}", gameType.getName(), value.getPlatformId(), value.getChannelId());
                    rewardService.getWithCache(gameType, value.getPlatformId(), value.getChannelId());
                    log.info("end warm up rewardInfo, gameType: {}, pid: {}, channelId: {}", gameType.getName(), value.getPlatformId(), value.getChannelId());
                }
            });
        } catch (Exception e) {
            log.error("RewardLoadPostExecutor error", e);
        }
    }
}
