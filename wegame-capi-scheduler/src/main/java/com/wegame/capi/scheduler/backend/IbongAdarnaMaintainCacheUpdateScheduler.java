package com.wegame.capi.scheduler.backend;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.wegame.capi.common.Constants.*;

@Slf4j
@Service
@ConditionalOnProperty(name = IBONG_ADARNA_PROP, havingValue = TRUE_VAL)
public class IbongAdarnaMaintainCacheUpdateScheduler extends AbstractMaintainCacheUpdateScheduler {

    @Scheduled(cron = "0 0/5 * * * ?")
    public void schedule() {
        try {
            log.info("schedule start");
            long startTime = DateUtil.getCurrentMilli();
            scheduleWithLock(LockNameUtil.getMaintainConfigUpdateLock(gameType()), this);
            log.info("schedule end, cost time:{}", DateUtil.getCurrentMilli() - startTime);
        } catch (LockException e) {
            log.info("get lock fail");
        } catch (Exception e) {
            log.error("schedule failed", e);
        }
    }

    @Override
    GameTypeEnum gameType() {
        return GameTypeEnum.IBONG_ADARNA;
    }
}
