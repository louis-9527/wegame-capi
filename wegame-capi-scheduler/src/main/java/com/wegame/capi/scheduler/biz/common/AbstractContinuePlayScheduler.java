package com.wegame.capi.scheduler.biz.common;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.module.base.bet.strategy.BetStrategy;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.VolatilityTypeEnum;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 星卡定时任务
 */
@Slf4j
@Component
public abstract class AbstractContinuePlayScheduler
        extends AbstractScheduler
        implements Runnable {

    @Autowired
    public BetRecordService betRecordService;

    @Autowired
    ThreadPoolExecutor autoContinuePlayGameThreadPoolExecutor;

    public abstract GameTypeEnum gameType();

    public abstract BetStrategy betStrategy();

    @Override
    public void run() {
        // 查询30-3分钟前的 可以继续玩的数据
        try {
            log.info("AbstractContinuePlayScheduler run start. gameId: {}", gameType().getId());
            long startTime = DateUtil.getCurrentMilli();
            long endCreateTime = System.currentTimeMillis() - (6 * 60 * 1000);
            long startCreateTime = System.currentTimeMillis() - (60 * 60 * 1000);
            Long lastId = null;
            List<BetRecordDo> detailList;

            BetRecordQuery query = BetRecordQuery.builder()
                    .endCreateTime(endCreateTime)
                    .startCreateTime(startCreateTime)
                    .continueStatus(true)
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE)
                    .build();
            do {
                query.setGtLastId(lastId);
                detailList = betRecordService.list(query, gameType());
                if (CollectionUtil.isNotEmpty(detailList)) {
                    lastId = detailList.get(detailList.size() - 1).getId();
                }
                Optional.ofNullable(detailList)
                        .orElse(Collections.emptyList())
                        .forEach(betRecord ->
                                autoContinuePlayGameThreadPoolExecutor.submit(() -> autoContinuePlay(betRecord)));
            } while (CollectionUtils.isNotEmpty(detailList));
            log.info("AbstractContinuePlayScheduler run end. cost time: {}", DateUtil.getCurrentMilli() - startTime);
        } catch (Exception e) {
            log.error("AbstractContinuePlayScheduler run exception", e);
        }
    }

    private void autoContinuePlay(BetRecordDo betRecordDo) {
        try {
            UserInfoContext.setUserInfo(buildUserInfo(betRecordDo));
            betStrategy().bet(buildBetDo(betRecordDo));
        } catch (Exception e) {
            //todo
            log.error("AbstractContinuePlayScheduler autoContinuePlay exception", e);
        } finally {
            UserInfoContext.clear();
        }
    }

    private BetDo buildBetDo(BetRecordDo betRecordDo) {
        BetTypeEnum betType = BetTypeEnum.valueOf(betRecordDo.getBetType());
        BetSubTypeEnum betSubType = BetTypeEnum.NORMAL_BET.getCode().equals(betType.getCode()) ?
                BetSubTypeEnum.FREE_WHEEL_NORMAL_BET : BetSubTypeEnum.FREE_WHEEL_BUY_BET;

        return BetDo.builder()
                .betAmount(betRecordDo.getBetAmount())
                .payAmount(0L)
                .betPromotionType(BetPromotionTypeEnum.valueOfCode(betRecordDo.getBetPromotionType()))
                .betType(betType)
                .betSubType(betSubType)
                .gameId(betRecordDo.getGameId())
                .gameType(gameType())
                .parentBetRecordId(betRecordDo.getId())
                .volatility(VolatilityTypeEnum.random())
                .build();
    }

    private UserInfo buildUserInfo(BetRecordDo betRecordDo) {
        return UserInfo.builder()
                .playerId(betRecordDo.getPlayerId())
                .playerName(betRecordDo.getPlayerName())
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .gameId(betRecordDo.getGameId())
                .id(String.valueOf(betRecordDo.getUserId()))
                .build();
    }
}
