package com.wegame.capi.scheduler.operation;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.MalakasOperationEventManager;
import com.wegame.capi.biz.manager.operation.MariaMakilingOperationEventManager;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.wegame.capi.common.Constants.MARIA_MAKILING_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * Marakas干预事件定时任务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
public class MalakasOperationEventScheduler extends AbstractOperationEventScheduler {

    @Autowired
    @Qualifier("malakasOperationEventManager")
    MalakasOperationEventManager malakasOperationEventManager;

    @Scheduled(cron = "0 0/9 * * * ?")
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
        return OperationEventTypeEnum.MALAKAS_DYNAMIC;
    }

    @Override
    AbstractOperationEventManager operationEventManager() {
        return malakasOperationEventManager;
    }
}
