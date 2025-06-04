package com.wegame.capi.scheduler.jackpot;

import com.wegame.base.exception.LockException;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
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
@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
public class LamangJackpotRewardGrantScheduler
        extends AbstractJackpotRewardGrantScheduler
        implements Runnable {


    @Override
    JackpotTypeEnum getJackpotTypeEnum() {
        return JackpotTypeEnum.LAM_ANG;
    }

    /**
     * 每5分钟执行
     */
    @Scheduled(cron = "*/5 * * * *")
    public void schedule() {
        log.info("LamangJackpotPayoutScheduler#schedule start");
        try {
            scheduleWithLock(LockNameUtil.getJackpotRewardGrantSchedulerLock(this.getJackpotTypeEnum().getGameType()), this);
        } catch (LockException e) {
            log.info("get lock fail");
        } catch (Exception e) {
            log.error("LamangJackpotPayoutScheduler#schedule failed", e);
        }
        log.info("LamangJackpotPayoutScheduler#schedule end");
    }

}
