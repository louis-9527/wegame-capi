package com.wegame.capi.scheduler;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.exception.LockException;
import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.biz.builder.req.BetWithPayoutReqBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * 投注记录兜底定时任务
 */
@Slf4j
@Service
public class MariaMakiLingBetRecordBetStatusScheduler
        extends AbstractBetRecordBetStatusScheduler
        implements Runnable {

    @Override
    GameTypeEnum getGameType() {
        return GameTypeEnum.MARIA_MAKILING;
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void schedule() {
        log.info("MariaMakiLingBetRecordBetStatusScheduler#schedule start");
        try {
            scheduleWithLock(LockNameUtil.getBetRecordStatusSchedulerLockName(this.getGameType()), this);
        } catch (LockException e) {
            log.info("get lock fail");
        } catch (Exception e) {
            log.error("MariaMakiLingBetRecordBetStatusScheduler#schedule failed", e);
        }
        log.info("MariaMakiLingBetRecordBetStatusScheduler#schedule end");
    }
}
