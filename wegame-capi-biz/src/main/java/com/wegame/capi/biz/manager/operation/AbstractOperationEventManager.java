package com.wegame.capi.biz.manager.operation;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.lock.DistributeLock;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.operation.UpdateEventsBetRecordReq;
import com.wegame.capi.api.domain.req.operation.UpdateEventsStatusReq;
import com.wegame.capi.api.domain.res.operation.UpdateEventsStatusRes;
import com.wegame.capi.api.operation.OperationEventApi;
import com.wegame.capi.biz.builder.req.UpdateEventsBetRecordReqBuilder;
import com.wegame.capi.biz.domain.biz.game.GameOperationEventDo;
import com.wegame.capi.biz.domain.biz.operation.*;
import com.wegame.capi.biz.service.operation.OperationEventConfigService;
import com.wegame.capi.biz.service.operation.OperationEventDetailService;
import com.wegame.capi.biz.service.operation.OperationEventService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.common.Constants;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.OperationEventReportStatusEnum;
import com.wegame.capi.common.enums.OperationEventStatusEnum;
import com.wegame.capi.common.enums.OperationEventSubStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.common.utils.MultipleUtil;
import com.wegame.capi.dao.repository.domain.operation.OperationEventConfigQuery;
import com.wegame.capi.dao.repository.domain.operation.OperationEventDetailQuery;
import com.wegame.capi.dao.repository.domain.operation.OperationEventQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 抽象干预事件Manager
 */
@Slf4j
@Component
public abstract class AbstractOperationEventManager {

    @Autowired
    OperationEventService operationEventService;
    @Autowired
    OperationEventDetailService operationEventDetailService;
    @Autowired
    OperationEventConfigService operationEventConfigService;
    @Autowired
    OperationEventApi operationEventApi;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    public ThreadPoolExecutor operationEventReportThreadPoolExecutor;
    @Autowired
    public ThreadPoolExecutor operationEventThreadPoolExecutor;
    @Autowired
    private DistributeLock distributeLock;

    /**
     * 获取干预事件类型
     *
     * @return 干预事件类型
     */
    public abstract OperationEventTypeEnum operationEventType();

    /**
     * 获取当前玩家在生效中的干预时间,并按时间取第一条
     */
    public OperationEventDo getEffectedEvent(String playName) {
        OperationEventTypeEnum dynamicOperationGame = operationEventType();
        List<DynamicOperationEffectEventCatchDo> entries = operationEventService.getEffectList(playName, operationEventType());
        for (DynamicOperationEffectEventCatchDo entry : entries) {
            long recordId = entry.getId();
            long effectedTime = entry.getEffectedTime();
            long expiredTime = entry.getExpiredTime();
            long currentMilli = DateUtil.getCurrentMilli();
            // 为啥加1000?是为了防止时间误差,dos系统干预事件立即上报会触发event not find
            if (currentMilli < effectedTime + 1000) {
                // 未到执行时间
                continue;
            }
            // 判断是否过期
            if (currentMilli > expiredTime) {
                // 过期 删除缓存并触发异步上报
                operationEventService.cleanOperationByCatch(recordId, playName, operationEventType());
                asyncUpdateFinishEvent(recordId, playName, OperationEventSubStatusEnum.FINISH_EXPIRED);
                continue;
            }
            //判断是否有未完成的干预事件
            OperationEventStatusEnum operationEventStatusEnum = operationEventService.checkOperationUseStatus(recordId, dynamicOperationGame);
            if (operationEventStatusEnum != null) {
                if (OperationEventStatusEnum.ING == operationEventStatusEnum) {
                    continue;
                }
            }
            //查询事件详情
            OperationEventDo eventDo = operationEventService.getById(recordId, dynamicOperationGame);
            if (eventDo == null) {
                log.warn("dynamicOperationEventService.getById is null:{}", recordId);
                operationEventService.cleanOperationByCatch(recordId, playName, dynamicOperationGame);
                continue;
            }
            if (OperationEventStatusEnum.ING == eventDo.getEventStatus()) {
                continue;
            }
            if (OperationEventStatusEnum.FINISH == eventDo.getEventStatus()) {
                //已完成 删除缓存并触发异步上报
                operationEventService.cleanOperationByCatch(recordId, playName, operationEventType());
                asyncReportEventById(recordId);
                continue;
            }
            if (OperationEventReportStatusEnum.reportSuccess(eventDo.getReportStatus())) {
                //已完成 删除缓存并触发异步上报
                operationEventService.cleanOperationByCatch(recordId, playName, operationEventType());
                continue;
            }
            // 判断金额
            if (eventDo.getFinishBonus() != null && (eventDo.getFinishBonus() >= eventDo.getMinBonus())) {
                // 干预完成,删除缓存并触发异步上报
                operationEventService.cleanOperationByCatch(recordId, playName, operationEventType());
                asyncUpdateFinishEvent(recordId, playName, OperationEventSubStatusEnum.FINISH_NORMAL);
                continue;
            }

            return eventDo;
        }

        return null;
    }

