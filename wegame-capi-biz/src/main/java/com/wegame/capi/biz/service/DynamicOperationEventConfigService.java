package com.wegame.capi.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.DynamicOperationConfigQueryBuilder;
import com.wegame.capi.biz.converter.DynamicOperationConfigConvert;
import com.wegame.capi.biz.converter.DynamicOperationConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationConfigDo;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.DynamicOperationGameEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.DynamicOperationEventTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigPo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigQuery;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DynamicOperationEventConfigService {
    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;

    private static final int partitionSize = 10;

    public List<List<DynamicOperationConfigDo>> getConfigDetail(DynamicOperationConfigQuery configQuery, DynamicOperationGameEnum dynamicOperationGame) {
        List<DynamicOperationConfigPo> dynamicOperationConfigPos = getMongoTemplate(dynamicOperationGame.getGameType()).find(DynamicOperationConfigQueryBuilder.buildQuery(configQuery), DynamicOperationConfigPo.class, dynamicOperationGame.getEventConfigCollectionName());
        List<DynamicOperationConfigDo> dynamicOperationConfigDos = DynamicOperationConfigConvert.convertDos(dynamicOperationConfigPos);
        if (CollectionUtils.isEmpty(dynamicOperationConfigDos)) {
            return new ArrayList<>();
        }
        return Lists.partition(dynamicOperationConfigDos, partitionSize);
    }


    public List<Double> getConfigMultipleArray(BetTypeEnum betType, DynamicOperationGameEnum dynamicOperationGame) {


        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("betType").is(betType.getCode())), // 添加 type 过滤
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


    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }

}
