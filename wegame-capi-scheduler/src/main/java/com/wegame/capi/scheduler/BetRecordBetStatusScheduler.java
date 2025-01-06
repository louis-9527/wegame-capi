package com.wegame.capi.scheduler;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.common.enums.GameStatusEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * 投注记录兜底定时任务
 */
@Slf4j
@Service
public class BetRecordBetStatusScheduler extends AbstractScheduler implements Runnable {
    @Autowired
    private BetRecordService betRecordService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void schedule() {
        scheduleWithLock(LockNameUtil.getBetRecordPayOutStatusSchedulerLockName(), this);
    }

    @Override
    public void run() {
        /**
         * 1.获取5分钟前,还在运行中的投注记录
         */
        Date fiveMinutesAgo = new Date(System.currentTimeMillis() - 5 * 60 * 1000);
        int pageNo = 1;
        List<BetRecordDo> recordList;
        do {
           recordList = betRecordService.list(BetRecordQuery.builder().status(GameStatusEnum.ING.getCode())
                    .endCreateTime(fiveMinutesAgo)
                    .limit(DEFAULT_PAGE_SIZE)
                    .offset((pageNo - 1) * DEFAULT_PAGE_SIZE)

                    .build());
            Optional.ofNullable(recordList)
                    .orElse(Collections.emptyList())
                    .forEach(this::handle);
            pageNo++;
        } while (CollectionUtils.isNotEmpty(recordList));
    }

    private void handle(BetRecordDo betRecordDo){
        //todo 调用单钱包,并且更新状态

    }
}
