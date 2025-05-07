package com.wegame.capi.scheduler.backend;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.backend.GameMaintainConfigDo;
import com.wegame.capi.biz.service.backend.GameMaintainConfigService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.infrastructure.config.properties.GameProperty;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class MaintainCacheUpdateScheduler extends AbstractScheduler implements Runnable {

    @Autowired
    GameMaintainConfigService gameMaintainConfigService;

    @Autowired
    GameProperty gameProperty;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void schedule() {
        try {
            log.info("MaintainCacheUpdateScheduler: update start");
            long startTime = DateUtil.getCurrentMilli();
            scheduleWithLock(LockNameUtil.getMaintainConfigUpdateLock(), this);
            log.info("MaintainCacheUpdateScheduler: schedule end, cost time:{}", DateUtil.getCurrentMilli() - startTime);
        } catch (LockException e) {
            log.info("MaintainCacheUpdateScheduler: get lock fail");
        } catch (Exception e) {
            log.error("MaintainCacheUpdateScheduler : schedule failed", e);
        }
    }


    @Override
    public void run() {
        List<GameTypeEnum> games =  gameProperty.enabledGameTypes();
        if (!CollectionUtils.isEmpty(games)) {
            games.forEach(gameType -> {
                GameMaintainConfigDo config = gameMaintainConfigService.selectByGameIdWithNoCache(gameType.getId());
                gameMaintainConfigService.putCache(gameType.getId(), config);
            });
        }
    }
}
