package com.wegame.capi.biz.manager;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.DynamicOperationApi;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsBetRecordReq;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsStatusReq;
import com.wegame.capi.api.domain.res.dynamicoperation.UpdateEventsStatusRes;
import com.wegame.capi.biz.builder.req.DynamicOperationStatusReportBuilder;
import com.wegame.capi.biz.domain.biz.operation.*;
import com.wegame.capi.biz.service.DynamicOperationEventConfigService;
import com.wegame.capi.biz.service.DynamicOperationEventDetailService;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.domain.request.query.GetStatusOfEventRequest;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.DynamicOperationGameEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationDetailQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.dynamicEventThreadPoolExecutor;

@Slf4j
@Component
public abstract class AbstractDynamicOperationManager {

    @Autowired
    DynamicOperationEventService dynamicOperationEventService;
    @Autowired
    DynamicOperationEventDetailService dynamicOperationEventDetailService;
    @Autowired
    DynamicOperationEventConfigService dynamicOperationEventConfigService;
    @Autowired
    DynamicOperationApi dynamicOperationApi;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    @Qualifier("dynamicEventReportThreadPoolExecutor")
    public ThreadPoolExecutor dynamicEventReportThreadPoolExecutor;

    /**
     * 获取当前玩家在生效中的干预时间,并按时间取第一条
     *
     * @return
     */
    public DynamicOperationEventDo getEffectedEvent(String playName, BetTypeEnum betType) {
        DynamicOperationGameEnum dynamicOperationGame = this.getDynamicOperationGameEnum();

//        String catch_key = String.format(Constants.DYNAMIC_OPERATION_EVENT_EXIST_KEY, dynamicOperationGame.getGameType().getId(), operationQuery.getPlayerName());
//        // 缓存事件信息
//        // 判断是否存在干预事件
//        Map<String, String> entries = redisTemplate.opsForHash().entries(catch_key);
//        if (entries.isEmpty()) {
//
//            return null;
//        }
        Map<Long, Pair<Long, Long>> entries = dynamicOperationEventService.getEffectList(playName, betType, this.getDynamicOperationGameEnum());

        for (Map.Entry<Long, Pair<Long, Long>> entry : entries.entrySet()) {
            long recordId = entry.getKey();
            long effectedTime = entry.getValue().getLeft();
            long expiredTime = entry.getValue().getRight();
            long currentMilli = DateUtil.getCurrentMilli();
            if (currentMilli < effectedTime) {
                // 未到执行时间
                continue;
            }
            // 判断是否过期
            if (currentMilli > expiredTime) {
                //过期 删除缓存并触发异步上报
                this.delCatchAndReport(playName, recordId, betType, DynamicOperationEventStatusEnum.FINISH_EXPIRED);
                continue;
            }
            //查询事件详情
            DynamicOperationEventDo eventDo = dynamicOperationEventService.getById(recordId, dynamicOperationGame);
            if (eventDo == null) {
                log.warn("dynamicOperationEventService.getById is null:{}", recordId);
                continue;
            }
            if (DynamicOperationEventStatusEnum.eventFinish(eventDo.getEventStatus())) {
                //已完成 删除缓存并触发异步上报
                this.delCatchAndReport(playName, recordId, betType, eventDo.getEventStatus());
                continue;
            }
            if (eventDo.getEventStatus() != DynamicOperationEventStatusEnum.NONE) {
                // 当前事件有未完成的干预事件 不执行
                continue;
            }

            // 判断金额
            if (eventDo.getFinishBonus() >= eventDo.getMinBonus()) {
                // 干预完成,删除缓存并触发异步上报
                this.delCatchAndReport(playName, recordId, betType, DynamicOperationEventStatusEnum.FINISH_NORMAL);
                continue;
            }
            // 判断需要干预金额
            long eventAmount = eventDo.getBonus() - eventDo.getFinishBonus();
            if ((eventDo.getFinishBonus() + eventAmount) > eventDo.getMaxBonus()) {
                // 如果干预后的金额,比最大金额还大,取最小的
                eventAmount = eventDo.getMaxBonus() - eventDo.getFinishBonus();
            }
            eventDo.setInterventionAmount(eventAmount);
            return eventDo;
        }

        return null;
    }

