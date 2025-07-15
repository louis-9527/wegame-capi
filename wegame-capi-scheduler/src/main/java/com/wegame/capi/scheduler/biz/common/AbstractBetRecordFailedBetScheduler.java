package com.wegame.capi.scheduler.biz.common;

import com.wegame.capi.biz.service.FailedBetPostProcessService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 投注记录兜底定时任务<br/>
 * 失败 -> 投注成功/投注中/失败
 */
@Slf4j
public abstract class AbstractBetRecordFailedBetScheduler
        extends AbstractScheduler
        implements Runnable {

    @Autowired
    FailedBetPostProcessService failedBetPostProcessService;

    public abstract GameTypeEnum getGameType();

    /**
     * 扫描投注时间范围内失败的投注 重试
     */
    @Override
    public void run() {
        log.info("{} schedule run start...", taskName());
        failedBetPostProcessService.processFailedBet(getGameType());
        log.info("{} schedule run end...", taskName());
    }
}
