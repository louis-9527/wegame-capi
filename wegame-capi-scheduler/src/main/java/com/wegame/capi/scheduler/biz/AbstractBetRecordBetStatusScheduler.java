package com.wegame.capi.scheduler.biz;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.BetStatusService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;


/**
 * 投注记录兜底定时任务<br/>
 * 扭转投注中 -> 投注成功/投注失败
 */
@Slf4j
public abstract class AbstractBetRecordBetStatusScheduler
        extends AbstractScheduler
        implements Runnable {

    private static final long THREE_MINUTES = 3 * 60 * 1000;
    private static final long ONE_MINUTES = 1 * 60 * 1000;

    @Autowired
    BetRecordService betRecordService;
    @Autowired
    BetStatusService betStatusService;

    abstract GameTypeEnum getGameType();

    /**
     * 扫描投注状态为投注中 && 创建时间在[当前时间-3分钟，当前时间-2分钟]的投注记录，扭转其投注状态
     */
    @Override
    public void run() {
        log.info("{} schedule run start...", taskName());
        Long gtLastId = null;
        List<Long> recordIdList;
        do {
            recordIdList = betRecordService.listId(BetRecordQuery.builder()
                    .status(BetStatusEnum.ING.getCode())
                    .startCreateTime(System.currentTimeMillis() - THREE_MINUTES)
                    .endCreateTime(System.currentTimeMillis() - ONE_MINUTES)
                    .gtLastId(gtLastId)
                    .limit(DEFAULT_PAGE_SIZE)
                    .orderByClause("_id")
                    .direction(Sort.Direction.ASC)
                    .build(), getGameType());
            if (CollectionUtil.isNotEmpty(recordIdList)) {
                gtLastId = recordIdList.get(recordIdList.size() - 1);
            }
            for (Long betRecordId : CollectionUtils.emptyIfNull(recordIdList)) {
                BetRecordDo betRecordDo = betRecordService.get(betRecordId, getGameType());
                if (Objects.isNull(betRecordDo)) {
                    log.error("betRecordDo is null, betRecordId: {}", betRecordId);
                } else {
                    betStatusService.asyncConvertBetStatus(betRecordDo, getGameType());
                }
            }
        } while (CollectionUtils.isNotEmpty(recordIdList));
        log.info("{} schedule run end...", taskName());
    }

    //    public void handle(BetRecordDo betRecordDo) {
    //        try {
    //            if (!BetStatusEnum.ING.getCode().equals(betRecordDo.getStatus())) {
    //                return;
    //            }
    //            log.info("{}, handle in betting BetRecordDo, betRecordId: {}", taskName(), betRecordDo.getId());
    //            // 获取当前订单状态
    //            BetStatusRes betStatusRes = singleWalletService.getBetStatus(buildStatusReq(betRecordDo));
    //            AssertUtil.assertNotNull(betStatusRes, ErrorCode.ERR_INTER);
    //            SingleWalletBetStatusEnum singleWalletBetStatus = valueOfCode(betStatusRes.getCode());
    //            AssertUtil.assertNotNull(singleWalletBetStatus, ErrorCode.ERR_PARAM, "invalid single wallet bet status code");
    //            BetStatusEnum betStatus = BetStatusEnum.valueOfSingleWalletBetStatus(singleWalletBetStatus);
    //            if (Objects.equals(BetStatusEnum.SUCCESS, betStatus)) {
    //                // 更新记录: 状态更新为成功
    //                betRecordService.update(BetRecordDo.builder()
    //                        .id(betRecordDo.getId())
    //                        .betId(betStatusRes.getBetId())
    //                        .status(BetStatusEnum.SUCCESS.getCode())
    //                        .build(), getGameType());
    //            } else if (Objects.equals(BetStatusEnum.FAIL, betStatus)) {
    //                // 更新记录: 状态更新为失败
    //                betRecordService.update(BetRecordDo.builder()
    //                                .id(betRecordDo.getId())
    //                                .betId(betStatusRes.getBetId())
    //                                .status(BetStatusEnum.FAIL.getCode())
    //                                .build(),
    //                        getGameType());
    //            } else {
    //                log.error("{}, handle invalid betStatus: {}, betRecordId: {}, betStatusRes: {}",
    //                        taskName(),
    //                        betStatus,
    //                        betRecordDo.getId(),
    //                        betStatusRes);
    //            }
    //        } catch (Exception e) {
    //            log.error("{}, handle in betting BetRecordDo error, betRecordId: {}, errMsg: {}", taskName(), betRecordDo.getId(), e.getMessage(), e);
    //        }
    //    }
}