    /**
     * 结果上报
     */
    public void updateEventDetail(DynamicOperationEventDetailDo eventDetailDo, DynamicOperationEventDo eventDo) {
        // 更新 订单状态
        dynamicOperationEventDetailService.update(eventDetailDo, this.getDynamicOperationGameEnum());
        dynamicOperationEventService.update(eventDo, this.getDynamicOperationGameEnum());
    }

//    public void updateEventAndReportExpiredEvent(DynamicOperationEventDo eventDo, GameTypeEnum gameType) {
//        //更新状态
//        DynamicOperationEventDo eventUpdate = DynamicOperationEventDo.builder().id(eventDo.getId())
//                .eventStatus(DynamicOperationEventStatusEnum.FINISH_EXPIRED).build();
//        dynamicOperationEventService.update(eventUpdate, gameType);
//        // 执行上报
//        this.reportEvent(eventDo.getId(), eventDo.getEventId(), DynamicOperationEventStatusEnum.FINISH_EXPIRED, gameType);
//    }


    public Long addEventDetail(DynamicOperationEventDetailDo eventDetailDo) {
        DynamicOperationGameEnum dynamicOperationGame = this.getDynamicOperationGameEnum();
        Long addId = dynamicOperationEventDetailService.add(eventDetailDo, dynamicOperationGame);
        DynamicOperationEventDo eventUpdate = DynamicOperationEventDo.builder().id(eventDetailDo.getEventRecordId())
                .eventStatus(DynamicOperationEventStatusEnum.ING).build();
        dynamicOperationEventService.update(eventUpdate, dynamicOperationGame);
        return addId;
    }

    public List<StatusOfEventsResultDo> getStatusOfEvents(GetStatusOfEventRequest request) {
        // 根据条件查询已完成的事件信息
        DynamicOperationQuery operationQuery = DynamicOperationQuery.builder()
                .eventIdList(request.getEventIds())
                .eventStatusList(Lists.newArrayList(DynamicOperationEventStatusEnum.FINISH_NORMAL.getCode(), DynamicOperationEventStatusEnum.FINISH_EXPIRED.getCode()))
                .effectedStartTime(request.getStartTime().toInstant().toEpochMilli())
                .effectedEndTime(request.getEndTime().toInstant().toEpochMilli())
                .build();
        List<DynamicOperationEventDo> eventDoList = dynamicOperationEventService.queryList(operationQuery, this.getDynamicOperationGameEnum());
        // 获取事件明细
        if (CollectionUtils.isEmpty(eventDoList)) {
            return new ArrayList<>();
        }
        List<StatusOfEventsResultDo> resultList = new ArrayList<>();
        eventDoList.forEach(eventDo -> {
            DynamicOperationDetailQuery detailQuery = DynamicOperationDetailQuery.builder()
                    .recordId(eventDo.getId())
                    .betStatus(BetStatusEnum.SUCCESS.getCode())
                    .build();
            List<DynamicOperationEventDetailDo> eventDetailDos = dynamicOperationEventDetailService.queryList(detailQuery, this.getDynamicOperationGameEnum());

            resultList.add(this.getStatusOfEvents(eventDo, eventDetailDos));
        });
        return resultList;
    }


