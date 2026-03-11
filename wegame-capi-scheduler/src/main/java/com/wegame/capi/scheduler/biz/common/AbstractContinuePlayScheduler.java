package com.wegame.capi.scheduler.biz.common;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.exception.LockException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.module.base.bet.strategy.BetStrategy;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.*;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 继续玩定时任务
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

    private static final long START_TIME_DURATION_IN_MS = 10 * 60 * 1000;
    private static final long END_TIME_DURATION_IN_MS = 5 * 60 * 1000;

    private static final AbTypeEnum DEFAULT_AB_TYPE = AbTypeEnum.B;

    @Override
    public void run() {
        // 查询10-5分钟前的 可以继续玩的数据
        try {
            log.info("AbstractContinuePlayScheduler run start. gameId: {}", gameType().getId());
            long startTime = DateUtil.getCurrentMilli();
            long endCreateTime = System.currentTimeMillis() - END_TIME_DURATION_IN_MS;
            long startCreateTime = System.currentTimeMillis() - START_TIME_DURATION_IN_MS;
            Long lastId = null;
            List<Long> detailList;

            BetRecordQuery query = BetRecordQuery.builder()
                    .endCreateTime(endCreateTime)
                    .startCreateTime(startCreateTime)
                    .freeWheelHandleStatus(FreeWheelHandleStatusEnum.PENDING.getCode())
                    .status(BetStatusEnum.SUCCESS.getCode())
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE)
                    .build();
            do {
                query.setGtLastId(lastId);
                detailList = betRecordService.listId(query, gameType());
                if (CollectionUtil.isNotEmpty(detailList)) {
                    lastId = detailList.get(detailList.size() - 1);
                }
                Optional.ofNullable(detailList)
                        .orElse(Collections.emptyList())
                        .stream()
                        .forEach(betRecord ->
                                autoContinuePlayGameThreadPoolExecutor.submit(() -> autoContinuePlay(betRecord)));
            } while (CollectionUtils.isNotEmpty(detailList));
            log.info("AbstractContinuePlayScheduler run end. cost time: {}", DateUtil.getCurrentMilli() - startTime);
        } catch (Exception e) {
            log.error("AbstractContinuePlayScheduler run exception", e);
        }
    }

    private void autoContinuePlay(Long betRecordId) {
        try {
            String lockName = LockNameUtil.getContinuePlayRecordLock(gameType(), betRecordId);
            distributeLock.lockThenRun(lockName, () -> {
                BetRecordDo betRecordDo = betRecordService.getById(betRecordId, gameType(), false);
                if (Objects.equals(FreeWheelHandleStatusEnum.PENDING.getCode(), betRecordDo.getFreeWheelHandleStatus())) {
                    UserInfoContext.setUserInfo(buildUserInfo(betRecordDo));
                    betStrategy().bet(buildBetDo(betRecordDo));
                }
            }, "lock error");
        } catch (LockException lockException) {
            log.info("AbstractContinuePlayScheduler autoContinuePlay lockException");
        }catch (Exception e) {
            log.error("AbstractContinuePlayScheduler autoContinuePlay exception", e);
        } finally {
            UserInfoContext.clear();
        }
    }

    private BetDo buildBetDo(BetRecordDo betRecordDo) {
        BetTypeEnum betType = BetTypeEnum.valueOf(betRecordDo.getBetType());
        BetSubTypeEnum betSubType = BetTypeEnum.NORMAL_BET.getCode().equals(betType.getCode()) ?
                BetSubTypeEnum.FREE_WHEEL_NORMAL_BET : BetSubTypeEnum.FREE_WHEEL_BUY_BET;
        BetPromotionTypeEnum promotionType = BetPromotionTypeEnum.valueOfCode(betRecordDo.getBetPromotionType());
        GamePlayTypeEnum gamePlayType;
        if (Objects.equals(promotionType, BetPromotionTypeEnum.OPERATION_EVENT)) {
            gamePlayType = GamePlayTypeEnum.EVENT_PLAY;
        } else {
            gamePlayType = GamePlayTypeEnum.TEMPLATE_PLAY;
        }


        return BetDo.builder()
                .betAmount(betRecordDo.getBetAmount())
                .payAmount(0L)
                .betPromotionType(promotionType)
                .betType(betType)
                .betSubType(betSubType)
                .gameId(betRecordDo.getGameId())
                .gameType(gameType())
                .parentBetRecordId(betRecordDo.getId())
                .volatility(VolatilityTypeEnum.random())
                .gamePlayType(gamePlayType)
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
                .ut(DEFAULT_AB_TYPE.getId())
                .build();
    }
}
