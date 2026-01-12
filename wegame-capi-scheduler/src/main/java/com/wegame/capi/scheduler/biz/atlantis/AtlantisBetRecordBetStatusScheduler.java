package com.wegame.capi.scheduler.biz.atlantis;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.scheduler.biz.common.AbstractBetRecordBetStatusScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.wegame.capi.common.Constants.*;

/**
 * 投注记录兜底定时任务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = ATLANTIS_PROP, havingValue = TRUE_VAL)
public class AtlantisBetRecordBetStatusScheduler extends AbstractBetRecordBetStatusScheduler {

    @Override
    public GameTypeEnum getGameType() {
        return GameTypeEnum.ATLANTIS;
    }

    /**
     * 每分钟30秒执行
     */
    @Scheduled(cron = "30 * * * * ?")
    public void schedule() {
        try {
            log.info("schedule start");
            long startTime = DateUtil.getCurrentMilli();
            scheduleWithLock(LockNameUtil.getBetRecordStatusSchedulerLockName(getGameType()), this);
            log.info("schedule end, cost time: {}", DateUtil.getCurrentMilli() - startTime);
        } catch (LockException e) {
            log.info("schedule get lock fail");
        } catch (Exception e) {
            log.error("schedule failed", e);
        }
    }
}
