package com.wegame.capi.biz.manager;

import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigQuery;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.wegame.capi.infrastructure.config.MongodbTransactionConfig.mariamakilingTransactionManager;

@Service
@Component("mariaMakilingOperationEventManager")
public class MariaMakilingOperationEventManager extends AbstractOperationEventManager {
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
    public OperationEventTypeEnum getDynamicOperationGameEnum() {
        return OperationEventTypeEnum.MARIA_MAKILING;
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
