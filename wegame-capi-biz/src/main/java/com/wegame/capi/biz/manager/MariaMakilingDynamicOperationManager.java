package com.wegame.capi.biz.manager;

import com.google.common.collect.Lists;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.DynamicOperationGameEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationEventTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigQuery;
import io.swagger.models.auth.In;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.wegame.capi.infrastructure.config.MongodbTransactionConfig.mariamakilingTransactionManager;

@Service
@Component("mariaMakilingDynamicOperationManager")
public class MariaMakilingDynamicOperationManager extends AbstractDynamicOperationManager {
    // 使用 ConcurrentHashMap 进行缓存
    private static final Map<String, List<Double>> configMultipleArray = new ConcurrentHashMap<>();
    private static final Map<Double, List<List<DynamicOperationConfigDo>>> normalConfigDetailArray = new ConcurrentHashMap<>();

    @Override
    @Transactional(transactionManager = mariamakilingTransactionManager)
    public Long addEventDetail(DynamicOperationEventDetailDo eventDetailDo) {
        return super.addEventDetail(eventDetailDo);
    }


    @Override
    @Transactional(transactionManager = mariamakilingTransactionManager)
    public void updateEventDetail(DynamicOperationEventDetailDo eventDetailDo, DynamicOperationEventDo eventDo) {
        super.updateEventDetail(eventDetailDo, eventDo);
    }

    @Override
    public DynamicOperationGameEnum getDynamicOperationGameEnum() {
        return DynamicOperationGameEnum.MARIA_MAKILING_DYNAMIC;
    }

    @Override
    public List<List<DynamicOperationConfigDo>> getConfigDetail(DynamicOperationConfigQuery configQuery) {
        Double multiple = configQuery.getMultiple();
        return normalConfigDetailArray.computeIfAbsent(multiple, k -> super.getConfigDetail(configQuery));
    }

    @Override
    public List<Double> getConfigMultipleArray() {
        String key = this.getDynamicOperationGameEnum().getGameType().getId();
        return configMultipleArray.computeIfAbsent(key, k -> super.getConfigMultipleArray());
    }
}
