package com.wegame.capi.scheduler.operation;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.MariaMakilingOperationEventManager;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * MariaMakiling干预事件定时任务
 */
@Slf4j
@Service
public class MariaMakilingOperationEventScheduler extends AbstractOperationEventScheduler {

    @Autowired
    @Qualifier("mariaMakilingOperationEventManager")
    MariaMakilingOperationEventManager mariaMakilingOperationEventManager;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void schedule() {
        long startTime = DateUtil.getCurrentMilli();
        log.info("{}#schedule start", getClass().getSimpleName());
        try {
            scheduleWithLock(LockNameUtil.getOperationEventSchedulerLockName(operationEventType().getGameType()), this);
        } catch (LockException e) {
            log.info("{}, get lock fail", getClass().getSimpleName());
        } catch (Exception e) {
            log.error("{}#schedule failed", getClass().getSimpleName(), e);
        }
        log.info("{}#schedule end, cost time:{}", getClass().getSimpleName(), DateUtil.getCurrentMilli() - startTime);
    }

    @Override
    OperationEventTypeEnum operationEventType() {
        return OperationEventTypeEnum.MARIA_MAKILING;
    }

    @Override
    AbstractOperationEventManager operationEventManager() {
        return mariaMakilingOperationEventManager;
    }
}
