package com.wegame.capi.biz.manager;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.service.DynamicOperationEventDetailService;
import com.wegame.capi.biz.service.DynamicOperationEventService;
import com.wegame.capi.client.enums.GameTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.wegame.capi.infrastructure.config.MongodbTransactionConfig.mariamakilingTransactionManager;

@Service
public class DynamicOperationManager {
    @Autowired
    private DynamicOperationEventService dynamicOperationEventService;
    @Autowired
    private DynamicOperationEventDetailService dynamicOperationEventDetailService;

    @Transactional(transactionManager = mariamakilingTransactionManager)
    public void updateEvent(DynamicOperationEventDo eventDo, DynamicOperationEventDetailDo eventDetailDo, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(eventDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(eventDetailDo, ErrorCode.ERR_PARAM);
        Boolean eventUpdateRes = dynamicOperationEventService.updateById(eventDo, gameType);
        AssertUtil.assertTrue(eventUpdateRes, ErrorCode.ERR_INTER);
        Long detailId = dynamicOperationEventDetailService.add(eventDetailDo, gameType);
        AssertUtil.assertNotNull(detailId, ErrorCode.ERR_INTER);
    }
}
