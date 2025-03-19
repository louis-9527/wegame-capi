package com.wegame.capi.scheduler.event;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.manager.AbstractDynamicOperationManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.DynamicOperationEventDetailService;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.DynamicOperationGameEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventSubStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationDetailQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
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

@Slf4j
@Component
public abstract class AbstractDynamicOperationEventScheduler
        extends AbstractScheduler
        implements Runnable {

    private final long fiveMinutesAgo = System.currentTimeMillis() - 10 * 60 * 1000;

    @Autowired
    public DynamicOperationEventService dynamicOperationEventService;
    @Autowired
    public DynamicOperationEventDetailService dynamicOperationEventDetailService;
    @Autowired
    public BetRecordService betRecordService;

    abstract DynamicOperationGameEnum getDynamicOperationGame();

    abstract AbstractDynamicOperationManager getDynamicOperationManager();

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
            GameTypeEnum gameType = getDynamicOperationGame().getGameType();
            log.info("{}#eventDetailFailExec run start:{}", getClass().getSimpleName(), gameType.getId());
            long startTime = DateUtil.getCurrentMilli();
            Long lastId = null;
            List<DynamicOperationEventDetailDo> eventDetailDoList = null;
            DynamicOperationDetailQuery query = DynamicOperationDetailQuery.builder()
                    .betStatus(BetStatusEnum.ING.getCode())
                    .createTimeLt(fiveMinutesAgo)
                    .direction(Sort.Direction.ASC)
                    .orderByClause("_id")
                    .limit(DEFAULT_PAGE_SIZE)
                    .build();
            do {
                query.setLastId(lastId);
                eventDetailDoList = dynamicOperationEventDetailService.queryList(query, getDynamicOperationGame());
                if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                    lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
                }
                Optional.ofNullable(eventDetailDoList)
                        .orElse(Collections.emptyList())
                        .forEach(this::eventDetailFailHandle);
            } while (CollectionUtils.isNotEmpty(eventDetailDoList));
            log.info("{}#eventDetailFailExec run end:{},time:{}", getClass().getSimpleName(),
                    gameType.getId(),
                    DateUtil.getCurrentMilli() - startTime);
        } catch (Exception e) {
            log.error("{}#eventDetailFailExec run exception:{}", getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    /**
     * 执行处理中事件
     *
     * @param eventDetailDo
     */
    private void eventDetailFailHandle(DynamicOperationEventDetailDo eventDetailDo) {
        GameTypeEnum gameType = getDynamicOperationGame().getGameType();
        Long betRecordId = eventDetailDo.getBetRecordId();
        // 查询原始订单的状态
        BetRecordDo recordDo = betRecordService.get(betRecordId, gameType);
        if (Objects.equals(BetStatusEnum.ING.getCode(), recordDo.getStatus())) {
            return;
        }
        BetStatusEnum eventDetailStatus = null;
        DynamicOperationEventStatusEnum newEventStatus = DynamicOperationEventStatusEnum.NONE;
        DynamicOperationEventSubStatusEnum eventSubStatus = null;
        // 获取事件信息
        DynamicOperationEventDo eventDo = dynamicOperationEventService.getById(betRecordId, getDynamicOperationGame());
        if (Objects.equals(BetStatusEnum.FAIL.getCode(), recordDo.getStatus())) {
            eventDetailStatus = BetStatusEnum.FAIL;
        } else if (Objects.equals(BetStatusEnum.SUCCESS.getCode(), recordDo.getStatus())) {
            eventDetailStatus = BetStatusEnum.SUCCESS;
            if (eventDetailDo.getEventAmount() + eventDo.getFinishBonus() > eventDo.getMinBonus()) {
                eventSubStatus = DynamicOperationEventSubStatusEnum.FINISH_NORMAL;
                newEventStatus = DynamicOperationEventStatusEnum.FINISH;
            }
        }
        // 更新订单信息
        DynamicOperationEventDetailDo eventDetailUpdate = DynamicOperationEventDetailDo.builder()
                .id(eventDetailDo.getId())
                .betStatus(eventDetailStatus)
                .betId(recordDo.getBetId())
                .build();
        DynamicOperationEventDo eventDoUpdate = DynamicOperationEventDo.builder()
                .id(eventDo.getId())
                .eventSubStatus(eventSubStatus)
                .finishBonus(eventDo.getFinishBonus() + eventDetailDo.getEventAmount())
                .eventStatus(newEventStatus).build();
        getDynamicOperationManager().updateEventDetail(eventDetailUpdate, eventDoUpdate);
        if (DynamicOperationEventStatusEnum.FINISH == newEventStatus) {
            getDynamicOperationManager().asyncReportEvent(eventDo.getId());
        }
    }

    /**
     * 事件已经完成,但未上报
     */
    private void eventReportFailExec() {
        GameTypeEnum gameType = getDynamicOperationGame().getGameType();
        log.info("AbstractDynamicOperationEventScheduler#eventReportFailExec run start:{}", gameType.getId());

        long startTime = DateUtil.getCurrentMilli();
        Long lastId = null;
        List<DynamicOperationEventDo> eventDetailDoList = null;
        DynamicOperationQuery query = DynamicOperationQuery.builder()
                .reportStatus(DynamicOperationReportStatusEnum.NONE.getCode())
                .eventStatus(DynamicOperationEventStatusEnum.FINISH.getCode())
                .createTimeLt(fiveMinutesAgo)
                .direction(Sort.Direction.ASC)
                .orderByClause("_id")
                .limit(DEFAULT_PAGE_SIZE).build();
        do {
            query.setLtLastId(lastId);
            eventDetailDoList = dynamicOperationEventService.queryList(query, getDynamicOperationGame());
            if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
            }
            Optional.ofNullable(eventDetailDoList)
                    .orElse(Collections.emptyList())
                    .forEach(this::eventReportFailHandle);
        } while (CollectionUtils.isNotEmpty(eventDetailDoList));
        log.info("AbstractDynamicOperationEventScheduler#eventReportFailExec run end:{},time:{}", gameType.getId(), DateUtil.getCurrentMilli() - startTime);
    }

    /**
     * 事件处理成功,但未上报
     *
     * @param eventDo
     */
    private void eventReportFailHandle(DynamicOperationEventDo eventDo) {
        getDynamicOperationManager().asyncReportEvent(eventDo.getId());
    }

    /**
     * 事件已过期
     */
    private void eventEventExpiredExec() {
        GameTypeEnum gameType = getDynamicOperationGame().getGameType();
        log.info("{}#eventEventIngExec run start:{}", getClass().getSimpleName(), gameType.getId());
        long startTime = DateUtil.getCurrentMilli();
        Long lastId = null;
        List<DynamicOperationEventDo> eventDetailDoList = null;

        DynamicOperationQuery query = DynamicOperationQuery.builder()
                .reportStatus(DynamicOperationReportStatusEnum.NONE.getCode())
                .expiredTimeLt(DateUtil.getCurrentMilli())
                .direction(Sort.Direction.ASC)
                .orderByClause("_id")
                .limit(DEFAULT_PAGE_SIZE).build();
        do {
            query.setLtLastId(lastId);
            eventDetailDoList = dynamicOperationEventService.queryList(query, getDynamicOperationGame());
            if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
            }
            Optional.ofNullable(eventDetailDoList)
                    .orElse(Collections.emptyList())
                    .forEach(this::eventEventIngHandle);
        } while (CollectionUtils.isNotEmpty(eventDetailDoList));
        log.info("AbstractDynamicOperationEventScheduler#eventEventIngExec run end:{},time:{}", gameType.getId(), DateUtil.getCurrentMilli() - startTime);
    }

    private void eventEventIngHandle(DynamicOperationEventDo eventDo) {
        if (eventDo.getExpiredTime() < DateUtil.getCurrentMilli()) {
            // 过期上报
            getDynamicOperationManager().asyncUpdateFinishEvent(eventDo.getId(), DynamicOperationEventSubStatusEnum.FINISH_EXPIRED);
        }
    }

}
