package com.wegame.capi.biz.service;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.mq.MQMessage;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.listener.PulsarProducer;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MessageTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.pulsar.client.api.MessageId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.betRecordStatusThreadPoolExecutor;

/**
 * 投注状态服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class UnsuccessfulBetPostProcessService {


    //投注中 5 - 20分钟内
    private static final long BETTING_START_TIME_DURATION_IN_MS = 20 * 60 * 1000;
    private static final long BETTING_END_TIME_DURATION_IN_MS = 5 * 60 * 1000;

    // 失败  1 - 15分钟内
    private static final long FAILED_START_TIME_DURATION_IN_MS = 15 * 60 * 1000;
    private static final long FAILED_END_TIME_DURATION_IN_MS = 60 * 1000;
    private static final int DEFAULT_PAGE_SIZE = 100;

    @Autowired
    SingleWalletService singleWalletService;
    @Autowired
    PulsarProducer pulsarProducer;
    @Autowired
    BetRecordService betRecordService;

    @Autowired
    @Qualifier(betRecordStatusThreadPoolExecutor)
    private ThreadPoolExecutor betRecordStatusExecutor;

    @Async(betRecordStatusThreadPoolExecutor)
    public void processUnsuccessfulBetAfterLogin(GameTypeEnum gameType) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        AssertUtil.assertNotNull(userInfo, ErrorCode.ERR_INTER);
        processUnsuccessfulBet(gameType, Long.valueOf(userInfo.getId()));
    }

    @Async(betRecordStatusThreadPoolExecutor)
    public void processUnsuccessfulBet(GameTypeEnum gameType) {
        processUnsuccessfulBet(gameType, null);
    }


    /**
     * 处理投注失败的
     *
     * @param gameType
     * @param userId
     */
    private void processFailedBet(GameTypeEnum gameType, Long userId) {
        Long gtLastId = null;
        List<Long> recordIdList;
        do {
            recordIdList = betRecordService.listId(buildQuery(gtLastId, userId, BetStatusEnum.FAIL), gameType);
            if (CollectionUtil.isNotEmpty(recordIdList)) {
                gtLastId = recordIdList.get(recordIdList.size() - 1);
            }
            for (Long betRecordId : CollectionUtils.emptyIfNull(recordIdList)) {
                BetRecordDo betRecordDo = betRecordService.get(betRecordId, gameType);
                if (Objects.nonNull(betRecordDo) && Objects.nonNull(betRecordDo.getParentId())) {
                    betRecordStatusExecutor.submit(() -> retryBet(betRecordDo, gameType));
                }
            }
        } while (CollectionUtils.isNotEmpty(recordIdList));
    }

    /**
     * 处理投注中的
     *
     * @param gameType
     * @param userId
     */
    private void processInBettingBet(GameTypeEnum gameType, Long userId) {
        Long gtLastId = null;
        List<Long> recordIdList;
        do {
            recordIdList = betRecordService.listId(buildQuery(gtLastId, userId, BetStatusEnum.ING), gameType);
            if (CollectionUtil.isNotEmpty(recordIdList)) {
                gtLastId = recordIdList.get(recordIdList.size() - 1);
            }
            for (Long betRecordId : CollectionUtils.emptyIfNull(recordIdList)) {
                BetRecordDo betRecordDo = betRecordService.get(betRecordId, gameType);
                if (Objects.nonNull(betRecordDo) && Objects.nonNull(betRecordDo.getParentId())) {
                    betRecordStatusExecutor.submit(() -> retryBet(betRecordDo, gameType));
                }
            }
        } while (CollectionUtils.isNotEmpty(recordIdList));
    }

    private void processUnsuccessfulBet(GameTypeEnum gameType, Long userId) {
        if (!isSupportedType(gameType)) {
            return;
        }
        processInBettingBet(gameType, userId);
        processFailedBet(gameType, userId);
    }

    /**
     * 扭转投注状态: 投注失败 -> 投注成功/失败/投注中
     */
    private void retryBet(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        log.info("convertBetStatus betRecordDo: {}", betRecordDo);
        if (!Objects.equals(BetStatusEnum.FAIL.getCode(), betRecordDo.getStatus())) {
            // 只处理投注失败的记录
            log.error("betRecordDo status is not failed, betRecordId: {}", betRecordDo.getId());
            return;
        }
        if (System.currentTimeMillis() - betRecordDo.getCreateTime() >= FAILED_END_TIME_DURATION_IN_MS) {
            log.error("betRecordDo createTime is expired, betRecordId: {}", betRecordDo.getId());
            return;
        }
        SingleWalletBetResultDo resultDo = singleWalletService.betWithPayout(betRecordDo);
        handle(betRecordDo, gameType, resultDo);
    }

    private void handle(BetRecordDo betRecordDo, GameTypeEnum gameType, SingleWalletBetResultDo resultDo) {
        log.info("handle betStatus betRecordId: {}, betStatus: {}", betRecordDo.getId(), resultDo.getBetStatus());
        MessageId messageId = pulsarProducer.syncSendMessage(gameType,
                buildUpdateMessage(betRecordDo, resultDo.getBetStatus(), resultDo.getBetId()),
                100);
        if (Objects.isNull(messageId)) {
            log.error("handle betStatus betRecordId: {}, betStatus: {}, send message fail", betRecordDo.getId(), resultDo.getBetStatus());
        }
    }

    private MQMessage<BetRecordDo> buildUpdateMessage(BetRecordDo betRecordDo, BetStatusEnum betStatus, String betId) {
        return MQMessage.buildMsg(String.valueOf(betRecordDo.getId()),
                MessageTypeEnum.UPDATE.getCode(),
                BetRecordDo.builder()
                        .id(betRecordDo.getId())
                        .betId(betId)
                        .status(betStatus.getCode())
                        .jackpot(betRecordDo.getJackpot())
                        .build());
    }

    private boolean isSupportedType(GameTypeEnum gameType) {
        return GameTypeEnum.MANGKUKULAM.getId().equals(gameType.getId());
    }

    /**
     * 带用户id的时候查询createTime 小于 endTime的
     */
    private BetRecordQuery buildQuery(Long gtLastId, Long userId, BetStatusEnum betStatus) {
        long current = System.currentTimeMillis();
        boolean failed = BetStatusEnum.FAIL.getCode().equals(betStatus.getCode());
        long start = failed ? current - FAILED_START_TIME_DURATION_IN_MS : current - BETTING_START_TIME_DURATION_IN_MS;
        long end = failed ? current - FAILED_END_TIME_DURATION_IN_MS : current - BETTING_END_TIME_DURATION_IN_MS;
        if (Objects.isNull(userId)) {
            return BetRecordQuery.builder()
                    .status(betStatus.getCode())
                    .startCreateTime(start)
                    .endCreateTime(end)
                    .gtLastId(gtLastId)
                    .limit(DEFAULT_PAGE_SIZE)
                    .orderByClause("_id")
                    .direction(Sort.Direction.ASC)
                    .build();
        } else {
            return BetRecordQuery.builder()
                    .status(betStatus.getCode())
                    .endCreateTime(start)
                    .gtLastId(gtLastId)
                    .userId(userId)
                    .limit(DEFAULT_PAGE_SIZE)
                    .orderByClause("_id")
                    .direction(Sort.Direction.ASC)
                    .build();
        }
    }
}