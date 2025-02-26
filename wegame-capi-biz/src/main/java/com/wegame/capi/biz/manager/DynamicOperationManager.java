package com.wegame.capi.biz.manager;

import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.DynamicOperationApi;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsBetRecordReq;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsStatusReq;
import com.wegame.capi.biz.builder.req.DynamicOperationStatusReportBuilder;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationStatusOfEventsDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationStatusOfEventsRecordDO;
import com.wegame.capi.biz.service.DynamicOperationEventDetailService;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.domain.request.DynamicOperationStatusOfEventRequest;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationDetailQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.dynamicOperationThreadPoolExecutor;

@Service
public class DynamicOperationManager {
    @Autowired
    private DynamicOperationEventService dynamicOperationEventService;
    @Autowired
    private DynamicOperationEventDetailService dynamicOperationEventDetailService;
    @Autowired
    private DynamicOperationApi dynamicOperationApi;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 获取当前玩家在生效中的干预时间,并按时间取第一条
     *
     * @return
     */
    public DynamicOperationEventDo getBuyUserNameAndTime(DynamicOperationQuery operationQuery, GameTypeEnum gameType) {
        String key = String.format(Constants.DYNAMIC_OPERATION_EVENT_EXIST_KEY, gameType.getId(), operationQuery.getPlayerName());
        // 缓存事件信息
        // 判断是否存在干预事件
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (CollectionUtils.isEmpty(members)) {
            return null;
        }
        for (Object recordIdObj : members) {
            Long recordId = (Long) recordIdObj;
            DynamicOperationEventDo eventDo = dynamicOperationEventService.getById(recordId, gameType);
            // 判断是否过期
            if (DateUtil.getCurrentMilli() > eventDo.getExpiredTime()) {
                redisTemplate.opsForSet().remove(key, recordId);
                continue;
            }
            Long payOutAmount = dynamicOperationEventDetailService.sumPayoutAmount(recordId, gameType);
            // 金额在10%左右 可视为完成
            DynamicOperationEventStatusEnum eventStatus = dynamicOperationEventService.getEventStatus(eventDo.getBonus(), payOutAmount);
            if (DynamicOperationEventStatusEnum.NONE != eventStatus) {
                redisTemplate.opsForSet().remove(key, recordId);
                continue;
            }
            eventDo.setInterventionAmount(eventDo.getBonus() - payOutAmount);
            return eventDo;
        }
        return null;
    }

    /**
     * 结果上报
     */
    @Async(dynamicOperationThreadPoolExecutor)
    public void asyncEventReport(DynamicOperationEventDo eventDo, Long detailId, String betId, GameTypeEnum gameType) {
        // 更新 订单状态
        DynamicOperationEventDetailDo updateReq = DynamicOperationEventDetailDo.builder().id(detailId)
                .betStatus(BetStatusEnum.SUCCESS)
                .betId(betId)
                .build();
        dynamicOperationEventDetailService.update(updateReq, gameType);
        // 检查干预事件是否可上报,如果已完成则上报
        this.tryUpdateEventAndReportEvent(eventDo, gameType);
    }

    public void updateEventAndReportExpiredEvent(DynamicOperationEventDo eventDo, GameTypeEnum gameType) {
        //更新状态
        DynamicOperationEventDo eventUpdate = DynamicOperationEventDo.builder().id(eventDo.getId())
                .eventStatus(DynamicOperationEventStatusEnum.FINISH_EXPIRED).build();
        dynamicOperationEventService.update(eventUpdate, gameType);
        // 执行上报
        this.reportEvent(eventDo.getId(), eventDo.getEventId(), DynamicOperationEventStatusEnum.FINISH_EXPIRED, gameType);
    }

    public void tryUpdateEventAndReportEvent(DynamicOperationEventDo eventDo, GameTypeEnum gameType) {
        Long payOutAmount = dynamicOperationEventDetailService.sumPayoutAmount(eventDo.getId(), gameType);
        DynamicOperationEventStatusEnum eventStatus = dynamicOperationEventService.getEventStatus(eventDo.getBonus(), payOutAmount);
        if (DynamicOperationEventStatusEnum.FINISH_NORMAL == eventStatus) {
            //更新状态
            DynamicOperationEventDo eventUpdate = DynamicOperationEventDo.builder().id(eventDo.getId())
                    .eventStatus(eventStatus).build();
            dynamicOperationEventService.update(eventUpdate, gameType);
            // 执行上报
            this.reportEvent(eventDo.getId(), eventDo.getEventId(), DynamicOperationEventStatusEnum.FINISH_NORMAL, gameType);
        }

    }


