package com.wegame.capi.scheduler;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
import com.wegame.capi.biz.builder.req.BetWithPayoutReqBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.SingleWalletService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.SingleWalletBetStatusEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.wegame.capi.biz.builder.req.BetStatusReqBuilder.buildStatusReq;
import static com.wegame.capi.common.enums.SingleWalletBetStatusEnum.valueOfCode;


/**
 * 投注记录兜底定时任务
 */
@Slf4j
@Component
public abstract class AbstractBetRecordBetStatusScheduler extends AbstractScheduler implements Runnable {

    @Autowired
    BetRecordService betRecordService;
    @Autowired
    SingleWalletService singleWalletService;

    abstract GameTypeEnum getGameType();

    /**
     * 1.获取5分钟前,还在运行中的投注记录
     */
    @Override
    public void run() {
        log.info("MariaMakiLingBetRecordBetStatusScheduler#schedule run start");
        long fiveMinutesAgo = System.currentTimeMillis() - 5 * 60 * 1000;
        int pageNo = 1;
        Long lastId = null;
        List<Long> recordIdList;
        do {
            recordIdList = betRecordService.listId(BetRecordQuery.builder().status(BetStatusEnum.ING.getCode())
                    .endCreateTime(fiveMinutesAgo)
                    .limit(DEFAULT_PAGE_SIZE)
                    .offset((pageNo - 1) * DEFAULT_PAGE_SIZE)
                    .lastId(lastId)
                    .build(), this.getGameType());
            if (CollectionUtil.isNotEmpty(recordIdList)) {
                lastId = recordIdList.get(recordIdList.size() - 1);
            }
            Optional.ofNullable(recordIdList)
                    .orElse(Collections.emptyList())
                    .forEach(this::handle);
            pageNo++;
        } while (CollectionUtils.isNotEmpty(recordIdList));
        log.info("MariaMakiLingBetRecordBetStatusScheduler#schedule run end");
    }

    public void handle(Long betRecordId) {
        scheduleWithLock(LockNameUtil.getBetRecordIdSchedulerLockName(getGameType(), betRecordId), () -> {
            try {
                BetRecordDo betRecordDo = betRecordService.get(betRecordId, getGameType());
                if (!BetStatusEnum.ING.getCode().equals(betRecordDo.getStatus())) {
                    return;
                }
                log.info("handle in betting BetRecordDo, betRecordId: {}", betRecordDo.getId());
                // 获取当前订单状态
                BetStatusRes betStatusRes = singleWalletService.getBetStatus(buildStatusReq(betRecordDo));
                AssertUtil.assertNotNull(betStatusRes, ErrorCode.ERR_INTER);
                SingleWalletBetStatusEnum singleWalletBetStatus = valueOfCode(betStatusRes.getCode());
                AssertUtil.assertNotNull(singleWalletBetStatus, ErrorCode.ERR_PARAM, "invalid single wallet bet status code");
                BetStatusEnum betStatus = BetStatusEnum.valueOfSingleWalletBetStatus(singleWalletBetStatus);
                if (Objects.equals(BetStatusEnum.SUCCESS, betStatus)) {
                    // 更新记录: 状态更新为成功
                    BetRecordDo updateRecord = BetRecordDo.builder()
                            .id(betRecordDo.getId())
                            .betId(betStatusRes.getBetId())
                            .status(BetStatusEnum.SUCCESS.getCode())
                            .build();
                    betRecordService.update(updateRecord, getGameType());
                } else if (Objects.equals(BetStatusEnum.FAIL, betStatus)) {
                    // 更新记录: 状态更新为失败
                    BetRecordDo updateRecord = BetRecordDo.builder()
                            .id(betRecordDo.getId())
                            .betId(betStatusRes.getBetId())
                            .status(BetStatusEnum.FAIL.getCode())
                            .build();
                    betRecordService.update(updateRecord, this.getGameType());
                } else {
                    log.error("AbstractBetRecordBetStatusScheduler#handle invalid betStatus: {}, betRecordId: {}, betStatusRes: {}",
                            betStatus,
                            betRecordId,
                            betStatusRes);
                }
            } catch (Exception e) {
                log.error("handle in betting BetRecordDo error, betRecordId: {}, errMsg: {}", betRecordId, e.getMessage(), e);
            }
        });
    }
}
