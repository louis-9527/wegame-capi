package com.wegame.capi.biz.service;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.DynamicOperationApi;
import com.wegame.capi.api.domain.req.dynamicoperation.UpdateEventsStatusReq;
import com.wegame.capi.biz.builder.BetRecordQueryBuilder;
import com.wegame.capi.biz.builder.DynamicOperationQueryBuilder;
import com.wegame.capi.biz.builder.GameRecordQueryBuilder;
import com.wegame.capi.biz.converter.DynamicOperationConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.BetRecordPo;
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
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private Map<String, MongoTemplate> mongoTemplateMap;
    @Autowired
    private DynamicOperationApi dynamicOperationApi;

    private final String dynamicOperationEventCollectionName = "mariamakiling_dynamicOperation_event";

    public Long add(DynamicOperationEventDo dynamicOperationMsgDo, GameTypeEnum gameType) {
        dynamicOperationMsgDo.setId(snowflakeIdGenerator.nextId());
        DynamicOperationPo dynamicOperationPo = DynamicOperationConvert.convertPo(dynamicOperationMsgDo, OptEnum.ADD);
        if (Objects.isNull(dynamicOperationPo)) {
            return null;
        }
        DynamicOperationPo insert = getMongoTemplate(gameType).insert(dynamicOperationPo, dynamicOperationEventCollectionName);
        return insert.getId();
    }

    public Boolean update(DynamicOperationEventDo eventDo, GameTypeEnum gameType) {
        Criteria criteria = new Criteria();
        criteria.and("_id").is(eventDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(gameType).updateFirst(query,
                DynamicOperationQueryBuilder.buildUpdateById(eventDo),
                DynamicOperationPo.class,
                dynamicOperationEventCollectionName);

        return result.getModifiedCount() > 0;


    }

    public List<DynamicOperationEventDo> queryPage(DynamicOperationQuery operationQuery, GameTypeEnum gameType) {

        List<DynamicOperationPo> operationPoList = getMongoTemplate(gameType).find(DynamicOperationQueryBuilder.buildQuery(operationQuery), DynamicOperationPo.class, dynamicOperationEventCollectionName);
        // 判断干预金额
        return DynamicOperationConvert.convertDos(operationPoList);
    }


    /**
     * 获取当前玩家在生效中的干预时间,并按时间取第一条
     *
     * @return
     */
    public List<DynamicOperationEventDo> getBuyUserNameAndTime(DynamicOperationQuery operationQuery, GameTypeEnum gameType) {

        List<DynamicOperationPo> operationPoList = getMongoTemplate(gameType).find(DynamicOperationQueryBuilder.buildQueryBuyUserNameAndTime(operationQuery), DynamicOperationPo.class, dynamicOperationEventCollectionName);
        // 判断干预金额
        return DynamicOperationConvert.convertDos(operationPoList);
    }

    public DynamicOperationEventDo getById(Long id, GameTypeEnum gameType) {
        AssertUtil.assertNotNull(id, ErrorCode.ERR_PARAM);
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        Query query = new Query(criteria);
        query.fields().include("id", "bonus", "eventKind", "eventId");
        DynamicOperationPo operationPo = getMongoTemplate(gameType).findOne(
                query,
                DynamicOperationPo.class,
                dynamicOperationEventCollectionName);
        return DynamicOperationConvert.convertDo(operationPo);
    }



    public DynamicOperationEventStatusEnum getEventStatus(Long bonus, Long use) {
        long lowerBound = (long) (bonus * 0.9);
        if (use >= lowerBound) {
            return DynamicOperationEventStatusEnum.FINISH_NORMAL;
        }
        return DynamicOperationEventStatusEnum.NONE;
    }


    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        MongoTemplate template = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MongodbTemplateConfig.MONGOTEMPLATE);
        AssertUtil.assertNotNull(template, ErrorCode.ERR_INTER);
        return template;
    }


}
