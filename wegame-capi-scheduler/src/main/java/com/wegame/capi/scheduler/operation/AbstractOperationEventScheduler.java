package com.wegame.capi.scheduler.operation;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.biz.manager.operation.AbstractOperationEventManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.operation.OperationEventDetailService;
import com.wegame.capi.biz.service.operation.OperationEventService;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.OperationEventStatusEnum;
import com.wegame.capi.common.enums.OperationEventSubStatusEnum;
import com.wegame.capi.common.enums.OperationEventReportStatusEnum;
import com.wegame.capi.dao.repository.domain.operation.OperationEventDetailQuery;
import com.wegame.capi.dao.repository.domain.operation.OperationEventQuery;
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

/**
 * 抽象干预事件定时任务
 */
@Slf4j
@Component
public abstract class AbstractOperationEventScheduler
        extends AbstractScheduler
        implements Runnable {

    @Autowired
    public OperationEventService operationEventService;
    @Autowired
    public OperationEventDetailService operationEventDetailService;
    @Autowired
    public BetRecordService betRecordService;

    abstract OperationEventTypeEnum operationEventType();

    abstract AbstractOperationEventManager operationEventManager();

    @Override
    public void run() {
        // 事件明细中未处理成功
        eventDetailIngExec();
        // 事件状态已完成,但未上报
        eventReportFailExec();
        // 事件已过期,但未上报
        eventEventExpiredExec();
        // 处理已完成的事件
        eventFinishFailExec();
    }

    /**
     * 处理中的事件明细
     */
    private void eventDetailIngExec() {
        try {
            GameTypeEnum gameType = operationEventType().getGameType();
            log.info("eventDetailIngExec. gameId: {}", gameType.getId());
            long currentTimeMillis = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<OperationEventDetailDo> detailList;
            OperationEventDetailQuery query = OperationEventDetailQuery.builder()
                    .betStatus(BetStatusEnum.ING.getCode())
                    .gtCreateTime(currentTimeMillis - Constants.TWO_HOURS_AGO)
                    .ltCreateTime(currentTimeMillis - Constants.FIVE_MINUTES_AGO)
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE)
                    .build();
            do {
                query.setGtLastId(lastId);
                detailList = operationEventDetailService.list(query, operationEventType());
                if (CollectionUtil.isNotEmpty(detailList)) {
                    lastId = detailList.get(detailList.size() - 1).getId();
                }
                Optional.ofNullable(detailList)
                        .orElse(Collections.emptyList())
                        .forEach(this::eventDetailFailHandle);
            } while (CollectionUtils.isNotEmpty(detailList));
            log.info("eventDetailIngExec run end. gameId: {}, cost time:{}",
                    gameType.getId(),
                    DateUtil.getCurrentMilli() - currentTimeMillis);
        } catch (Exception e) {
            log.error("eventDetailIngExec run exception", e);
        }
    }

    /**
     * 执行处理中事件
     */
    private void eventDetailFailHandle(OperationEventDetailDo eventDetailDo) {
        try {
            GameTypeEnum gameType = operationEventType().getGameType();
            // 查询原始订单的状态
            OperationEventDetailDo eventDetailUpdate = null;
            OperationEventDo eventDoUpdate = null;
            BetRecordDo recordDo = betRecordService.get(eventDetailDo.getBetRecordId(), gameType);
            if (recordDo == null) {
                eventDetailUpdate = OperationEventDetailDo.builder()
                        .id(eventDetailDo.getId())
                        .betStatus(BetStatusEnum.FAIL)
                        .build();
            } else {
                if (Objects.equals(BetStatusEnum.ING.getCode(), recordDo.getStatus()) || Objects.equals(BetStatusEnum.SUCCESS.getCode(), recordDo.getStatus())) {
                    // 进行中或者成功 都按照成功计算
                    eventDetailUpdate = OperationEventDetailDo.builder()
                            .id(eventDetailDo.getId())
                            .betId(recordDo.getBetId())
                            .betStatus(BetStatusEnum.SUCCESS)
                            .build();

                    OperationEventDo eventDo = operationEventService.getById(eventDetailDo.getEventRecordId(), operationEventType());
                    long finishBonus = eventDo.getFinishBonus() == null ? 0L : eventDo.getFinishBonus();
                    long eventBonus = finishBonus + eventDetailDo.getEventAmount();
                    eventDoUpdate = OperationEventDo.builder()
                            .id(eventDetailDo.getEventRecordId())
                            .finishBonus(eventBonus).build();
                    if (eventDetailDo.getEventAmount() + finishBonus > eventDo.getMinBonus()) {
                        eventDoUpdate.setEventStatus(OperationEventStatusEnum.FINISH);
                        eventDoUpdate.setEventSubStatus(OperationEventSubStatusEnum.FINISH_NORMAL);
                    }
                } else if (Objects.equals(BetStatusEnum.FAIL.getCode(), recordDo.getStatus())) {
                    eventDetailUpdate = OperationEventDetailDo.builder()
                            .id(eventDetailDo.getId())
                            .betStatus(BetStatusEnum.FAIL)
                            .build();

                }
            }
            operationEventManager().updateEventDetail(eventDetailUpdate, eventDoUpdate);
            operationEventService.updateOperationUseStatus(eventDetailDo.getEventRecordId(), OperationEventStatusEnum.NONE, operationEventType());
            if (eventDoUpdate != null && OperationEventStatusEnum.FINISH == eventDoUpdate.getEventStatus()) {
                operationEventManager().reportEventById(eventDetailDo.getBetRecordId(), true);
            }
        } catch (Exception e) {
            log.error("eventDetailFailHandle exception", e);
        }
    }


    /**
     * 事件已经完成,但未上报
     */
    private void eventReportFailExec() {
        try {
            GameTypeEnum gameType = operationEventType().getGameType();
            log.info("eventReportFailExec run start. gameId: {}", gameType.getId());
            long currentTimeMillis = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<OperationEventDo> eventDetailDoList;
            OperationEventQuery query = OperationEventQuery.builder()
                    .reportStatus(OperationEventReportStatusEnum.NONE.getCode())
                    .eventStatus(OperationEventStatusEnum.FINISH.getCode())
                    .updateTimeGt(currentTimeMillis - Constants.TWO_HOURS_AGO)
                    .updateTimeLt(currentTimeMillis - Constants.FIVE_MINUTES_AGO)
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE).build();
            do {
                query.setGtLastId(lastId);
                eventDetailDoList = operationEventService.queryList(query, operationEventType());
                if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                    lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
                }
                Optional.ofNullable(eventDetailDoList)
                        .orElse(Collections.emptyList())
                        .forEach(this::eventReportFailHandle);
            } while (CollectionUtils.isNotEmpty(eventDetailDoList));
            log.info("eventReportFailExec run end. gameId: {}, cost time: {}",
                    gameType.getId(),
                    DateUtil.getCurrentMilli() - currentTimeMillis);
        } catch (Throwable e) {
            log.error("eventReportFailExec exception", e);

        }
    }

    /**
     * 事件处理成功,但未上报
     */
    private void eventReportFailHandle(OperationEventDo eventDo) {
        operationEventManager().reportEvent(eventDo, true);
    }

    /**
     * 事件已过期
     */
    private void eventEventExpiredExec() {
        try {
            GameTypeEnum gameType = operationEventType().getGameType();
            log.info("eventEventExpiredExec run start. gameId:{}", gameType.getId());
            long currentTimeMillis = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<OperationEventDo> eventDetailDoList;
            OperationEventQuery query = OperationEventQuery.builder()
                    .reportStatus(OperationEventReportStatusEnum.NONE.getCode())
                    .expiredTimeLt(DateUtil.getCurrentMilli())
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE).build();
            do {
                query.setGtLastId(lastId);
                eventDetailDoList = operationEventService.queryList(query, operationEventType());
                if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                    lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
                }
                Optional.ofNullable(eventDetailDoList)
                        .orElse(Collections.emptyList())
                        .forEach(this::eventEventIngHandle);
            } while (CollectionUtils.isNotEmpty(eventDetailDoList));
            log.info("eventEventExpiredExec run end. gameId: {}, cost time: {}",
                    gameType.getId(),
                    DateUtil.getCurrentMilli() - currentTimeMillis);
        } catch (Throwable e) {
            log.error("eventEventExpiredExec exception", e);
        }
    }

    private void eventEventIngHandle(OperationEventDo eventDo) {
        if (eventDo.getExpiredTime() < DateUtil.getCurrentMilli()) {
            // 过期上报
            operationEventManager().updateFinishEvent(eventDo.getId(), Boolean.TRUE, OperationEventSubStatusEnum.FINISH_EXPIRED);
        }
    }


    /**
     * 事件已经完成,但未处理
     */
    private void eventFinishFailExec() {
        try {
            GameTypeEnum gameType = operationEventType().getGameType();
            log.info("eventFinishFailExec run start. gameId: {}", gameType.getId());
            long currentTimeMillis = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<OperationEventDo> eventDetailDoList;
            OperationEventQuery query = OperationEventQuery.builder()
                    .reportStatus(OperationEventReportStatusEnum.NONE.getCode())
                    .eventStatus(OperationEventStatusEnum.NONE.getCode())
                    .updateTimeGt(currentTimeMillis - Constants.TWO_HOURS_AGO)
                    .updateTimeLt(currentTimeMillis - Constants.FIVE_MINUTES_AGO)
                    .finishMinAmount(gameType.getMinBetAmount() * Constants.MIN_BET_AMOUNT_M)
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE).build();
            do {
                query.setGtLastId(lastId);
                eventDetailDoList = operationEventService.queryList(query, operationEventType());
                if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                    lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
                }
                Optional.ofNullable(eventDetailDoList)
                        .orElse(Collections.emptyList())
                        .forEach(this::eventFinishFailExecHandle);
            } while (CollectionUtils.isNotEmpty(eventDetailDoList));
            log.info("eventFinishFailExec run end. gameId: {}, cost time: {}",
                    gameType.getId(),
                    DateUtil.getCurrentMilli() - currentTimeMillis);
        } catch (Throwable e) {
            log.error("eventFinishFailExec exception", e);
        }
    }

    private void eventFinishFailExecHandle(OperationEventDo eventDo) {
        if (eventDo.getFinishBonus() >= eventDo.getMinBonus()) {
            operationEventManager().updateFinishEvent(eventDo.getId(), Boolean.TRUE, OperationEventSubStatusEnum.FINISH_NORMAL);
            return;
        }
        if (Objects.equals(eventDo.getFinishBonus(), 0L)) {
            operationEventManager().updateFinishEvent(eventDo.getId(), Boolean.TRUE, OperationEventSubStatusEnum.FINISH_AMOUNT_ERROR_NOT_DETAIL);
        } else {
            operationEventManager().updateFinishEvent(eventDo.getId(), Boolean.TRUE, OperationEventSubStatusEnum.FINISH_AMOUNT_ERROR_FIND_DETAIL);
        }
    }
}
