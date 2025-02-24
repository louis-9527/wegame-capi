package com.wegame.capi.biz.manager;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.DynamicOperationApi;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsBetRecordReq;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsStatusReq;
import com.wegame.capi.biz.builder.req.DynamicOperationStatusReportBuilder;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.service.DynamicOperationEventDetailService;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.common.utils.AmountUtil;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationDetailQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.dynamicOperationThreadPoolExecutor;

@Service
public class DynamicOperationManager {
    @Autowired
    private DynamicOperationEventService dynamicOperationEventService;
    @Autowired
    private DynamicOperationEventDetailService dynamicOperationEventDetailService;
    @Autowired
    private DynamicOperationApi dynamicOperationApi;


    /**
     * 获取当前玩家在生效中的干预时间,并按时间取第一条
     *
     * @return
     */
    public DynamicOperationEventDo getBuyUserNameAndTime(DynamicOperationQuery operationQuery, GameTypeEnum gameType) {
        List<DynamicOperationEventDo> eventDoList = dynamicOperationEventService.getBuyUserNameAndTime(operationQuery, gameType);
        for (DynamicOperationEventDo event : eventDoList) {
            Long payOutAmount = dynamicOperationEventDetailService.sumPayoutAmount(event.getId(), gameType);
            // 金额在10%左右 可视为完成
            DynamicOperationEventStatusEnum eventStatus = dynamicOperationEventService.getEventStatus(event.getBonus(), payOutAmount);
            if (DynamicOperationEventStatusEnum.NONE == eventStatus) {
                event.setBonusBalance(event.getBonus() - payOutAmount);
                return event;
            }
        }
        return null;
    }


    /**
     * 结果上报
     */
    @Async(dynamicOperationThreadPoolExecutor)
    public void asyncEventReport(DynamicOperationEventDo eventDo, Long detailId, GameTypeEnum gameType) {
        // 更新 订单状态
        DynamicOperationEventDetailDo updateReq = DynamicOperationEventDetailDo.builder().id(detailId).betStatus(BetStatusEnum.SUCCESS).build();
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


    public void reportEvent(Long id, String eventId, DynamicOperationEventStatusEnum eventStatus, GameTypeEnum gameType) {
        Integer eventReportStatus = DynamicOperationEventStatusEnum.convertUpdateEventsStatus(eventStatus);

        AssertUtil.assertNotNull(eventReportStatus, ErrorCode.ERR_PARAM, "eventStatus is error");
        //获取待上报所有订单
        DynamicOperationDetailQuery detailQuery = DynamicOperationDetailQuery.builder()
                .operationId(id)
                .betStatus(BetStatusEnum.SUCCESS.getCode())
                .orderByClause("createTime")
                .direction(Sort.Direction.ASC)
                .build();

        List<DynamicOperationEventDetailDo> eventDetailDos = dynamicOperationEventDetailService.queryPage(detailQuery, gameType);

        List<String> betNos = new ArrayList<>();
        long totalAmount = 0L;
        Long startTime = null;
        List<UpdateEventsBetRecordReq> betRecords = new ArrayList<>();
        for (int i = 0; i <= eventDetailDos.size(); i++) {
            DynamicOperationEventDetailDo eventDetail = eventDetailDos.get(i);
            if (i == 0) {
                startTime = eventDetail.getCreateTime();
            }
            betNos.add(String.valueOf(eventDetail.getBetRecordId()));
            totalAmount += eventDetail.getPayOutAmount();
            betRecords.add(DynamicOperationStatusReportBuilder.updateEventsBetRecordReqBuild(eventDetail));
        }

        UpdateEventsStatusReq updateEventsStatusReq = UpdateEventsStatusReq.builder()
                .eventId(eventId)
                .requestTime(DateUtil.toUCTByMilli(new Date().getTime()))
                .executeBouns(AmountUtil.toBigDecimal(totalAmount))
                .executeTime(DateUtil.toUCTByMilli(startTime))
                .betNos(betNos)
                .betRecords(betRecords)
                .eventStatus(eventReportStatus)
                .build();

        dynamicOperationApi.updateEventsStatus(updateEventsStatusReq);
        // 更新事件状态为已上报
        DynamicOperationEventDo eventReportUpdate = DynamicOperationEventDo.builder().id(id)
                .reportStatus(DynamicOperationReportStatusEnum.FINISH).build();
        dynamicOperationEventService.update(eventReportUpdate, gameType);
    }

}
