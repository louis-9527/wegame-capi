package com.wegame.capi.scheduler;

import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.biz.builder.req.BetWithPayoutReqBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetSingleWalletResultDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.common.enums.GameBetStatusEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
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
    @Autowired
    private SingleWalletService singleWalletService;

    //@Scheduled(cron = "0 0/5 * * * ?")
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
            recordList = betRecordService.list(BetRecordQuery.builder().status(GameBetStatusEnum.ING.getCode())
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

    private void handle(BetRecordDo betRecordDo) {
        if (!GameBetStatusEnum.ING.getCode().equals(betRecordDo.getStatus())) {
            return;
        }
        //获取当前订单状态
        BetStatusReq betStatusReq = BetWithPayoutReqBuilder.buildStatusReq(betRecordDo);
        Pair<String, GameBetStatusEnum> betStatus = singleWalletService.getBetStatus(betStatusReq);
        if (GameBetStatusEnum.SUCCESS == betStatus.getRight()) {
            // 更新记录
            BetRecordDo updateRecord = BetRecordDo.builder()
                    .id(betRecordDo.getId())
                    .betId(betStatus.getLeft())
                    .status(GameBetStatusEnum.SUCCESS.getCode()).build();
            betRecordService.update(updateRecord);
            return;
        }
        if (GameBetStatusEnum.FAIL == betStatus.getRight()) {
            // 更新记录
            BetRecordDo updateRecord = BetRecordDo.builder()
                    .id(betRecordDo.getId())
                    .betId(betStatus.getLeft())
                    .status(GameBetStatusEnum.FAIL.getCode()).build();
            betRecordService.update(updateRecord);
        }


    }
}
