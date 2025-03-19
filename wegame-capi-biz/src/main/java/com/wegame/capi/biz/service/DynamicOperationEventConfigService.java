package com.wegame.capi.biz.service;

import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.DynamicOperationConfigQueryBuilder;
import com.wegame.capi.biz.converter.DynamicOperationConfigConvert;
import com.wegame.capi.biz.domain.biz.game.GameMultipleRecordDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigPo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigRecordPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wegame.capi.infrastructure.config.MongodbTemplateConfig.MONGOTEMPLATE;
import static com.wegame.capi.infrastructure.config.MongodbTemplateConfig.PRIMARY_MONGOTEMPLATE;

@Slf4j
@Service
public class DynamicOperationEventConfigService implements InitializingBean {

    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;

    private static final int partitionSize = 10;

    @Override
    public void afterPropertiesSet() {
        Arrays.stream(GameTypeEnum.values()).forEach(gameType -> {
            MongoTemplate mongoTemplate = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MONGOTEMPLATE);
            AssertUtil.assertNotNull(mongoTemplate, ErrorCode.ERR_INTER);
            MongoTemplate primaryMongoTemplate = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + PRIMARY_MONGOTEMPLATE);
            AssertUtil.assertNotNull(primaryMongoTemplate, ErrorCode.ERR_INTER);
        });
    }

    public List<List<DynamicOperationConfigDo>> getConfigDetail(DynamicOperationConfigQuery configQuery, OperationEventTypeEnum dynamicOperationGame) {
        List<DynamicOperationConfigPo> dynamicOperationConfigPos = getMongoTemplate(dynamicOperationGame.getGameType()).find(DynamicOperationConfigQueryBuilder.buildQuery(configQuery), DynamicOperationConfigPo.class, dynamicOperationGame.getEventConfigCollectionName());
        List<DynamicOperationConfigDo> dynamicOperationConfigDos = DynamicOperationConfigConvert.convertDos(dynamicOperationConfigPos);
        if (CollectionUtils.isEmpty(dynamicOperationConfigDos)) {
            return new ArrayList<>();
        }
        return Lists.partition(dynamicOperationConfigDos, partitionSize);
    }


    public List<Double> getConfigMultipleArray(OperationEventTypeEnum dynamicOperationGame) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group().addToSet("multiple").as("values")
        );
        AggregationResults<Document> results = getMongoTemplate(dynamicOperationGame.getGameType())
                .aggregate(aggregation, dynamicOperationGame.getEventConfigCollectionName(), Document.class);
        return results.getMappedResults().stream()
                .flatMap(doc -> ((List<?>) doc.get("values")).stream()) // 提取 "values" 数组
                .map(o -> ((Number) o).doubleValue()) // 转换为 Double
                .distinct() // 去重
                .sorted() // **保证升序排序**
                .collect(Collectors.toList());
    }


    public <WD extends SymbolDo> List<GameMultipleRecordDo<WD>> listRecord(Long recordId, OperationEventTypeEnum dynamicOperationGame) {
        List<DynamicOperationConfigRecordPo> dynamicOperationConfigPos = getMongoTemplate(dynamicOperationGame.getGameType())
                .find(DynamicOperationConfigQueryBuilder.buildRecordQuery(recordId), DynamicOperationConfigRecordPo.class, dynamicOperationGame.getEventConfigRecordCollectionName());
        return DynamicOperationConfigConvert.convertRecordDos(dynamicOperationConfigPos);
    }


    public Long addConfig(DynamicOperationConfigPo configPo, OperationEventTypeEnum dynamicOperationGame) {
        DynamicOperationConfigPo insert = getMongoTemplate(dynamicOperationGame.getGameType()).insert(configPo, dynamicOperationGame.getEventConfigCollectionName());
        return insert.getId();
    }

    public Long addConfigRecord(DynamicOperationConfigRecordPo recordPo, OperationEventTypeEnum dynamicOperationGame) {
        DynamicOperationConfigRecordPo insert = getMongoTemplate(dynamicOperationGame.getGameType()).insert(recordPo, dynamicOperationGame.getEventConfigRecordCollectionName());
        return insert.getId();
    }

    ///////////////  above auto generated ///////////////
    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        return mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MONGOTEMPLATE);
    }

    private MongoTemplate getPrimaryMongoTemplate(GameTypeEnum gameType) {
        return mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + PRIMARY_MONGOTEMPLATE);
    }
}
