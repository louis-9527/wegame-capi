package com.wegame.capi.scheduler.event;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.manager.DynamicOperationManager;
import com.wegame.capi.biz.service.BetRecordService;
import com.wegame.capi.biz.service.DynamicOperationEventDetailService;
import com.wegame.capi.biz.service.DynamicOperationEventService;
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
    private DynamicOperationManager dynamicOperationManager;
    @Autowired
    private BetRecordService betRecordService;

    private final long fiveMinutesAgo = System.currentTimeMillis() - 10 * 60 * 1000;


    abstract GameTypeEnum getGameType();


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
        log.info("AbstractDynamicOperationEventScheduler#eventDetailFailExec run start:{}", getGameType().getId());
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
            eventDetailDoList = dynamicOperationEventDetailService.queryPage(query, getGameType());
            if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
            }
            Optional.ofNullable(eventDetailDoList)
                    .orElse(Collections.emptyList())
                    .forEach(this::eventDetailFailHandle);
        } while (CollectionUtils.isNotEmpty(eventDetailDoList));
        log.info("AbstractDynamicOperationEventScheduler#eventDetailFailExec run end:{},time:{}", getGameType().getId(), DateUtil.getCurrentMilli() - startTime);

    }


    private void eventDetailFailHandle(DynamicOperationEventDetailDo eventDetailDo) {
        Long betRecordId = eventDetailDo.getBetRecordId();
        //查询原始订单的状态
        BetRecordDo recordDo = betRecordService.get(betRecordId, getGameType());
        if (recordDo == null || Objects.equals(BetStatusEnum.FAIL.getCode(), recordDo.getStatus())) {
            // 失败处理
            DynamicOperationEventDetailDo detailUpdate = DynamicOperationEventDetailDo.builder()
                    .id(eventDetailDo.getId())
                    .betStatus(BetStatusEnum.FAIL)
                    .build();
            dynamicOperationEventDetailService.update(detailUpdate, getGameType());
        } else if (Objects.equals(BetStatusEnum.SUCCESS.getCode(), recordDo.getStatus())) {
            // 成功处理
            DynamicOperationEventDetailDo detailUpdate = DynamicOperationEventDetailDo.builder()
                    .id(eventDetailDo.getId())
                    .betStatus(BetStatusEnum.SUCCESS)
                    .build();
            if (StringUtils.isEmpty(eventDetailDo.getBetId())) {
                detailUpdate.setBetId(recordDo.getBetId());
            }
            // 更新订单状态
            dynamicOperationEventDetailService.update(detailUpdate, getGameType());
            // 获取干预金额
            DynamicOperationEventDo operationEventDo = dynamicOperationEventService.getById(eventDetailDo.getOperatorRecordId(), getGameType());
            // 重新触发上报
            dynamicOperationManager.tryUpdateEventAndReportEvent(operationEventDo, getGameType());
        }
        // 进行中的订单不处理
    }

    /**
     * 事件已经完成,但未上报
     */
    private void eventReportFailExec() {
        log.info("AbstractDynamicOperationEventScheduler#eventReportFailExec run start:{}", getGameType().getId());
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
            eventDetailDoList = dynamicOperationEventService.queryPage(query, getGameType());
            if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
            }
            Optional.ofNullable(eventDetailDoList)
                    .orElse(Collections.emptyList())
                    .forEach(this::eventReportFailHandle);
        } while (CollectionUtils.isNotEmpty(eventDetailDoList));
        log.info("AbstractDynamicOperationEventScheduler#eventReportFailExec run end:{},time:{}", getGameType().getId(), DateUtil.getCurrentMilli() - startTime);
    }

    private void eventReportFailHandle(DynamicOperationEventDo eventDo) {
        // 上报失败(未上报)重新触发
        dynamicOperationManager.reportEvent(eventDo.getId(), eventDo.getEventId(), eventDo.getEventStatus(), getGameType());
    }

    /**
     * 事件已经完成,但未上报
     */
    private void eventEventIngExec() {
        log.info("AbstractDynamicOperationEventScheduler#eventEventIngExec run start:{}", getGameType().getId());
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
            eventDetailDoList = dynamicOperationEventService.queryPage(query, getGameType());
            if (CollectionUtil.isNotEmpty(eventDetailDoList)) {
                lastId = eventDetailDoList.get(eventDetailDoList.size() - 1).getId();
            }
            Optional.ofNullable(eventDetailDoList)
                    .orElse(Collections.emptyList())
                    .forEach(this::eventEventIngHandle);
        } while (CollectionUtils.isNotEmpty(eventDetailDoList));
        log.info("AbstractDynamicOperationEventScheduler#eventEventIngExec run end:{},time:{}", getGameType().getId(), DateUtil.getCurrentMilli() - startTime);
    }

    private void eventEventIngHandle(DynamicOperationEventDo eventDo) {
        if (eventDo.getExpiredTime() > DateUtil.getCurrentMilli()) {
            // 过期上报
            dynamicOperationManager.updateEventAndReportExpiredEvent(eventDo, getGameType());
            return;
        }
        // 未过期 检查上报
        dynamicOperationManager.tryUpdateEventAndReportEvent(eventDo, getGameType());

    }

}
