package com.wegame.capi.scheduler.biz.diwatamayari;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.DiwataMayariOperationEventManager;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.scheduler.biz.common.AbstractOperationEventScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.wegame.capi.common.Constants.DIWATA_MAYARI_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 干预事件定时任务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataMayariOperationEventScheduler extends AbstractOperationEventScheduler {

    @Autowired
    @Qualifier("diwataMayariOperationEventManager")
    DiwataMayariOperationEventManager diwataMayariOperationEventManager;

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
        return OperationEventTypeEnum.DIWATA_MAYARI;
    }

    @Override
    public AbstractOperationEventManager operationEventManager() {
        return diwataMayariOperationEventManager;
    }
}
