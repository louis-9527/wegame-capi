package com.wegame.capi.scheduler.starcard;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.manager.operation.MariaMakilingOperationEventManager;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.scheduler.operation.AbstractOperationEventScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.wegame.capi.common.Constants.MARIA_MAKILING_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * MariaMakiling干预事件定时任务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
public class MariaMakilingStarCardScheduler extends AbstractStarCardScheduler {


    @Scheduled(cron = "0 0/10 * * * ?")
    public void schedule() {
        try {
            log.info("schedule start");
            long startTime = DateUtil.getCurrentMilli();
            scheduleWithLock(LockNameUtil.getStarCardReportSchedulerLockName(gameTypeEnum()), this);
            log.info("schedule end, cost time:{}", DateUtil.getCurrentMilli() - startTime);
        } catch (LockException e) {
            log.info("schedule get lock fail");
        } catch (Exception e) {
            log.error("schedule failed", e);
        }
    }

    @Override
    GameTypeEnum gameTypeEnum() {
        return GameTypeEnum.MARIA_MAKILING;
    }
}
