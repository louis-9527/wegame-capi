package com.wegame.capi.scheduler.event;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.manager.AbstractDynamicOperationManager;
import com.wegame.capi.biz.manager.MariaMakilingDynamicOperationManager;
import com.wegame.capi.client.enums.DynamicOperationGameEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MariaMakiLingDynamicOperationEventScheduler
        extends AbstractDynamicOperationEventScheduler
        implements Runnable {

    @Autowired
    MariaMakilingDynamicOperationManager mariaMakilingDynamicOperationManager;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void schedule() {
        long startTime = DateUtil.getCurrentMilli();
        log.info("{}#schedule start", getClass().getSimpleName());
        try {
            scheduleWithLock(LockNameUtil.getDynamicOperationSchedulerLockName(this.getDynamicOperationGame().getGameType()), this);
        } catch (LockException e) {
            log.info("get lock fail");
        } catch (Exception e) {
            log.error("MariaMakiLingBetRecordBetStatusScheduler#schedule failed", e);
        }
        log.info("MariaMakiLingBetRecordBetStatusScheduler#schedule end,time:{}", DateUtil.getCurrentMilli() - startTime);
    }

    @Override
    DynamicOperationGameEnum getDynamicOperationGame() {
        return DynamicOperationGameEnum.MARIA_MAKILING_DYNAMIC;
    }

    @Override
    AbstractDynamicOperationManager getDynamicOperationManager() {
        return mariaMakilingDynamicOperationManager;
    }
}
