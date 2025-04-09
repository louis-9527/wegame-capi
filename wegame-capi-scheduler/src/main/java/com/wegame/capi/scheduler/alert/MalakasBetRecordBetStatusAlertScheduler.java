package com.wegame.capi.scheduler.alert;

import com.wegame.base.exception.LockException;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 投注状态告警定时任务
 */
@Slf4j
@Service
public class MalakasBetRecordBetStatusAlertScheduler
        extends AbstractAlertScheduler
        implements Runnable {

    @Override
    GameTypeEnum getGameType() {
        return GameTypeEnum.MALAKAS;
    }

    /**
     * 每天8点运行
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void schedule() {
        log.info("MalakasBetRecordBetStatusAlertScheduler#schedule start");
        try {
            scheduleWithLock(LockNameUtil.getBetRecordStatusAlertSchedulerLockName(getGameType()), this);
        } catch (LockException e) {
            log.info("MalakasBetRecordBetStatusAlertScheduler#get lock fail");
        } catch (Exception e) {
            log.error("MalakasBetRecordBetStatusAlertScheduler#schedule failed", e);
        }
        log.info("MalakasBetRecordBetStatusAlertScheduler#schedule end");
    }
}
