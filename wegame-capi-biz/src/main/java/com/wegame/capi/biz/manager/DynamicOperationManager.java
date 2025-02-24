package com.wegame.capi.biz.manager;

import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.service.DynamicOperationEventDetailService;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wegame.capi.infrastructure.config.ThreadPoolConfig.dynamicOperationThreadPoolExecutor;

@Service
public class DynamicOperationManager {
    @Autowired
    private DynamicOperationEventService dynamicOperationEventService;
    @Autowired
    private DynamicOperationEventDetailService dynamicOperationEventDetailService;


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
    public void asyncEventReport(Long operationRecordId, Long detailId, Long bonus, GameTypeEnum gameType) {
        // 更新 订单状态
        DynamicOperationEventDetailDo updateReq = DynamicOperationEventDetailDo.builder().id(detailId).betStatus(BetStatusEnum.SUCCESS).build();
        dynamicOperationEventDetailService.update(updateReq, gameType);
        // 检查干预事件是否可上报,如果已完成则上报
        Long payOutAmount = dynamicOperationEventDetailService.sumPayoutAmount(operationRecordId, gameType);
        // 更新事件状态
        dynamicOperationEventService.updateStatus(bonus, payOutAmount, operationRecordId,null, gameType);
    }

}