    public void updateEventDetail(OperationEventDetailDo eventDetailDo, OperationEventDo eventDo) {
        if (eventDetailDo != null) {
            operationEventDetailService.update(eventDetailDo, operationEventType());
        }
        if (eventDo != null) {
            operationEventService.update(eventDo, operationEventType());
        }
    }

    public Long addEventDetail(OperationEventDetailDo eventDetailDo) {
        OperationEventTypeEnum typeEnum = operationEventType();
        Long detailId = operationEventDetailService.add(eventDetailDo, typeEnum);
        OperationEventDo eventDo = OperationEventDo.builder()
                .id(eventDetailDo.getEventRecordId())
                .eventStatus(OperationEventStatusEnum.ING).build();
        operationEventService.update(eventDo, typeEnum);
        return detailId;
    }

    public void updateOperationUseStatus(Long eventRecordId, OperationEventStatusEnum eventStatusEnum) {
        operationEventService.updateOperationUseStatus(eventRecordId, eventStatusEnum, this.operationEventType());
    }

    public List<StatusOfEventsResultDo> getStatusOfEvents(OperationEventQuery operationEventQuery) {
        // 根据条件查询已完成的事件信息
        List<OperationEventDo> eventDoList = operationEventService.queryList(operationEventQuery, operationEventType());
        // 获取事件明细
        if (CollectionUtils.isEmpty(eventDoList)) {
            return new ArrayList<>();
        }
        List<StatusOfEventsResultDo> resultList = new ArrayList<>();
        eventDoList.forEach(eventDo -> {
            List<OperationEventDetailDo> eventDetailDos = operationEventDetailService.list(OperationEventDetailQuery.builder()
                    .recordId(eventDo.getId())
                    .betStatus(BetStatusEnum.SUCCESS.getCode())
                    .build(), operationEventType());
            resultList.add(getStatusOfEvents(eventDo, eventDetailDos));
        });
        return resultList;
    }


