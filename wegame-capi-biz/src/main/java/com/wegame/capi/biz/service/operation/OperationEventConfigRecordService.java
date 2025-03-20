package com.wegame.capi.biz.service.operation;

import com.wegame.base.CacheConstants;
import com.wegame.capi.biz.builder.operation.OperationEventConfigRecordQueryBuilder;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.dao.mapper.domain.operation.OperationEventConfigRecordPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wegame.capi.biz.converter.operation.OperationEventConfigRecordConvert.doToPo;
import static com.wegame.capi.biz.converter.operation.OperationEventConfigRecordConvert.poToDoList;

/**
 * 干预事件配置记录服务
 */
@Slf4j
@Service
public class OperationEventConfigRecordService extends AbstractService {

    public Long add(OperationEventConfigRecordDo recordDo, OperationEventTypeEnum dynamicOperationGame) {
        OperationEventConfigRecordPo insertPo = getMongoTemplate(dynamicOperationGame.getGameType())
                .insert(doToPo(recordDo), dynamicOperationGame.getEventConfigRecordCollectionName());
        return insertPo.getId();
    }
    @Cacheable(value = CacheConstants.GAME_DYNAMIC_OPERATION, key = "#typeEnum.name() + '_' + #betRecordId")
    public List<OperationEventConfigRecordDo> listByBetRecordId(Long betRecordId, OperationEventTypeEnum typeEnum) {
        List<OperationEventConfigRecordPo> poList = getMongoTemplate(typeEnum.getGameType())
                .find(OperationEventConfigRecordQueryBuilder.buildQuery(betRecordId),
                        OperationEventConfigRecordPo.class,
                        typeEnum.getEventConfigRecordCollectionName());
        return poToDoList(poList);
    }
}
