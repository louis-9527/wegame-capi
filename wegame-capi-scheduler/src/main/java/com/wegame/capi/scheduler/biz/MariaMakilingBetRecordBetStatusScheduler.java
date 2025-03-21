package com.wegame.capi.scheduler.biz;

import com.wegame.base.exception.LockException;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 投注记录兜底定时任务
 */
@Slf4j
@Service
public class MariaMakilingBetRecordBetStatusScheduler
        extends AbstractBetRecordBetStatusScheduler
        implements Runnable {

    @Override
    GameTypeEnum getGameType() {
        return GameTypeEnum.MARIA_MAKILING;
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void schedule() {
        log.info("MariaMakilingBetRecordBetStatusScheduler#schedule start");
        try {
            scheduleWithLock(LockNameUtil.getBetRecordStatusSchedulerLockName(getGameType()), this);
        } catch (LockException e) {
            log.info("MariaMakilingBetRecordBetStatusScheduler#get lock fail");
        } catch (Exception e) {
            log.error("MariaMakilingBetRecordBetStatusScheduler#schedule failed", e);
        }
        log.info("MariaMakilingBetRecordBetStatusScheduler#schedule end");
    }
}
