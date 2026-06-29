package com.wegame.capi.scheduler.biz.diwatamayari;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.config.domain.common.CommonBloodPoolConfig;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.scheduler.biz.common.AbstractBloodPoolConfigScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.wegame.capi.common.Constants.DIWATA_MAYARI_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

@Slf4j
@Component
@ConditionalOnProperty(name = DIWATA_MAYARI_PROP, havingValue = TRUE_VAL)
public class DiwataMayariBloodPoolConfigScheduler extends AbstractBloodPoolConfigScheduler {

    @Autowired
    DiwataMayariConfig diwataMayariConfig;

    @Override
    public GameTypeEnum gameTypeEnum() {
        return GameTypeEnum.DIWATA_MAYARI;
    }

    @Override
    public CommonBloodPoolConfig bloodPoolConfig() {
        return diwataMayariConfig.getCommonBloodPoolConfig();
    }

    @Scheduled(cron = "* * * * * ?")
    public void schedule() {
        try {
            log.info("schedule start");
            long startTime = DateUtil.getCurrentMilli();
            scheduleWithLock(LockNameUtil.getBloodPoolConfigSchedulerLockName(gameTypeEnum()), this);
            log.info("schedule end, cost time: {}", DateUtil.getCurrentMilli() - startTime);
        } catch (LockException e) {
            log.info("schedule get lock fail");
        } catch (Exception e) {
            log.error("schedule failed", e);
        }
    }
}
