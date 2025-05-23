package com.wegame.capi.scheduler.jackpot;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.biz.service.jackpot.JackpotUserRewardRecordService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardGrantStatusEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;

/**
 * jackpot 派彩定时任务<br/>
 */
@Slf4j
public abstract class AbstractJackpotRewardGrantScheduler
        extends AbstractScheduler
        implements Runnable {

    private static final long THREE_MINUTES = 3 * 60 * 1000;
    private static final long ONE_MINUTES = 1 * 60 * 1000;

    @Autowired
    JackpotUserRewardRecordService jackpotUserRewardRecordService;

    abstract JackpotTypeEnum getJackpotTypeEnum();

    /**
     * 查询中奖记录派奖派奖状态为（未派奖，派奖中)的记录，扭转为已派奖
     */
    @Override
    public void run() {
        log.info("{} schedule run start...", taskName());
        Long gtLastId = null;
        List<Long> recordIdList;
        do {
            recordIdList = jackpotUserRewardRecordService.listId(JackpotUserRewardRecordQuery.builder()
                    .rewardGrantStatus(JackpotRewardGrantStatusEnum.NONE.getCode())
                    .gtLastId(gtLastId)
                    .limit(DEFAULT_PAGE_SIZE)
                    .orderByClause("_id")
                    .direction(Sort.Direction.ASC)
                    .build(), getJackpotTypeEnum());
            if (CollectionUtil.isNotEmpty(recordIdList)) {
                gtLastId = recordIdList.get(recordIdList.size() - 1);
            }
            for (Long recordId : CollectionUtils.emptyIfNull(recordIdList)) {
                JackpotUserRewardRecordDo rewardRecordDo = jackpotUserRewardRecordService.get(recordId, getJackpotTypeEnum());
                if (Objects.isNull(rewardRecordDo)) {
                    log.warn("rewardRecordDo is null, recordId: {}", recordId);
                } else {
                    // todo 调用奖池派彩服务@jayni @tutu

                }
            }
        } while (CollectionUtils.isNotEmpty(recordIdList));
        log.info("{} schedule run end...", taskName());
    }
}
