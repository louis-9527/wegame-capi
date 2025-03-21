package com.wegame.capi.scheduler.alert;

import com.wegame.base.exception.LockException;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.scheduler.biz.AbstractBetRecordBetStatusScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 投注状态告警定时任务
 */
@Slf4j
@Service
public class MariaMakilingBetRecordBetStatusAlertScheduler
        extends AbstractAlertScheduler
        implements Runnable {

    @Override
    GameTypeEnum getGameType() {
        return GameTypeEnum.MARIA_MAKILING;
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void schedule() {
        log.info("MariaMakilingBetRecordBetStatusAlertScheduler#schedule start");
        try {
            scheduleWithLock(LockNameUtil.getBetRecordStatusAlertSchedulerLockName(getGameType()), this);
        } catch (LockException e) {
            log.info("MariaMakilingBetRecordBetStatusAlertScheduler#get lock fail");
        } catch (Exception e) {
            log.error("MariaMakilingBetRecordBetStatusAlertScheduler#schedule failed", e);
        }
        log.info("MariaMakilingBetRecordBetStatusAlertScheduler#schedule end");
    }
}
