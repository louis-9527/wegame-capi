//package com.wegame.capi.scheduler.biz.mariamakiling;
//
//import com.wegame.base.exception.LockException;
//import com.wegame.base.utils.DateUtil;
//import com.wegame.capi.client.enums.GameTypeEnum;
//import com.wegame.capi.common.utils.LockNameUtil;
//import com.wegame.capi.scheduler.biz.common.AbstractBetRecordBetStatusAlertScheduler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import static com.wegame.capi.common.Constants.MARIA_MAKILING_PROP;
//import static com.wegame.capi.common.Constants.TRUE_VAL;
//
///**
// * 投注状态告警定时任务
// */
//@Slf4j
//@Service
//@ConditionalOnProperty(name = MARIA_MAKILING_PROP, havingValue = TRUE_VAL)
//public class MariaMakilingBetRecordBetStatusAlertScheduler
//        extends AbstractBetRecordBetStatusAlertScheduler
//        implements Runnable {
//
//    /**
//     * 每天8点运行
//     */
//    @Scheduled(cron = "0 0 8 * * ?")
//    public void schedule() {
//        try {
//            log.info("schedule start");
//            long startTime = DateUtil.getCurrentMilli();
//            scheduleWithLock(LockNameUtil.getBetRecordStatusAlertSchedulerLockName(getGameType()), this);
//            log.info("schedule end, cost time:{}", DateUtil.getCurrentMilli() - startTime);
//        } catch (LockException e) {
//            log.info("schedule get lock fail");
//        } catch (Exception e) {
//            log.error("schedule failed", e);
//        }
//    }
//
//    @Override
//    public GameTypeEnum getGameType() {
//        return GameTypeEnum.MARIA_MAKILING;
//    }
//
//}