    public List<DynamicOperationStatusOfEventsDo> queryStatusOfEvents(DynamicOperationStatusOfEventRequest request) {
        GameTypeEnum gameTypeEnum = GameTypeEnum.valueOfId(request.getGameId());
        // 根据条件查询已完成的事件信息
        DynamicOperationQuery operationQuery = DynamicOperationQuery.builder()
                .eventIdList(request.getEventIds())
                .eventStatusList(Lists.newArrayList(DynamicOperationEventStatusEnum.FINISH_NORMAL.getCode(), DynamicOperationEventStatusEnum.FINISH_EXPIRED.getCode()))
                .effectedStartTime(request.getStartTime().toInstant().toEpochMilli())
                .effectedEndTime(request.getEndTime().toInstant().toEpochMilli())
                .build();
        List<DynamicOperationEventDo> eventDoList = dynamicOperationEventService.queryList(operationQuery, gameTypeEnum);
        // 获取事件明细
        if (CollectionUtils.isEmpty(eventDoList)) {
            return new ArrayList<>();
        }
        List<DynamicOperationStatusOfEventsDo> resultList = new ArrayList<>();
        eventDoList.forEach(eventDo -> {
            DynamicOperationDetailQuery detailQuery = DynamicOperationDetailQuery.builder()
                    .operationId(eventDo.getId())
                    .betStatus(BetStatusEnum.SUCCESS.getCode())
                    .build();
            List<DynamicOperationEventDetailDo> eventDetailDos = dynamicOperationEventDetailService.queryList(detailQuery, gameTypeEnum);

            resultList.add(this.getStatusOfEvents(eventDo, eventDetailDos));
        });
        return resultList;
    }


    private DynamicOperationStatusOfEventsDo getStatusOfEvents(DynamicOperationEventDo eventDo, List<DynamicOperationEventDetailDo> eventDetailDos) {
        if (CollectionUtils.isEmpty(eventDetailDos)) {
            // 过期从未使用
            return DynamicOperationStatusOfEventsDo.builder()
                    .betNos(new ArrayList<>())
                    .executeBouns(null)
                    .eventId(eventDo.getEventId())
                    .eventStatus(DynamicOperationEventStatusEnum.convertUpdateEventsStatus(eventDo.getEventStatus()))
                    .build();
        } else {
            long totalAmount = 0L;
            Long executeTime = null;
            List<String> betNos = new ArrayList<>();
            List<DynamicOperationStatusOfEventsRecordDO> betRecords = new ArrayList<>();

            for (int i = 0; i <= eventDetailDos.size(); i++) {
                DynamicOperationEventDetailDo eventDetail = eventDetailDos.get(i);
                if (i == 0) {
                    executeTime = eventDetail.getCreateTime();
                }
                betNos.add(String.valueOf(eventDetail.getBetRecordId()));
                totalAmount += eventDetail.getPayOutAmount();
                DynamicOperationStatusOfEventsRecordDO eventsRecord = DynamicOperationStatusOfEventsRecordDO.builder()
                        .betNo(eventDetail.getBetId())
                        .orderTime(eventDetail.getBetTime())
                        .settlementTime(eventDetail.getPayOutTime())
                        .betAmount(eventDetail.getBetAmount())
                        .payoutAmount(eventDetail.getPayOutAmount())
                        .build();
                betRecords.add(eventsRecord);
            }


            return DynamicOperationStatusOfEventsDo.builder()
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
     * @param id
     * @param eventId
     * @param eventStatus
     * @param gameType
     */
    public void reportEvent(Long id, String eventId, DynamicOperationEventStatusEnum eventStatus, GameTypeEnum gameType) {
        Integer eventReportStatus = DynamicOperationEventStatusEnum.convertUpdateEventsStatus(eventStatus);

        AssertUtil.assertNotNull(eventReportStatus, ErrorCode.ERR_PARAM, "eventStatus is error");
        // 获取事件下所有已完成订单
        DynamicOperationDetailQuery detailQuery = DynamicOperationDetailQuery.builder()
                .operationId(id)
                .betStatus(BetStatusEnum.SUCCESS.getCode())
                .orderByClause("createTime")
                .direction(Sort.Direction.ASC)
                .build();

        List<DynamicOperationEventDetailDo> eventDetailDos = dynamicOperationEventDetailService.queryList(detailQuery, gameType);

        List<String> betNos = new ArrayList<>();
        long totalAmount = 0L;
        Long startTime = null;
        List<UpdateEventsBetRecordReq> betRecords = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(eventDetailDos)) {
            for (int i = 0; i <= eventDetailDos.size(); i++) {
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
                .eventId(eventId)
                .requestTime(DateUtil.toUCTByMilli(new Date().getTime()))
                .executeBouns(AmountUtil.toBigDecimal(totalAmount))
                .executeTime(startTime == null ? null : DateUtil.toUCTByMilli(startTime))
                .betNos(betNos)
                .betRecords(betRecords)
                .eventStatus(eventReportStatus)
                .build();
        // 上报
        dynamicOperationApi.updateEventsStatus(updateEventsStatusReq);
        // 更新事件状态为已上报
        DynamicOperationEventDo eventReportUpdate = DynamicOperationEventDo.builder().id(id)
                .reportStatus(DynamicOperationReportStatusEnum.FINISH).build();
        dynamicOperationEventService.update(eventReportUpdate, gameType);
    }

}
