package com.wegame.capi.scheduler.biz.mangkukulam;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.MangkukulamOperationEventManager;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.scheduler.biz.common.AbstractOperationEventScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.wegame.capi.common.Constants.MANGKUKULAM_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * Mangkukulam干预事件定时任务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
public class MangkukulamOperationEventScheduler extends AbstractOperationEventScheduler {

    @Autowired
    @Qualifier("mangkukulamOperationEventManager")
    MangkukulamOperationEventManager mangkukulamOperationEventManager;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void schedule() {
        try {
            log.info("schedule start");
            long startTime = DateUtil.getCurrentMilli();
            scheduleWithLock(LockNameUtil.getOperationEventSchedulerLockName(operationEventType().getGameType()), this);
            log.info("schedule end, cost time: {}", DateUtil.getCurrentMilli() - startTime);
        } catch (LockException e) {
            log.info("schedule get lock fail");
        } catch (Exception e) {
            log.error("schedule failed", e);
        }
    }

    @Override
    public OperationEventTypeEnum operationEventType() {
        return OperationEventTypeEnum.MANGKUKULAM;
    }

    @Override
    public AbstractOperationEventManager operationEventManager() {
        return mangkukulamOperationEventManager;
    }
}
