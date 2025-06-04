package com.wegame.capi.scheduler.jackpot;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotAwardDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotUserRewardRecordDo;
import com.wegame.capi.biz.module.base.jackpot.AbsJackpotHandler;
import com.wegame.capi.biz.module.base.jackpot.JackpotHandlerContext;
import com.wegame.capi.biz.service.BetRecordService;
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
    @Autowired
    JackpotHandlerContext jackpotHandlerContext;
    @Autowired
    BetRecordService betRecordService;

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
                    // 调用奖池派奖
                    this.jackPotHandler(rewardRecordDo);
                }
            }
        } while (CollectionUtils.isNotEmpty(recordIdList));
        log.info("{} schedule run end...", taskName());
    }

    private void jackPotHandler(JackpotUserRewardRecordDo rewardRecordDo) {
        try {
            AbsJackpotHandler jackpotHandler = jackpotHandlerContext.getHandler(this.getJackpotTypeEnum());
            AssertUtil.assertNotNull(jackpotHandler, ErrorCode.ERR_INTER, "jackpotHandler is null");
            // 需要投注记录信息
            BetRecordDo betRecordDo = betRecordService.get(rewardRecordDo.getBetRecordId(), this.getJackpotTypeEnum().getGameType());
            AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_INTER, "betRecordDo is null betRecordId is: " + rewardRecordDo.getBetRecordId());
            JackpotAwardDo jackpotAwardDo = JackpotAwardDo.builder()
                    .pId(betRecordDo.getPId())
                    .channelId(betRecordDo.getChannelId())
                    .playerId(betRecordDo.getPlayerId())
                    .playerName(betRecordDo.getPlayerName())
                    .userId(betRecordDo.getUserId())
                    .jackpotUserRewardRecordDo(rewardRecordDo)
                    .build();
            jackpotHandler.singleAward(jackpotAwardDo);
        } catch (Exception e) {
            log.error("AbstractJackpotRewardGrantScheduler#jackPotHandler error,jackPotType:{}", this.getJackpotTypeEnum(), e);
        }
    }
}
