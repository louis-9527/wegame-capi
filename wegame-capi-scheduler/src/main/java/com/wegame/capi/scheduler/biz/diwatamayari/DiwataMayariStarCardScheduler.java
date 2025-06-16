package com.wegame.capi.scheduler.biz.diwatamayari;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.scheduler.biz.common.AbstractStarCardScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.wegame.capi.common.Constants.DIWATA_MAYARI_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * 星卡定时任务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataMayariStarCardScheduler extends AbstractStarCardScheduler {

//    @Scheduled(cron = "0 0/12 * * * ?")
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
    public GameTypeEnum gameTypeEnum() {
        return GameTypeEnum.DIWATA_MAYARI;
    }
}
