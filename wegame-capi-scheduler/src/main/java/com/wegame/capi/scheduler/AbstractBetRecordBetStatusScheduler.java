package com.wegame.capi.scheduler;

import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 投注记录兜底定时任务
 */
@Slf4j
@Component
public abstract class AbstractBetRecordBetStatusScheduler extends AbstractScheduler implements Runnable {
    abstract GameTypeEnum getGameType();
}
