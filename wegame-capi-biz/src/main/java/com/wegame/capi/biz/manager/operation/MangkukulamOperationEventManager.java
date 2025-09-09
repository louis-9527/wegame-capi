package com.wegame.capi.biz.manager.operation;

import com.wegame.capi.biz.domain.biz.operation.OperationEventDetailDo;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.dao.repository.domain.operation.OperationEventConfigQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.wegame.capi.biz.config.mongo.MangkukulamMongoConfig.mangkukulamTransactionManager;

@Component("mangkukulamOperationEventManager")
public class MangkukulamOperationEventManager extends AbstractOperationEventManager {

    @Override
    public OperationEventTypeEnum operationEventType() {
        return OperationEventTypeEnum.MANGKUKULAM;
    }

    // 使用 ConcurrentHashMap 进行缓存
    private static final Map<String, List<Double>> configMultipleArray = new ConcurrentHashMap<>();
    private static final Map<Double, List<List<Long>>> normalConfigDetailArray = new ConcurrentHashMap<>();

    @Override
    @Transactional(transactionManager = mangkukulamTransactionManager)
    public void updateEventDetail(OperationEventDetailDo eventDetailDo, OperationEventDo eventDo) {
        super.updateEventDetail(eventDetailDo, eventDo);
    }

    @Override
    @Transactional(transactionManager = mangkukulamTransactionManager)
    public Long addEventDetail(OperationEventDetailDo eventDetailDo) {
        return super.addEventDetail(eventDetailDo);
    }


    @Override
    public List<List<Long>> getConfigIds(OperationEventConfigQuery configQuery) {
        Double multiple = configQuery.getMultiple();
        return normalConfigDetailArray.computeIfAbsent(multiple, k -> super.getConfigIds(configQuery));
    }

    @Override
    public List<Double> getConfigMultipleArray() {
        String key = this.operationEventType().getGameType().getId();
        return configMultipleArray.computeIfAbsent(key, k -> super.getConfigMultipleArray());
    }

}
