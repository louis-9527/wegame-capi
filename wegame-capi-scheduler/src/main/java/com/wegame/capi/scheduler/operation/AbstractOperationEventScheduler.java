package com.wegame.capi.scheduler.operation;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.biz.manager.AbstractOperationEventManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.operation.OperationEventDetailService;
import com.wegame.capi.biz.service.operation.OperationEventService;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.OperationEventStatusEnum;
import com.wegame.capi.common.enums.OperationEventSubStatusEnum;
import com.wegame.capi.common.enums.OperationEventReportStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.OperationEventDetailQuery;
import com.wegame.capi.dao.mapper.domain.business.OperationEventQuery;
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

    private final long FIVE_MINUTES_AGO = System.currentTimeMillis() - 10 * 60 * 1000;

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
            log.info("{}#eventDetailFailExec run start. gameId: {}", getClass().getSimpleName(), gameType.getId());
            long startTime = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<DynamicOperationEventDetailDo> detailList;
            OperationEventDetailQuery query = OperationEventDetailQuery.builder()
                    .betStatus(BetStatusEnum.ING.getCode())
                    .createTimeLt(FIVE_MINUTES_AGO)
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE)
                    .build();
            do {
                query.setLastId(lastId);
                detailList = operationEventDetailService.list(query, operationEventType());
                if (CollectionUtil.isNotEmpty(detailList)) {
                    lastId = detailList.get(detailList.size() - 1).getId();
                }
                Optional.ofNullable(detailList)
                        .orElse(Collections.emptyList())
                        .forEach(this::eventDetailFailHandle);
            } while (CollectionUtils.isNotEmpty(detailList));
            log.info("{}#eventDetailFailExec run end:{},time:{}", getClass().getSimpleName(),
                    gameType.getId(),
                    DateUtil.getCurrentMilli() - startTime);
        } catch (Exception e) {
            log.error("{}#eventDetailFailExec run exception:{}", getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    /**
     * 执行处理中事件
     */
    private void eventDetailFailHandle(DynamicOperationEventDetailDo eventDetailDo) {
        try {
            GameTypeEnum gameType = operationEventType().getGameType();
            Long betRecordId = eventDetailDo.getBetRecordId();
            // 查询原始订单的状态
            BetRecordDo recordDo = betRecordService.get(betRecordId, gameType);
            if (Objects.equals(BetStatusEnum.ING.getCode(), recordDo.getStatus())) {
                return;
            }
            BetStatusEnum eventDetailStatus = null;
            OperationEventStatusEnum newEventStatus = OperationEventStatusEnum.NONE;
            OperationEventSubStatusEnum eventSubStatus = null;
            // 获取事件信息
            OperationEventDo eventDo = operationEventService.getById(betRecordId, operationEventType());
            if (Objects.equals(BetStatusEnum.FAIL.getCode(), recordDo.getStatus())) {
                eventDetailStatus = BetStatusEnum.FAIL;
            } else if (Objects.equals(BetStatusEnum.SUCCESS.getCode(), recordDo.getStatus())) {
                eventDetailStatus = BetStatusEnum.SUCCESS;
                if (eventDetailDo.getEventAmount() + eventDo.getFinishBonus() > eventDo.getMinBonus()) {
                    eventSubStatus = OperationEventSubStatusEnum.FINISH_NORMAL;
                    newEventStatus = OperationEventStatusEnum.FINISH;
                }
            }
            // 更新订单信息
            DynamicOperationEventDetailDo eventDetailUpdate = DynamicOperationEventDetailDo.builder()
                    .id(eventDetailDo.getId())
                    .betStatus(eventDetailStatus)
                    .betId(recordDo.getBetId())
                    .build();
            OperationEventDo eventDoUpdate = OperationEventDo.builder()
                    .id(eventDo.getId())
                    .eventSubStatus(eventSubStatus)
                    .finishBonus(eventDo.getFinishBonus() + eventDetailDo.getEventAmount())
                    .eventStatus(newEventStatus).build();
            operationEventManager().updateEventDetail(eventDetailUpdate, eventDoUpdate);
            if (OperationEventStatusEnum.FINISH == newEventStatus) {
                operationEventManager().asyncReportEvent(eventDo.getId());
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
                    .createTimeLt(FIVE_MINUTES_AGO)
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE).build();
            do {
                query.setLtLastId(lastId);
                eventDetailDoList = operationEventService.queryList(query, operationEventType());
                if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                    lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
                }
                Optional.ofNullable(eventDetailDoList)
                        .orElse(Collections.emptyList())
                        .forEach(this::eventReportFailHandle);
            } while (CollectionUtils.isNotEmpty(eventDetailDoList));
            log.info("AbstractDynamicOperationEventScheduler#eventReportFailExec run end:{},time:{}", gameType.getId(), DateUtil.getCurrentMilli() - startTime);
        }catch (Throwable e) {
            log.error("{}#eventReportFailExec exception:{}", getClass().getSimpleName(), e.getMessage(), e);

        }
    }

    /**
     * 事件处理成功,但未上报
     */
    private void eventReportFailHandle(OperationEventDo eventDo) {
        operationEventManager().asyncReportEvent(eventDo.getId());
    }

    /**
     * 事件已过期
     */
    private void eventEventExpiredExec() {
        try {
            GameTypeEnum gameType = operationEventType().getGameType();
            log.info("{}#eventEventIngExec run start:{}", getClass().getSimpleName(), gameType.getId());
            long startTime = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<OperationEventDo> eventDetailDoList;
            OperationEventQuery query = OperationEventQuery.builder()
                    .reportStatus(OperationEventReportStatusEnum.NONE.getCode())
                    .expiredTimeLt(DateUtil.getCurrentMilli())
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE).build();
            do {
                query.setLtLastId(lastId);
                eventDetailDoList = operationEventService.queryList(query, operationEventType());
                if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                    lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
                }
                Optional.ofNullable(eventDetailDoList)
                        .orElse(Collections.emptyList())
                        .forEach(this::eventEventIngHandle);
            } while (CollectionUtils.isNotEmpty(eventDetailDoList));
            log.info("{}#eventEventExpiredExec run end:{}, cost time:{}", getClass().getSimpleName(), gameType.getId(), DateUtil.getCurrentMilli() - startTime);
        }catch (Throwable e) {
            log.error("{}#eventEventExpiredExec exception:{}", getClass().getSimpleName(), e.getMessage(), e);

        }

    }

    private void eventEventIngHandle(OperationEventDo eventDo) {
        if (eventDo.getExpiredTime() < DateUtil.getCurrentMilli()) {
            // 过期上报
            operationEventManager().asyncUpdateFinishEvent(eventDo.getId(), OperationEventSubStatusEnum.FINISH_EXPIRED);
        }
    }
}
