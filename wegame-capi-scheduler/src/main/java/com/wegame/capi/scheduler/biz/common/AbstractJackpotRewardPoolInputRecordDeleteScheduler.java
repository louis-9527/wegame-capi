package com.wegame.capi.scheduler.biz.common;

import com.wegame.capi.biz.builder.jackpot.JackpotUserRewardPoolInputRecordQueryBuilder;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolInputRecordService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotAccFlagEnum;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;

/**
 * jackpot 派彩定时任务<br/>
 */
@Slf4j
public abstract class AbstractJackpotRewardPoolInputRecordDeleteScheduler
        extends AbstractScheduler
        implements Runnable {

    private static final long THREE_DAYS = 3 * 24 * 60 * 1000;

    @Autowired
    JackpotRewardPoolInputRecordService jackpotRewardPoolInputRecordService;

    public abstract JackpotTypeEnum getJackpotTypeEnum();

    @Override
    public void run() {
        log.info("{} schedule run start...", taskName());
        long time = System.currentTimeMillis() - THREE_DAYS;
        Query query = JackpotUserRewardPoolInputRecordQueryBuilder.buildDeleteQuery(time, JackpotAccFlagEnum.YES);
        long num = jackpotRewardPoolInputRecordService.batchDelete(getJackpotTypeEnum(), query);
        log.info("delete JackpotUserRewardPoolInputRecord: {}", num);
        log.info("{} schedule run end...", taskName());
    }
}
