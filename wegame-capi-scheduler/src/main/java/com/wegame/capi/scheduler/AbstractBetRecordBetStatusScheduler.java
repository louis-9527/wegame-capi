package com.wegame.capi.scheduler;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.wegame.capi.biz.builder.req.BetStatusReqBuilder.buildStatusReq;
import static com.wegame.capi.common.enums.SingleWalletBetStatusEnum.valueOfCode;


/**
 * 投注记录兜底定时任务<br/>
 * 扭转投注中 -> 投注成功/投注失败
 */
@Slf4j
@Component
public abstract class AbstractBetRecordBetStatusScheduler
        extends AbstractScheduler
        implements Runnable {

    private static final long FIVE_MINUTES = 5 * 60 * 1000;
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
        log.info("{} schedule run start...", taskName());
        Long gtLastId = null;
        List<Long> recordIdList;
        AtomicBoolean continueFlag = new AtomicBoolean(true);
        do {
            recordIdList = betRecordService.listId(BetRecordQuery.builder()
                    .status(BetStatusEnum.ING.getCode())
                    .gtLastId(gtLastId)
                    .limit(DEFAULT_PAGE_SIZE)
                    .orderByClause("_id")
                    .direction(Sort.Direction.ASC)
                    .build(), getGameType());
            if (CollectionUtil.isNotEmpty(recordIdList)) {
                gtLastId = recordIdList.get(recordIdList.size() - 1);
            }
            for (Long betRecordId : CollectionUtils.emptyIfNull(recordIdList)) {
                scheduleWithLock(LockNameUtil.getBetRecordIdSchedulerLockName(getGameType(), betRecordId), () -> {
                    BetRecordDo betRecordDo = betRecordService.get(betRecordId, getGameType());
                    if (Objects.isNull(betRecordDo)) {
                        // do nothing
                    } else {
                        if (System.currentTimeMillis() - betRecordDo.getCreateTime() <= FIVE_MINUTES) {
                            // 只处理5分钟以前的投注记录（投注中）
                            continueFlag.set(false);
                        } else {
                            handle(betRecordDo);
                        }
                    }
                });
            }
        } while (CollectionUtils.isNotEmpty(recordIdList) && continueFlag.get());
        log.info("{} schedule run end...", taskName());
    }

    public void handle(BetRecordDo betRecordDo) {
        try {
            if (!BetStatusEnum.ING.getCode().equals(betRecordDo.getStatus())) {
                return;
            }
            log.info("{}, handle in betting BetRecordDo, betRecordId: {}", taskName(), betRecordDo.getId());
            // 获取当前订单状态
            BetStatusRes betStatusRes = singleWalletService.getBetStatus(buildStatusReq(betRecordDo));
            AssertUtil.assertNotNull(betStatusRes, ErrorCode.ERR_INTER);
            SingleWalletBetStatusEnum singleWalletBetStatus = valueOfCode(betStatusRes.getCode());
            AssertUtil.assertNotNull(singleWalletBetStatus, ErrorCode.ERR_PARAM, "invalid single wallet bet status code");
            BetStatusEnum betStatus = BetStatusEnum.valueOfSingleWalletBetStatus(singleWalletBetStatus);
            if (Objects.equals(BetStatusEnum.SUCCESS, betStatus)) {
                // 更新记录: 状态更新为成功
                betRecordService.update(BetRecordDo.builder()
                        .id(betRecordDo.getId())
                        .betId(betStatusRes.getBetId())
                        .status(BetStatusEnum.SUCCESS.getCode())
                        .build(), getGameType());
            } else if (Objects.equals(BetStatusEnum.FAIL, betStatus)) {
                // 更新记录: 状态更新为失败
                betRecordService.update(BetRecordDo.builder()
                                .id(betRecordDo.getId())
                                .betId(betStatusRes.getBetId())
                                .status(BetStatusEnum.FAIL.getCode())
                                .build(),
                        getGameType());
            } else {
                log.error("{}, handle invalid betStatus: {}, betRecordId: {}, betStatusRes: {}",
                        taskName(),
                        betStatus,
                        betRecordDo.getId(),
                        betStatusRes);
            }
        } catch (Exception e) {
            log.error("{}, handle in betting BetRecordDo error, betRecordId: {}, errMsg: {}", taskName(), betRecordDo.getId(), e.getMessage(), e);
        }
    }
}
