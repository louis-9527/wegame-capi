package com.wegame.capi.scheduler.biz.mangkukulam;

import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.module.base.bet.strategy.BetStrategy;
import com.wegame.capi.biz.module.mangkukulam.bet.strategy.MangkukulamNormalBetStrategy;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.scheduler.biz.common.AbstractContinuePlayScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.wegame.capi.common.Constants.MANGKUKULAM_PROP;
import static com.wegame.capi.common.Constants.TRUE_VAL;

/**
 * Mangkukulam维护配置定时任务
 */
@Slf4j
@Service
@ConditionalOnProperty(name = MANGKUKULAM_PROP, havingValue = TRUE_VAL)
public class MangkukulamContinuPlayScheduler extends AbstractContinuePlayScheduler {


    @Qualifier("mangkukulamNormalBetStrategy")
    @Autowired
    MangkukulamNormalBetStrategy mangkukulamNormalBetStrategy;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void schedule() {
        try {
            log.info("schedule start");
            long startTime = DateUtil.getCurrentMilli();
            scheduleWithLock(LockNameUtil.getMaintainConfigUpdateLock(gameType()), this);
            log.info("schedule end, cost time:{}", DateUtil.getCurrentMilli() - startTime);
        } catch (LockException e) {
            log.info("get lock fail");
        } catch (Exception e) {
            log.error("schedule failed", e);
        }
    }

    @Override
    public GameTypeEnum gameType() {
        return GameTypeEnum.MANGKUKULAM;
    }

    @Override
    public BetStrategy betStrategy() {
        return mangkukulamNormalBetStrategy;
    }
}
