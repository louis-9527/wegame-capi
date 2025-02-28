package com.wegame.capi.scheduler.event;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.manager.AbstractDynamicOperationManager;
import com.wegame.capi.biz.manager.MariaMakilingDynamicOperationManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.DynamicOperationEventDetailService;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.DynamicOperationGameEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationDetailQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import com.wegame.capi.scheduler.AbstractScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public abstract class AbstractDynamicOperationEventScheduler extends AbstractScheduler implements Runnable {
    @Autowired
    private DynamicOperationEventService dynamicOperationEventService;
    @Autowired
    private DynamicOperationEventDetailService dynamicOperationEventDetailService;
    @Autowired
    private BetRecordService betRecordService;

    private final long fiveMinutesAgo = System.currentTimeMillis() - 10 * 60 * 1000;


    abstract DynamicOperationGameEnum getDynamicOperationGame();

    abstract AbstractDynamicOperationManager getDynamicOperationManager();


    @Override
    public void run() {
        /**
         * 1.事件明细中未处理成功
         * 2.事件状态已完成,但未上报
         * 3.明细全部处理完成,[事件进行中(实际已完成)、事件已经过期],但未上报
         */
        this.eventDetailIngExec();
        this.eventReportFailExec();
        this.eventEventIngExec();
    }


    /**
     * 处理中的事件明细
     */
    private void eventDetailIngExec() {
        GameTypeEnum gameType = getDynamicOperationGame().getGameType();
        log.info("AbstractDynamicOperationEventScheduler#eventDetailFailExec run start:{}", gameType.getId());
        long startTime = DateUtil.getCurrentMilli();
        Long lastId = null;
        List<DynamicOperationEventDetailDo> eventDetailDoList = null;

        DynamicOperationDetailQuery query = DynamicOperationDetailQuery.builder()
                .betStatus(BetStatusEnum.ING.getCode())
                .createTimeLt(fiveMinutesAgo)
                .direction(Sort.Direction.ASC)
                .orderByClause("_id")
                .limit(DEFAULT_PAGE_SIZE).build();

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
        log.info("AbstractDynamicOperationEventScheduler#eventDetailFailExec run end:{},time:{}", gameType.getId(), DateUtil.getCurrentMilli() - startTime);

    }


    private void eventDetailFailHandle(DynamicOperationEventDetailDo eventDetailDo) {
        GameTypeEnum gameType = getDynamicOperationGame().getGameType();

        Long betRecordId = eventDetailDo.getBetRecordId();
        //查询原始订单的状态
        BetRecordDo recordDo = betRecordService.get(betRecordId, gameType);

        if (Objects.equals(BetStatusEnum.ING.getCode(), recordDo.getStatus())) {
            return;
        }
        BetStatusEnum eventDetailStatus = null;
        DynamicOperationEventStatusEnum newEventStatus = null;
        // 获取事件信息
        DynamicOperationEventDo eventDo = dynamicOperationEventService.getById(betRecordId, getDynamicOperationGame());
        if (Objects.equals(BetStatusEnum.FAIL.getCode(), recordDo.getStatus())) {
            eventDetailStatus = BetStatusEnum.FAIL;
            newEventStatus = getDynamicOperationManager().getNewEventStatus(eventDo);

        } else if (Objects.equals(BetStatusEnum.SUCCESS.getCode(), recordDo.getStatus())) {
            eventDetailStatus = BetStatusEnum.SUCCESS;
            newEventStatus = getDynamicOperationManager().getNewEventStatus(eventDo, eventDetailDo.getEventAmount());
        }
        // 获取事件状态
        Boolean finish = DynamicOperationEventStatusEnum.eventFinish(newEventStatus);
        if (!finish) {
            newEventStatus = DynamicOperationEventStatusEnum.NONE;
        }
        // 更新订单信息
        DynamicOperationEventDetailDo eventDetailUpdate = DynamicOperationEventDetailDo.builder()
                .id(eventDetailDo.getId())
                .betStatus(eventDetailStatus)
                .betId(recordDo.getBetId())
                .build();
        DynamicOperationEventDo eventDoUpdate = DynamicOperationEventDo.builder()
                .id(eventDo.getId())
                .finishBonus(eventDo.getFinishBonus() + eventDetailDo.getEventAmount())
                .eventStatus(newEventStatus).build();
        getDynamicOperationManager().updateEventDetail(eventDetailUpdate, eventDoUpdate);
        if (finish) {
            getDynamicOperationManager().reportEvent(eventDo.getId(), eventDo.getEventId(), newEventStatus);
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
                .eventStatusList(Lists.newArrayList(DynamicOperationEventStatusEnum.FINISH_NORMAL.getCode(), DynamicOperationEventStatusEnum.FINISH_EXPIRED.getCode()))
                .createTimeLt(fiveMinutesAgo)
                .direction(Sort.Direction.ASC)
                .orderByClause("_id")
                .limit(DEFAULT_PAGE_SIZE).build();

        do {
            query.setLastId(lastId);
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

    private void eventReportFailHandle(DynamicOperationEventDo eventDo) {
        // 上报失败(未上报)重新触发
        getDynamicOperationManager().reportEvent(eventDo.getId(), eventDo.getEventId(), eventDo.getEventStatus());
    }

    /**
     * 事件进行中,但未上报
     */
    private void eventEventIngExec() {
        GameTypeEnum gameType = getDynamicOperationGame().getGameType();

        log.info("AbstractDynamicOperationEventScheduler#eventEventIngExec run start:{}", gameType.getId());
        long startTime = DateUtil.getCurrentMilli();
        Long lastId = null;
        List<DynamicOperationEventDo> eventDetailDoList = null;

        DynamicOperationQuery query = DynamicOperationQuery.builder()
                .reportStatus(DynamicOperationReportStatusEnum.NONE.getCode())
                .eventStatus(DynamicOperationEventStatusEnum.NONE.getCode())
                .createTimeLt(fiveMinutesAgo)
                .direction(Sort.Direction.ASC)
                .orderByClause("_id")
                .limit(DEFAULT_PAGE_SIZE).build();

        do {
            query.setLastId(lastId);
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
            getDynamicOperationManager().reportEvent(eventDo.getId(), eventDo.getEventId(), DynamicOperationEventStatusEnum.FINISH_EXPIRED);
        }

    }

}
