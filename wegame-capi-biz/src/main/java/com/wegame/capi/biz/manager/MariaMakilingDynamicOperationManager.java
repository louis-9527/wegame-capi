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

    private static final Map<Double, List<List<DynamicOperationConfigDo>>> freeConfigDetailArray = new ConcurrentHashMap<>();

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
        if (BetTypeEnum.NORMAL_BET.getCode().equals(configQuery.getBetType())) {
            return normalConfigDetailArray.computeIfAbsent(multiple, k -> super.getConfigDetail(configQuery));
        } else if (BetTypeEnum.BUY_BET.getCode().equals(configQuery.getBetType())) {
            return freeConfigDetailArray.computeIfAbsent(multiple, k -> super.getConfigDetail(configQuery));
        }
        return null;
    }

    @Override
    public List<Double> getConfigMultipleArray(BetTypeEnum betType) {
        // String gameId = this.getDynamicOperationGameEnum().getGameType().getId();
        // 获取该 gameId 对应的 Map，如果不存在则创建
        // Map<Integer, List<Double>> typeMap = configMultipleArray.computeIfAbsent(gameId, k -> new ConcurrentHashMap<>());
        // 获取 gameType 对应的配置，如果不存在则查询数据库
        return configMultipleArray.computeIfAbsent(betType.getCode(), type -> super.getConfigMultipleArray(betType));
    }
}
