package com.wegame.capi.biz.service.operation;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.operation.OperationEventDetailConvert;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDetailDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.dao.mapper.domain.business.OperationEventDetailPo;
import com.wegame.capi.dao.mapper.domain.business.OperationEventDetailQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wegame.capi.biz.builder.DynamicOperationDetailQueryBuilder.buildQuery;
import static com.wegame.capi.biz.builder.DynamicOperationDetailQueryBuilder.buildUpdate;
import static com.wegame.capi.biz.converter.operation.OperationEventDetailConvert.poToDoList;

/**
 * 干预事件明细服务
 */
@Slf4j
@Component
public class OperationEventDetailService extends AbstractService {

    public Long add(DynamicOperationEventDetailDo detailDo, OperationEventTypeEnum gameEnum) {
        OperationEventDetailPo po = OperationEventDetailConvert.doToPo(detailDo, OptEnum.ADD);
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
        return poToDoList(poList);
    }
}
