//package com.wegame.capi.scheduler.biz.lamang;
//
//import com.wegame.base.exception.LockException;
//import com.wegame.capi.client.enums.JackpotTypeEnum;
//import com.wegame.capi.common.utils.LockNameUtil;
//import com.wegame.capi.scheduler.biz.common.AbstractJackpotRewardPoolInputRecordDeleteScheduler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import static com.wegame.capi.common.Constants.LAMANG_PROP;
//import static com.wegame.capi.common.Constants.TRUE_VAL;
//
//
///**
// * 中奖记录兜底告警定时任务
// */
//@Slf4j
//@Service
//@ConditionalOnProperty(name = LAMANG_PROP, havingValue = TRUE_VAL)
//public class LamangJackpotRewardPoolInputRecordDeleteScheduler
//        extends AbstractJackpotRewardPoolInputRecordDeleteScheduler
//        implements Runnable {
//
//    @Override
//    public JackpotTypeEnum getJackpotTypeEnum() {
//        return JackpotTypeEnum.LAM_ANG;
//    }
//
//    /**
//     * 每天7点执行
//     */
//    @Scheduled(cron = "0 0 7 * * ?")
//    public void schedule() {
//        log.info("schedule start");
//        try {
//            scheduleWithLock(LockNameUtil.getJackpotRewardPoolInputRecordDeleteSchedulerSchedulerLock(this.getJackpotTypeEnum().getGameType()), this);
//        } catch (LockException e) {
//            log.info("schedule get lock fail");
//        } catch (Exception e) {
//            log.error("schedule failed", e);
//        }
//    }
//
//}
