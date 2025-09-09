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
import com.wegame.capi.client.enums.BetSubTypeEnum;
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

import java.util.Arrays;
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
public class FailedBetPostProcessService {

    // 定时任务扫描 失败  - 15分钟内
    private static final long SCAN_TIME_DURATION_FOR_SCHEDULER_IN_MS = 15 * 60 * 1000;
    // 用户登陆时候查询时间范围 - 30天内
    private static final long SCAN_TIME_DURATION_FOR_LOGIN_IN_MS = 30 * 24 * 60 * 60 * 1000L;
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final List<String> SUPPORTED_SUB_TYPES = Arrays.asList(BetSubTypeEnum.FREE_WHEEL_BUY_BET.getCode(),
            BetSubTypeEnum.FREE_WHEEL_NORMAL_BET.getCode());

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
    public void processFailedBetAfterLogin(GameTypeEnum gameType, Long userId) {
        AssertUtil.assertNotNull(userId, ErrorCode.ERR_INTER);
        process(gameType, userId);
    }

    @Async(betRecordStatusThreadPoolExecutor)
    public void processFailedBet(GameTypeEnum gameType) {
        process(gameType, null);
    }

    private void process(GameTypeEnum gameType, Long userId) {
        if (!isSupportedType(gameType)) {
            return;
        }
        processFailedBet(gameType, userId);
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
            recordIdList = betRecordService.listId(buildQuery(gtLastId, userId), gameType);
            if (CollectionUtil.isNotEmpty(recordIdList)) {
                gtLastId = recordIdList.get(recordIdList.size() - 1);
            }
            for (Long betRecordId : CollectionUtils.emptyIfNull(recordIdList)) {
                BetRecordDo betRecordDo = betRecordService.get(betRecordId, gameType);
                if (Objects.nonNull(betRecordDo) && BetSubTypeEnum.isSecondStep(betRecordDo.getBetSubType())) {
                    betRecordStatusExecutor.submit(() -> retryBet(betRecordDo, gameType));
                }
            }
        } while (CollectionUtils.isNotEmpty(recordIdList));
    }

    /**
     * 扭转投注状态: 投注失败 -> 投注成功/失败/投注中
     */
    private void retryBet(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(betRecordDo, ErrorCode.ERR_INTER);
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER);
        log.info("retryBet betRecordDo: {}", betRecordDo);
        if (!Objects.equals(BetStatusEnum.FAIL.getCode(), betRecordDo.getStatus())) {
            // 只处理失败的记录
            log.error("betRecordDo status is success, betRecordId: {}", betRecordDo.getId());
            return;
        }
        rebuildBetRecord(betRecordDo);
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

    /**
     * 将createTime改为当前事件，防止调用convertStatus被过滤掉
     *
     * @param betRecordDo
     */
    private void rebuildBetRecord(BetRecordDo betRecordDo) {
        if (Objects.nonNull(betRecordDo)) {
            betRecordDo.setCreateTime(System.currentTimeMillis());
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
     * 构建失败投注的查询
     */
    private BetRecordQuery buildQuery(Long gtLastId, Long userId) {
        long current = System.currentTimeMillis();
        // todo 校验parentId
        if (Objects.nonNull(userId)) {
            return BetRecordQuery.builder()
                    .status(BetStatusEnum.FAIL.getCode())
                    .startCreateTime(current - SCAN_TIME_DURATION_FOR_LOGIN_IN_MS)
                    .endCreateTime(current)
                    .gtLastId(gtLastId)
                    .userId(userId)
                    .betSubTypes(SUPPORTED_SUB_TYPES)
                    .limit(DEFAULT_PAGE_SIZE)
                    .orderByClause("_id")
                    .direction(Sort.Direction.ASC)
                    .build();
        } else {
            return BetRecordQuery.builder()
                    .status(BetStatusEnum.FAIL.getCode())
                    .startCreateTime(current - SCAN_TIME_DURATION_FOR_SCHEDULER_IN_MS)
                    .endCreateTime(current)
                    .gtLastId(gtLastId)
                    .betSubTypes(SUPPORTED_SUB_TYPES)
                    .limit(DEFAULT_PAGE_SIZE)
                    .orderByClause("_id")
                    .direction(Sort.Direction.ASC)
                    .build();
        }
    }

}