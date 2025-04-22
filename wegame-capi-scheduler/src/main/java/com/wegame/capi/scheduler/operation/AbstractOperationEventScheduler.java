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

    private final long FIVE_MINUTES_AGO = System.currentTimeMillis() - 5 * 60 * 1000;

    private final long MONTH_DAY_AGO = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);


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
    }

    /**
     * 处理中的事件明细
     */
    private void eventDetailIngExec() {
        try {
            GameTypeEnum gameType = operationEventType().getGameType();
            log.info("{}#eventDetailIngExec. gameId: {}", getClass().getSimpleName(), gameType.getId());
            long startTime = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<OperationEventDetailDo> detailList;
            OperationEventDetailQuery query = OperationEventDetailQuery.builder()
                    .betStatus(BetStatusEnum.ING.getCode())
                    .gtCreateTime(MONTH_DAY_AGO)
                    .ltCreateTime(FIVE_MINUTES_AGO)
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
            log.info("{}#eventDetailIngExec run end:{},time:{}", getClass().getSimpleName(),
                    gameType.getId(),
                    DateUtil.getCurrentMilli() - startTime);
        } catch (Exception e) {
            log.error("{}#eventDetailIngExecrun exception:{}", getClass().getSimpleName(), e.getMessage(), e);
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
                if (Objects.equals(BetStatusEnum.ING.getCode(), recordDo.getStatus())) {
                    // 原始订单处理中 则等待
                    return;
                }
                // 获取事件信息
                if (Objects.equals(BetStatusEnum.FAIL.getCode(), recordDo.getStatus())) {
                    eventDetailUpdate = OperationEventDetailDo.builder()
                            .id(eventDetailDo.getId())
                            .betStatus(BetStatusEnum.FAIL)
                            .build();

                } else if (Objects.equals(BetStatusEnum.SUCCESS.getCode(), recordDo.getStatus())) {
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
                }
            }
            operationEventManager().updateEventDetail(eventDetailUpdate, eventDoUpdate);
            operationEventService.updateOperationUseStatus(eventDetailDo.getEventRecordId(),OperationEventStatusEnum.NONE, operationEventType());
            if (eventDoUpdate != null && OperationEventStatusEnum.FINISH == eventDoUpdate.getEventStatus()) {
                operationEventManager().asyncReportEvent(eventDetailDo.getBetRecordId());
            }
        } catch (Exception e) {
            log.error("{}#eventDetailFailHandle exception:{}", getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    /**
     * 事件已经完成,但未上报
     */
    private void eventReportFailExec() {
        try {
            GameTypeEnum gameType = operationEventType().getGameType();
            log.info("AbstractDynamicOperationEventScheduler#eventReportFailExec run start:{}", gameType.getId());
            long startTime = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<OperationEventDo> eventDetailDoList;
            OperationEventQuery query = OperationEventQuery.builder()
                    .reportStatus(OperationEventReportStatusEnum.NONE.getCode())
                    .eventStatus(OperationEventStatusEnum.FINISH.getCode())
                    .updateTimeGt(MONTH_DAY_AGO)
                    .updateTimeLt(FIVE_MINUTES_AGO)
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
            log.info("AbstractDynamicOperationEventScheduler#eventReportFailExec run end:{},time:{}", gameType.getId(), DateUtil.getCurrentMilli() - startTime);
        } catch (Throwable e) {
            log.error("{}#eventReportFailExec exception:{}", getClass().getSimpleName(), e.getMessage(), e);

        }
    }

    /**
     * 事件处理成功,但未上报
     */
    private void eventReportFailHandle(OperationEventDo eventDo) {
        operationEventManager().asyncReportEvent(eventDo);
    }

    /**
     * 事件已过期
     */
    private void eventEventExpiredExec() {
        try {
            GameTypeEnum gameType = operationEventType().getGameType();
            log.info("{}#eventEventExpiredExec run start:{}", getClass().getSimpleName(), gameType.getId());
            long startTime = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<OperationEventDo> eventDetailDoList;
            OperationEventQuery query = OperationEventQuery.builder()
                    .reportStatus(OperationEventReportStatusEnum.NONE.getCode())
                    .expiredTimeLt(DateUtil.getCurrentMilli())
                    .updateTimeGt(MONTH_DAY_AGO)
                    .updateTimeLt(FIVE_MINUTES_AGO)
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
            log.info("{}#eventEventExpiredExec run end:{}, cost time:{}", getClass().getSimpleName(), gameType.getId(), DateUtil.getCurrentMilli() - startTime);
        } catch (Throwable e) {
            log.error("{}#eventEventExpiredExec exception:{}", getClass().getSimpleName(), e.getMessage(), e);

        }

    }

    private void eventEventIngHandle(OperationEventDo eventDo) {
        if (eventDo.getExpiredTime() < DateUtil.getCurrentMilli()) {
            // 过期上报
            operationEventManager().asyncUpdateFinishEvent(eventDo.getId(), eventDo.getPlayerName(), OperationEventSubStatusEnum.FINISH_EXPIRED);
        }
    }
}
