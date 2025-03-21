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
public class IbongAdarnaBetRecordBetStatusScheduler
        extends AbstractBetRecordBetStatusScheduler {

    @Override
    GameTypeEnum getGameType() {
        return GameTypeEnum.IBONG_ADARNA;
    }

    /**
     * 每分钟30秒执行
     */
    @Scheduled(cron = "30 * * * * ?")
    public void schedule() {
        log.info("IbongAdarnaBetRecordBetStatusScheduler#schedule start");
        try {
            scheduleWithLock(LockNameUtil.getBetRecordStatusSchedulerLockName(getGameType()), this);
        } catch (LockException e) {
            log.info("IbongAdarnaBetRecordBetStatusScheduler#get lock fail");
        } catch (Exception e) {
            log.error("IbongAdarnaBetRecordBetStatusScheduler#schedule failed", e);
        }
        log.info("IbongAdarnaBetRecordBetStatusScheduler#schedule end");
    }
}
