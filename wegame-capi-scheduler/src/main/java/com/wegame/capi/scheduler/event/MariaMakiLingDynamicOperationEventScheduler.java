package com.wegame.capi.scheduler.event;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class MariaMakiLingDynamicOperationEventScheduler extends AbstractDynamicOperationEventScheduler implements Runnable {
    @Override
    GameTypeEnum getGameType() {
        return GameTypeEnum.MARIA_MAKILING;
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void schedule() {
        long startTime = DateUtil.getCurrentMilli();
        log.info("MariaMakiLingDynamicOperationEventScheduler#schedule start");
        try {
            scheduleWithLock(LockNameUtil.getDynamicOperationSchedulerLockName(this.getGameType()), this);
        } catch (LockException e) {
            log.info("get lock fail");
        } catch (Exception e) {
            log.error("MariaMakiLingBetRecordBetStatusScheduler#schedule failed", e);
        }
        log.info("MariaMakiLingBetRecordBetStatusScheduler#schedule end,time:{}", DateUtil.getCurrentMilli() - startTime);
    }
}
