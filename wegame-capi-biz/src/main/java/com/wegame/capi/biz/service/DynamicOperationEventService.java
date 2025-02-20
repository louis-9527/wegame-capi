package com.wegame.capi.biz.service;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.DynamicOperationQueryBuilder;
import com.wegame.capi.biz.builder.GameRecordQueryBuilder;
import com.wegame.capi.biz.converter.DynamicOperationConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationPo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import com.wegame.capi.dao.mapper.domain.business.GameRecordQuery;
import com.wegame.capi.infrastructure.config.MongodbTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class DynamicOperationEventService {
    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;

    private final String dynamicOperationEventCollectionName = "mariamakiling_dynamicOperation_event";

    public Long add(DynamicOperationEventDo dynamicOperationMsgDo, GameTypeEnum gameType) {
        DynamicOperationPo dynamicOperationPo = DynamicOperationConvert.convertPo(dynamicOperationMsgDo, OptEnum.ADD);
        if (Objects.isNull(dynamicOperationPo)) {
            return null;
        }
        DynamicOperationPo insert = getMongoTemplate(gameType).insert(dynamicOperationPo, dynamicOperationEventCollectionName);
        return insert.getId();
    }

    public Boolean updateById(DynamicOperationEventDo dynamicOperationMsgDo, GameTypeEnum gameType) {
        Criteria criteria = new Criteria();
        criteria.and("_id").is(dynamicOperationMsgDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(gameType).updateFirst(query,
                DynamicOperationQueryBuilder.buildUpdateById(dynamicOperationMsgDo),
                DynamicOperationPo.class,
                dynamicOperationEventCollectionName);

        return result.getModifiedCount() > 0;


    }


    /**
     * 获取当前玩家在生效中的干预时间,并按时间取第一条
     *
     * @return
     */
    public DynamicOperationEventDo getBuyUserNameAndTime(DynamicOperationQuery operationQuery,GameTypeEnum gameType) {

        DynamicOperationPo dynamicOperationPo = getMongoTemplate(gameType).findOne(DynamicOperationQueryBuilder.buildQueryBuyUserNameAndTime(operationQuery), DynamicOperationPo.class);
        return DynamicOperationConvert.convertDo(dynamicOperationPo);
    }


    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }
}
