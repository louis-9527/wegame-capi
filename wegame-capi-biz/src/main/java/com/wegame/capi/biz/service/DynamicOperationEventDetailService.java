package com.wegame.capi.biz.service;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.DynamicOperationDetailQueryBuilder;
import com.wegame.capi.biz.converter.DynamicOperationDetailConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationDetailPo;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class DynamicOperationEventDetailService {
    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;
    private final String dynamicOperationEventDetailCollectionName = "mariamakiling_dynamicOperation_event_detail";

    public Long add(DynamicOperationEventDetailDo detailDo, GameTypeEnum gameType) {
        DynamicOperationDetailPo detailPo = DynamicOperationDetailConvert.convertPo(detailDo, OptEnum.ADD);
        if (Objects.isNull(detailPo)) {
            return null;
        }
        DynamicOperationDetailPo insert = getMongoTemplate(gameType).insert(detailPo, dynamicOperationEventDetailCollectionName);
        return insert.getId();
    }

    public Long sumPayoutAmount(Long operatorRecordId, GameTypeEnum gameType) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("operatorRecordId").is(operatorRecordId));

        // 计算 payOutAmount 的总和
        GroupOperation sumOperation = Aggregation.group().sum("payOutAmount").as("totalPayOutAmount");

        // 构建 Aggregation 查询管道
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, sumOperation);
        AggregationResults<Document> results = getMongoTemplate(gameType).aggregate(aggregation, dynamicOperationEventDetailCollectionName, Document.class);
        // 获取 totalPayOutAmount 值
        Document result = results.getUniqueMappedResult();
        return result != null ? result.getLong("totalPayOutAmount") : 0L; // 如果查询为空，返回 0
    }


    public Boolean update(DynamicOperationEventDetailDo detailDo, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(detailDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(detailDo.getId(), ErrorCode.ERR_PARAM, "request param \'id\' is null");
        Criteria criteria = new Criteria();
        criteria.and("_id").is(detailDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(gameType).updateFirst(query,
                DynamicOperationDetailQueryBuilder.buildUpdateStatus(detailDo),
                DynamicOperationDetailPo.class,
                dynamicOperationEventDetailCollectionName);
        return result.getModifiedCount() > 0;
    }

    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }
}