    private StatusOfEventsResultDo getStatusOfEvents(DynamicOperationEventDo eventDo, List<DynamicOperationEventDetailDo> eventDetailDos) {
        if (CollectionUtils.isEmpty(eventDetailDos)) {
            // 过期从未使用
            return StatusOfEventsResultDo.builder()
                    .betNos(new ArrayList<>())
                    .executeBouns(null)
                    .eventId(eventDo.getEventId())
                    .eventStatus(DynamicOperationEventStatusEnum.convertUpdateEventsStatus(eventDo.getEventStatus()))
                    .build();
        } else {
            long totalAmount = 0L;
            Long executeTime = null;
            List<String> betNos = new ArrayList<>();
            List<StatusOfEventsRecordDo> betRecords = new ArrayList<>();

            for (int i = 0; i < eventDetailDos.size(); i++) {
                DynamicOperationEventDetailDo eventDetail = eventDetailDos.get(i);
                if (i == 0) {
                    executeTime = eventDetail.getCreateTime();
                }
                betNos.add(String.valueOf(eventDetail.getBetRecordId()));
                totalAmount += eventDetail.getPayOutAmount();
                StatusOfEventsRecordDo eventsRecord = StatusOfEventsRecordDo.builder()
                        .betNo(eventDetail.getBetId())
                        .orderTime(eventDetail.getBetTime())
                        .settlementTime(eventDetail.getPayOutTime())
                        .betAmount(eventDetail.getBetAmount())
                        .payoutAmount(eventDetail.getPayOutAmount())
                        .build();
                betRecords.add(eventsRecord);
            }


            return StatusOfEventsResultDo.builder()
                    .betNos(betNos)
                    .executeBouns(totalAmount)
                    .executeTime(executeTime)
                    .eventId(eventDo.getEventId())
                    .betRecords(betRecords)
                    .eventStatus(DynamicOperationEventStatusEnum.convertUpdateEventsStatus(eventDo.getEventStatus()))
                    .build();

        }
    }

    /**
     * 事件上报
     *
     * @param eventStatus
     */