    private StatusOfEventsResultDo getStatusOfEvents(OperationEventDo eventDo, List<OperationEventDetailDo> eventDetailDos) {
        if (CollectionUtils.isEmpty(eventDetailDos)) {
            // 过期从未使用
            return StatusOfEventsResultDo.builder()
                    .betNos(new ArrayList<>())
                    .executeBouns(null)
                    .eventId(eventDo.getEventId())
                    .eventStatus(eventDo.getEventSubStatus().getCode())
                    .build();
        } else {
            long totalAmount = 0L;
            Long executeTime = null;
            List<String> betNos = new ArrayList<>();
            List<StatusOfEventsRecordDo> betRecords = new ArrayList<>();

            for (int i = 0; i < eventDetailDos.size(); i++) {
                OperationEventDetailDo eventDetail = eventDetailDos.get(i);
                if (i == 0) {
                    executeTime = eventDetail.getCreateTime();
                }
                betNos.add(eventDetail.getBetId());
                totalAmount += eventDetail.getEventAmount();
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
                    .eventStatus(eventDo.getEventSubStatus().getCode())
                    .build();

        }
    }


    /**
     * 获取干预事件结果
     */
    public GameOperationEventDo getEventBetResult(Long payOutAmount) {
        UserInfo userInfo = UserInfoContext.getUserInfo();
        // 判断是否存在干预事件
        OperationEventDo eventDo = getEffectedEvent(userInfo.getPlayerName());
        if (eventDo == null) {
            return GameOperationEventDo.builder().eventResult(false).build();
        }
        // 获取需要干预的金额
        Long interventionAmount = getInterventionAmount(eventDo, userInfo.getPlayerName(), payOutAmount);
        if (interventionAmount == null) {
            return GameOperationEventDo.builder().eventResult(false).build();
        }
        // 构建并获取Wheel
        Long wheelId = getWheelId(interventionAmount, payOutAmount);
        if (Objects.isNull(wheelId)) {
            return GameOperationEventDo.builder().eventResult(false).build();
        }
        GameOperationEventDo gameEventDo = new GameOperationEventDo();
        gameEventDo.setEventResult(true);
        gameEventDo.setConfigId(wheelId);
        gameEventDo.setOperationEvent(eventDo);
        return gameEventDo;
    }

    /**
     * 获取需要干预的金额
     */
    private Long getInterventionAmount(OperationEventDo eventDo, String playName, Long betAmount) {
        // 判断需要干预金额
        long finishBonus = eventDo.getFinishBonus() == null ? 0L : eventDo.getFinishBonus();
        long eventAmount = eventDo.getBonus() - finishBonus;

        if ((finishBonus + eventAmount) > eventDo.getMaxBonus()) {
            // 如果干预后的金额,比最大金额还大,取最小的
            eventAmount = eventDo.getMaxBonus() - finishBonus;
        }

        // 如果投注金额小于系统最小的投注金额,需要判断是否可以赠送出去
        if (betAmount <= operationEventType().getGameType().getMinBetAmount()) {
            // 如果投注金额等于最小的投注金额,就需要判断是否能送出去
            if (eventAmount < Constants.MIN_BET_AMOUNT_M * betAmount) {
                // 上报
                log.info("event getInterventionAmount finish amount error eventId:{}", eventDo.getEventId());
                if (finishBonus > 0L) {
                    asyncUpdateFinishEvent(eventDo.getId(), playName, OperationEventSubStatusEnum.FINISH_AMOUNT_ERROR_FIND_DETAIL);
                } else {
                    asyncUpdateFinishEvent(eventDo.getId(), playName, OperationEventSubStatusEnum.FINISH_AMOUNT_ERROR_NOT_DETAIL);
                }
                return null;
            }
        }
        return eventAmount;
    }

    public Long getWheelId(Long interventionAmount, Long betPayAmount) {
        // 计算干预金额是投注金额的多少倍,派奖金额应该是betAmount+干预金额,
        long interveneBalance = interventionAmount + betPayAmount;
        //计算最大倍数
        double multiple = ((double) interveneBalance / betPayAmount);
        // 根据倍数找最接近的
        Double closestMultiple = MultipleUtil.findClosestMultiple(multiple, getConfigMultipleArray());
        if (closestMultiple == null) {
            return null;
        }
        return getPayMultipleConfigId(closestMultiple);
    }

    public void asyncUpdateEventDetail(OperationEventDo eventDo, String playerName, BetStatusEnum betStatus, Long detailId, String betId, Long eventAmount) {
        operationEventThreadPoolExecutor.execute(() -> {
            OperationEventSubStatusEnum eventSubStatusEnum = OperationEventSubStatusEnum.NONE;
            OperationEventStatusEnum eventStatusEnum = OperationEventStatusEnum.NONE;

            Long finishBonus = eventDo.getFinishBonus() == null ? 0 : eventDo.getFinishBonus() + eventAmount;
            if (eventDo.getMinBonus() <= finishBonus) {
                eventSubStatusEnum = OperationEventSubStatusEnum.FINISH_NORMAL;
                eventStatusEnum = OperationEventStatusEnum.FINISH;
            }
            OperationEventDetailDo eventDetailUpdate = OperationEventDetailDo.builder()
                    .id(detailId)
                    .betStatus(betStatus)
                    .betId(betId)
                    .build();
            OperationEventDo eventDoUpdate = OperationEventDo.builder()
                    .id(eventDo.getId())
                    .finishBonus(BetStatusEnum.SUCCESS == betStatus ? finishBonus : null)
                    .eventStatus(eventStatusEnum)
                    .eventSubStatus(eventSubStatusEnum)
                    .build();


            updateEventDetail(eventDetailUpdate, eventDoUpdate);
            operationEventService.updateOperationUseStatus(eventDo.getId(), OperationEventStatusEnum.NONE, operationEventType());
            // 执行上报
            if (OperationEventSubStatusEnum.isFinish(eventSubStatusEnum)) {
                // 如果完成执行上报
                asyncReportEventById(eventDo.getId());
            }
        });
    }


    public void asyncUpdateFinishEvent(Long recordId, String playName, OperationEventSubStatusEnum eventSubStatusEnum) {
        operationEventThreadPoolExecutor.execute(() -> {
            OperationEventDo build = OperationEventDo.builder()
                    .id(recordId)
                    .eventStatus(OperationEventStatusEnum.FINISH)
                    .eventSubStatus(eventSubStatusEnum)
                    .build();
            operationEventService.update(build, operationEventType());
            // 上报
            reportEventById(recordId);
        });
    }


    public void updateFinishEvent(Long recordId, Boolean scheduler, OperationEventSubStatusEnum eventSubStatusEnum) {
        OperationEventDo build = OperationEventDo.builder()
                .id(recordId)
                .eventStatus(OperationEventStatusEnum.FINISH)
                .eventSubStatus(eventSubStatusEnum)
                .build();
        operationEventService.update(build, operationEventType());
        // 上报
        reportEventById(recordId);
    }


    /**
     * 异步上报,
     *
     * @param recordId 事件ID
     */
    public void asyncReportEventById(Long recordId) {
        operationEventReportThreadPoolExecutor.execute(() -> {
            OperationEventDo eventDo = operationEventService.getById(recordId, operationEventType());
            this.reportEvent(eventDo);
        });

    }

    /**
     * 同步上报,
     *
     * @param recordId 事件ID
     */
    public void reportEventById(Long recordId) {
        OperationEventDo eventDo = operationEventService.getById(recordId, operationEventType());
        this.reportEvent(eventDo, false);
    }

    /**
     * 同步上报,
     *
     * @param recordId 事件ID
     */
    public void reportEventById(Long recordId, boolean scheduler) {
        OperationEventDo eventDo = operationEventService.getById(recordId, operationEventType());
        this.reportEvent(eventDo, scheduler);
    }

    public void reportEvent(OperationEventDo eventDo) {
        distributeLock.lockThenRun(LockNameUtil.getOperationEventReportLockName(this.operationEventType().getGameType(), eventDo.getId()),
                () -> this.reportEventLock(eventDo, false),
                "OperationEvent report distributeLock");
    }

    public void reportEvent(OperationEventDo eventDo, boolean scheduler) {
        distributeLock.lockThenRun(LockNameUtil.getOperationEventReportLockName(this.operationEventType().getGameType(), eventDo.getId()),
                () -> this.reportEventLock(eventDo, scheduler),
                "OperationEvent report distributeLock");
    }


    private void reportEventLock(OperationEventDo eventDo, boolean scheduler) {
        log.info("report start eventId:{}", eventDo.getId());
        Long recordId = eventDo.getId();
        AssertUtil.assertNotNull(eventDo, ErrorCode.ERR_INTER);
        if (OperationEventStatusEnum.FINISH != eventDo.getEventStatus()) {
            log.warn("event not finish,report fail:{}", recordId);
            return;
        }
        if (!(Objects.equals(OperationEventReportStatusEnum.NONE.getCode(), eventDo.getReportStatus().getCode()))) {
            log.warn("event report is finish,report fail:{}", recordId);
            return;
        }
        // 获取事件下所有已完成订单
        OperationEventDetailQuery detailQuery = OperationEventDetailQuery.builder()
                .recordId(recordId)
                .betStatus(BetStatusEnum.SUCCESS.getCode())
                .orderByClause("createTime")
                .direction(Sort.Direction.ASC)
                .build();

        List<OperationEventDetailDo> eventDetailDos = operationEventDetailService.list(detailQuery, operationEventType());
        List<String> betNos = new ArrayList<>();
        long totalAmount = 0L;
        Long startTime = null;
        List<UpdateEventsBetRecordReq> betRecords = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(eventDetailDos)) {
            for (int i = 0; i < eventDetailDos.size(); i++) {
                OperationEventDetailDo eventDetail = eventDetailDos.get(i);
                if (i == 0) {
                    startTime = eventDetail.getCreateTime();
                }
                betNos.add(eventDetail.getBetId());
                totalAmount += eventDetail.getEventAmount();
                betRecords.add(UpdateEventsBetRecordReqBuilder.buildReq(eventDetail));
            }
        }
        UpdateEventsStatusReq updateEventsStatusReq = UpdateEventsStatusReq.builder()
                .eventId(eventDo.getEventId())
                .requestTime(DateUtil.toUTC(new Date()))
                .executeBonus(AmountUtil.toBigDecimal(totalAmount))
                .executeTime(startTime == null ? DateUtil.toUTC(DateUtil.getCurrentMilli()) : DateUtil.toUTC(startTime))
                .betNos(betNos)
                .betRecords(betRecords)
                .eventStatus(eventDo.getEventSubStatus().getCode())
                .build();
        // 上报
        UpdateEventsStatusRes updateEventsStatusRes = operationEventApi.updateEventsStatus(updateEventsStatusReq);
        if (updateEventsStatusRes.isSuccess()) {
            // 更新事件状态为已上报
            OperationEventDo eventReportUpdate = OperationEventDo.builder().id(recordId)
                    .reportStatus(OperationEventReportStatusEnum.FINISH)
                    .reportTime(DateUtil.getCurrentMilli())
                    .build();
            operationEventService.update(eventReportUpdate, operationEventType());
            return;
        }
        if (updateEventsStatusRes.isFail()) {
            log.warn("dynamicOperationEvent report error,eventId:{}", eventDo.getEventId());
            if (!scheduler) {
                log.warn("dynamicOperationEvent report error,no scheduler return,,eventId:{}", eventDo.getEventId());
                return;
            }
            if ((DateUtil.getCurrentMilli() - eventDo.getUpdateTime()) >= Constants.TWO_HOURS_AGO) {
                log.error("dynamicOperationEvent report error,scheduler eventId:{}", eventDo.getEventId());
                // 如果失败(特定情况)
                OperationEventDo eventReportUpdate = OperationEventDo.builder().id(recordId)
                        .reportStatus(OperationEventReportStatusEnum.FAIL)
                        .reportTime(DateUtil.getCurrentMilli())
                        .reportFailMsg(updateEventsStatusRes.getMessage())
                        .build();
                operationEventService.update(eventReportUpdate, operationEventType());
                return;
            }
        }
        log.error("dynamicOperationEvent report error:{}", JSON.toJSONString(updateEventsStatusReq));
    }


