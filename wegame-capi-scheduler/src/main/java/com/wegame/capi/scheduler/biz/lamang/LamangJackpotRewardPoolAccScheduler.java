package com.wegame.capi.scheduler.biz.lamang;

import com.wegame.base.exception.LockException;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.scheduler.biz.common.AbstractJackpotRewardPoolAccScheduler;
import com.wegame.capi.scheduler.biz.common.AbstractJackpotRewardPoolInputRecordDeleteScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.wegame.capi.common.Constants.LAMANG_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;


/**
 *
 */
@Slf4j
@Service
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangJackpotRewardPoolAccScheduler
        extends AbstractJackpotRewardPoolAccScheduler
        implements Runnable {

    @Override
    public JackpotTypeEnum getJackpotTypeEnum() {
        return JackpotTypeEnum.LAM_ANG;
    }

    /**
     * 每分钟执行
     */
    @Scheduled(cron = "0 * * * * ?")
    public void schedule() {
        log.info("schedule start");
        try {
            scheduleWithLock(LockNameUtil.getJackpotRewardPoolAccSchedulerSchedulerLock(this.getJackpotTypeEnum().getGameType()), this);
        } catch (LockException e) {
            log.info("schedule get lock fail");
        } catch (Exception e) {
            log.error("schedule failed", e);
        }
    }

}
