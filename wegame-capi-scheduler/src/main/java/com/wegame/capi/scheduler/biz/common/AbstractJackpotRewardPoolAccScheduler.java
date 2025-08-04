package com.wegame.capi.scheduler.biz.common;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.biz.builder.jackpot.JackpotUserRewardPoolInputRecordQueryBuilder;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.manager.jackpot.LamangJackpotManager;
import com.wegame.capi.biz.service.jackpot.JackpotRewardPoolInputRecordService;
import com.wegame.capi.client.enums.JackpotTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.JackpotAccFlagEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolInputRecordQuery;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Objects;

/**
 * jackpot 奖池累计定时任务<br/>
 */
@Slf4j
public abstract class AbstractJackpotRewardPoolAccScheduler
        extends AbstractScheduler
        implements Runnable {

    private static final long TEN_MINUTES = 10 * 60 * 1000;

    @Autowired
    LamangJackpotManager lamangJackpotManager;
    @Autowired
    JackpotRewardPoolInputRecordService jackpotRewardPoolInputRecordService;

    public abstract JackpotTypeEnum getJackpotTypeEnum();

    @Override
    public void run() {
        log.info("{} schedule run start...", taskName());
        Long gtLastId = null;
        List<JackpotRewardPoolInputRecordDo> recordList;
        do {
            recordList = jackpotRewardPoolInputRecordService.list(JackpotRewardPoolInputRecordQuery.builder()
                    .accFlag(JackpotAccFlagEnum.NO.getCode())
                    .startCreateTime(System.currentTimeMillis() - TEN_MINUTES)
                    .gtLastId(gtLastId)
                    .limit(DEFAULT_PAGE_SIZE)
                    .orderByClause("_id")
                    .direction(Sort.Direction.ASC)
                    .build(), getJackpotTypeEnum());
            if (CollectionUtil.isNotEmpty(recordList)) {
                gtLastId = recordList.get(recordList.size() - 1).getId();
            }
            lamangJackpotManager.accRewardPool(recordList);
        } while (CollectionUtils.isNotEmpty(recordList));
        log.info("{} schedule run end...", taskName());
    }
}
