package com.wegame.capi.biz.service.operation;

import com.google.common.collect.Lists;
import com.wegame.capi.biz.builder.operation.OperationEventConfigQueryBuilder;
import com.wegame.capi.biz.converter.operation.OperationEventConfigConvert;
import com.wegame.capi.biz.domain.biz.operation.OperationEventConfigDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.OperationEventConfigPo;
import com.wegame.capi.dao.mapper.domain.business.OperationEventConfigQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.converter.operation.OperationEventConfigConvert.doToPo;

/**
 * 干预事件配置服务
 */
@Slf4j
@Service
public class OperationEventConfigService extends AbstractService {

    private static final int partitionSize = 10;

    public Long add(OperationEventConfigDo configDo, OperationEventTypeEnum dynamicOperationGame) {
        OperationEventConfigPo insert = getMongoTemplate(dynamicOperationGame.getGameType()).insert(doToPo(configDo),
                dynamicOperationGame.getEventConfigCollectionName());
        return insert.getId();
    }

    public List<List<OperationEventConfigDo>> getConfigDetail(OperationEventConfigQuery configQuery, OperationEventTypeEnum typeEnum) {
        List<OperationEventConfigPo> poList = getMongoTemplate(typeEnum.getGameType()).find(OperationEventConfigQueryBuilder.buildQuery(configQuery),
                OperationEventConfigPo.class,
                typeEnum.getEventConfigCollectionName());
        List<OperationEventConfigDo> doList = OperationEventConfigConvert.poToDoList(poList);
        if (CollectionUtils.isEmpty(doList)) {
            return new ArrayList<>();
        }
        return Lists.partition(doList, partitionSize);
    }

    public List<Double> getConfigMultipleArray(OperationEventTypeEnum typeEnum) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group().addToSet("multiple").as("values")
        );
        AggregationResults<Document> results = getMongoTemplate(typeEnum.getGameType())
                .aggregate(aggregation, typeEnum.getEventConfigCollectionName(), Document.class);
        return results.getMappedResults().stream()
                // 提取 "values" 数组
                .flatMap(doc -> ((List<?>) doc.get("values")).stream())
                // 转换为 Double
                .map(o -> ((Number) o).doubleValue())
                // 去重
                .distinct()
                // **保证升序排序**
                .sorted()
                .collect(Collectors.toList());
    }
}