    public void reportEvent(Long recordId, DynamicOperationEventStatusEnum eventStatus) {
        dynamicEventReportThreadPoolExecutor.execute(() -> {
            DynamicOperationEventDo eventDo = dynamicOperationEventService.getById(recordId, this.getDynamicOperationGameEnum());
            AssertUtil.assertNotNull(eventDo, ErrorCode.ERR_INTER);
            if (!DynamicOperationEventStatusEnum.eventFinish(eventDo.getEventStatus())) {
                log.warn("event not finish,report fail:{}", recordId);
                return;
            }
            Integer eventReportStatus = DynamicOperationEventStatusEnum.convertUpdateEventsStatus(eventStatus);
            AssertUtil.assertNotNull(eventReportStatus, ErrorCode.ERR_PARAM, "eventStatus is error");
            // 获取事件下所有已完成订单
            DynamicOperationDetailQuery detailQuery = DynamicOperationDetailQuery.builder()
                    .recordId(recordId)
                    .betStatus(BetStatusEnum.SUCCESS.getCode())
                    .orderByClause("createTime")
                    .direction(Sort.Direction.ASC)
                    .build();

            List<DynamicOperationEventDetailDo> eventDetailDos = dynamicOperationEventDetailService.queryList(detailQuery, this.getDynamicOperationGameEnum());
            List<String> betNos = new ArrayList<>();
            long totalAmount = 0L;
            Long startTime = null;
            List<UpdateEventsBetRecordReq> betRecords = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(eventDetailDos)) {
                for (int i = 0; i < eventDetailDos.size(); i++) {
                    DynamicOperationEventDetailDo eventDetail = eventDetailDos.get(i);
                    if (i == 0) {
                        startTime = eventDetail.getCreateTime();
                    }
                    betNos.add(String.valueOf(eventDetail.getBetRecordId()));
                    totalAmount += eventDetail.getPayOutAmount();
                    betRecords.add(DynamicOperationStatusReportBuilder.updateEventsBetRecordReqBuild(eventDetail));
                }
            }
            UpdateEventsStatusReq updateEventsStatusReq = UpdateEventsStatusReq.builder()
                    .eventId(eventDo.getEventId())
                    .requestTime(DateUtil.toUCTByMilli(new Date().getTime()))
                    .executeBouns(AmountUtil.toBigDecimal(totalAmount))
                    .executeTime(startTime == null ? null : DateUtil.toUCTByMilli(startTime))
                    .betNos(betNos)
                    .betRecords(betRecords)
                    .eventStatus(eventReportStatus)
                    .build();
            // 上报
            UpdateEventsStatusRes updateEventsStatusRes = dynamicOperationApi.updateEventsStatus(updateEventsStatusReq);
            if (updateEventsStatusRes.success()) {
                // 更新事件状态为已上报
                DynamicOperationEventDo eventReportUpdate = DynamicOperationEventDo.builder().id(recordId)
                        .reportStatus(DynamicOperationReportStatusEnum.FINISH).build();
                dynamicOperationEventService.update(eventReportUpdate, this.getDynamicOperationGameEnum());
                return;
            }
            if (StringUtils.isEmpty(updateEventsStatusRes.fail())) {
                // 如果失败(特定情况)
                DynamicOperationEventDo eventReportUpdate = DynamicOperationEventDo.builder().id(recordId)
                        .reportStatus(DynamicOperationReportStatusEnum.FAIL)
                        .reportFailMsg(updateEventsStatusRes.fail())
                        .build();
                dynamicOperationEventService.update(eventReportUpdate, this.getDynamicOperationGameEnum());
                return;
            }
            log.error("dynamicOperationEvent report error:{}", JSON.toJSONString(updateEventsStatusReq));
        });
    }


    public DynamicOperationEventStatusEnum getNewEventStatus(DynamicOperationEventDo eventDo, Long eventAmount) {
        if (DateUtil.getCurrentMilli() > eventDo.getExpiredTime()) {
            // 过期
            return DynamicOperationEventStatusEnum.FINISH_EXPIRED;
        }
        long finishBonus = eventDo.getFinishBonus() + eventAmount;
        if (eventDo.getMinBonus() <= finishBonus) {
            return DynamicOperationEventStatusEnum.FINISH_NORMAL;

        }
        return eventDo.getEventStatus();
    }


    /**
     * 获取事件完成状态
     * 只会返回 FINISH_NORMAL FINISH_NORMAL ,没有完成返回null;
     *
     * @param eventDo
     * @param eventAmount
     * @return
     */
    public DynamicOperationEventStatusEnum getFinishEventStatus(DynamicOperationEventDo eventDo, Long eventAmount) {
        if (DateUtil.getCurrentMilli() > eventDo.getExpiredTime()) {
            // 过期
            return DynamicOperationEventStatusEnum.FINISH_EXPIRED;
        }
        long finishBonus = eventDo.getFinishBonus() + eventAmount;
        if (eventDo.getMinBonus() <= finishBonus) {
            return DynamicOperationEventStatusEnum.FINISH_NORMAL;

        }
        return null;
    }

    public DynamicOperationEventStatusEnum getNewEventStatus(DynamicOperationEventDo eventDo) {
        if (DateUtil.getCurrentMilli() > eventDo.getExpiredTime()) {
            // 过期
            return DynamicOperationEventStatusEnum.FINISH_EXPIRED;
        }

        return eventDo.getEventStatus();

    }

    public abstract DynamicOperationGameEnum getDynamicOperationGameEnum();


    public List<DynamicOperationConfigDo> getConfigDetail(DynamicOperationConfigQuery configQuery) {
        return dynamicOperationEventConfigService.getConfigDetail(configQuery, getDynamicOperationGameEnum());
    }

    public List<Double> getConfigMultipleArray() {
        return dynamicOperationEventConfigService.getConfigMultipleArray(getDynamicOperationGameEnum());
    }


    private void delCatchAndReport(String playName, Long recordId, BetTypeEnum betType, DynamicOperationEventStatusEnum eventStatusEnum) {
        String gameId = this.getDynamicOperationGameEnum().getGameType().getId();
        String catch_key = String.format(Constants.DYNAMIC_OPERATION_EVENT_EXIST_KEY, gameId, playName, betType.getCode());
        redisTemplate.opsForHash().delete(catch_key, String.valueOf(recordId));
        this.reportEvent(recordId, eventStatusEnum);
    }

}
