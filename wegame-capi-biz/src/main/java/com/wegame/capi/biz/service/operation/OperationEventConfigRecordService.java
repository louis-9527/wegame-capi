package com.wegame.capi.biz.service.operation;

import com.wegame.capi.biz.builder.DynamicOperationConfigQueryBuilder;
import com.wegame.capi.biz.converter.operation.OperationEventConfigRecordConvert;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigRecordDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.OperationEventConfigRecordPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 干预事件配置记录服务
 */
@Slf4j
@Service
public class OperationEventConfigRecordService extends AbstractService {

    public Long add(OperationEventConfigRecordDo recordDo, OperationEventTypeEnum dynamicOperationGame) {
        OperationEventConfigRecordPo insertPo = getMongoTemplate(dynamicOperationGame.getGameType())
                .insert(OperationEventConfigRecordConvert.doToPo(recordDo), dynamicOperationGame.getEventConfigRecordCollectionName());
        return insertPo.getId();
    }

    public List<OperationEventConfigRecordDo> listByBetRecordId(Long betRecordId, OperationEventTypeEnum typeEnum) {
        List<OperationEventConfigRecordPo> poList = getMongoTemplate(typeEnum.getGameType())
                .find(DynamicOperationConfigQueryBuilder.buildRecordQuery(betRecordId),
                        OperationEventConfigRecordPo.class,
                        typeEnum.getEventConfigRecordCollectionName());
        return OperationEventConfigRecordConvert.poToDoList(poList);
    }
}
