package com.wegame.capi.biz.service.operation;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.OperationEventDetailConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.OperationEventDetailPo;
import com.wegame.capi.dao.mapper.domain.business.OperationEventDetailQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.wegame.capi.biz.builder.DynamicOperationDetailQueryBuilder.buildQuery;
import static com.wegame.capi.biz.builder.DynamicOperationDetailQueryBuilder.buildUpdate;
import static com.wegame.capi.biz.converter.OperationEventDetailConvert.toDoList;
import static com.wegame.capi.infrastructure.config.MongodbTemplateConfig.MONGOTEMPLATE;
import static com.wegame.capi.infrastructure.config.MongodbTemplateConfig.PRIMARY_MONGOTEMPLATE;

/**
 * 干预事件明细服务
 */
@Slf4j
@Component
public class OperationEventDetailService implements InitializingBean {

    @Autowired
    Map<String, MongoTemplate> mongoTemplateMap;

    @Override
    public void afterPropertiesSet() {
        Arrays.stream(GameTypeEnum.values()).forEach(gameType -> {
            MongoTemplate mongoTemplate = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MONGOTEMPLATE);
            AssertUtil.assertNotNull(mongoTemplate, ErrorCode.ERR_INTER);
            MongoTemplate primaryMongoTemplate = mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + PRIMARY_MONGOTEMPLATE);
            AssertUtil.assertNotNull(primaryMongoTemplate, ErrorCode.ERR_INTER);
        });
    }

    public Long add(DynamicOperationEventDetailDo detailDo, OperationEventTypeEnum gameEnum) {
        OperationEventDetailPo po = OperationEventDetailConvert.toPo(detailDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_INTER);
        OperationEventDetailPo insertPo = getMongoTemplate(gameEnum.getGameType()).insert(po, gameEnum.getEventDetailCollectionName());
        return insertPo.getId();
    }

    public boolean update(DynamicOperationEventDetailDo detailDo, OperationEventTypeEnum gameEnum) {
        AssertUtil.assertNotNull(detailDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(detailDo.getId(), ErrorCode.ERR_PARAM, "request param \'id\' is null");
        Criteria criteria = new Criteria();
        criteria.and("_id").is(detailDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(gameEnum.getGameType()).updateFirst(query,
                buildUpdate(detailDo),
                OperationEventDetailPo.class,
                gameEnum.getEventDetailCollectionName());
        return result.getModifiedCount() > 0;
    }

    public List<DynamicOperationEventDetailDo> list(OperationEventDetailQuery query, OperationEventTypeEnum gameEnum) {
        List<OperationEventDetailPo> poList = getMongoTemplate(gameEnum.getGameType()).find(buildQuery(query),
                OperationEventDetailPo.class,
                gameEnum.getEventDetailCollectionName());
        return toDoList(poList);
    }

    ///////////////  above auto generated ///////////////
    private MongoTemplate getMongoTemplate(GameTypeEnum gameType) {
        return mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + MONGOTEMPLATE);
    }

    private MongoTemplate getPrimaryMongoTemplate(GameTypeEnum gameType) {
        return mongoTemplateMap.get(StringUtils.lowerCase(gameType.getName()) + PRIMARY_MONGOTEMPLATE);
    }
}
