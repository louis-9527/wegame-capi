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
public class MalakasBetRecordBetStatusScheduler
        extends AbstractBetRecordBetStatusScheduler
        implements Runnable {

    @Override
    GameTypeEnum getGameType() {
        return GameTypeEnum.MALAKAS;
    }

    /**
     * 每分钟15秒执行
     */
    @Scheduled(cron = "15 * * * * ?")
    public void schedule() {
        log.info("MalakasBetRecordBetStatusScheduler#schedule start");
        try {
            scheduleWithLock(LockNameUtil.getBetRecordStatusSchedulerLockName(this.getGameType()), this);
        } catch (LockException e) {
            log.info("get lock fail");
        } catch (Exception e) {
            log.error("MalakasBetRecordBetStatusScheduler#schedule failed", e);
        }
        log.info("MalakasBetRecordBetStatusScheduler#schedule end");
    }
}