    private Long getPayMultipleConfigId(double multiple) {
        OperationEventConfigQuery configQuery = OperationEventConfigQuery.builder()
                .gameId(operationEventType().getGameType().getId())
                .multiple(multiple).build();
        List<List<Long>> configDetailGroup = getConfigIds(configQuery);
        if (CollectionUtils.isEmpty(configDetailGroup)) {
            log.error("normalPayMultipleConfig result null,,multiple:{}", multiple);
            return null;
        }
        // 获取分组下config配置,避免玩家相同倍数连续命中同样的滚轮
        String incrementKey = String.format(Constants.OPERATION_EVENT_CONFIG_INCREMENT_CACHE_KEY,
                operationEventType().getGameType().getId(),
                UserInfoContext.getUserInfo().getId(), multiple);
        Long increment = redisTemplate.opsForValue().increment(incrementKey);
        if (increment == null || increment >= configDetailGroup.size()) {
            increment = 1L;
            redisTemplate.opsForValue().set(incrementKey, increment);
        }
        List<Long> configDetail = configDetailGroup.get((int) (increment - 1));
        int randomIndex = ThreadLocalRandom.current().nextInt(configDetail.size());
        return configDetail.get(randomIndex);
    }


    public List<List<Long>> getConfigIds(OperationEventConfigQuery configQuery) {
        return operationEventConfigService.getConfigIds(configQuery, operationEventType());
    }

    public List<Double> getConfigMultipleArray() {
        return operationEventConfigService.listMultiple(operationEventType());
    }
}
