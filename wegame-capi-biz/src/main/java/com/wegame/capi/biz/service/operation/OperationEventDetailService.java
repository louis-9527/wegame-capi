package com.wegame.capi.biz.service.operation;

import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.enums.OptEnum;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDetailDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.OperationEventTypeEnum;
import com.wegame.capi.dao.repository.domain.operation.OperationEventDetailPo;
import com.wegame.capi.dao.repository.domain.operation.OperationEventDetailQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wegame.capi.biz.builder.operation.OperationEventDetailQueryBuilder.buildQuery;
import static com.wegame.capi.biz.builder.operation.OperationEventDetailQueryBuilder.buildUpdate;
import static com.wegame.capi.biz.converter.operation.OperationEventDetailConvert.doToPo;
import static com.wegame.capi.biz.converter.operation.OperationEventDetailConvert.poToDoList;

/**
 * 干预事件明细服务
 */
@Slf4j
@Component
public class OperationEventDetailService extends AbstractService {

    public Long add(OperationEventDetailDo detailDo, OperationEventTypeEnum typeEnum) {
        OperationEventDetailPo po = doToPo(detailDo, OptEnum.ADD);
        AssertUtil.assertNotNull(po, ErrorCode.ERR_INTER);
        OperationEventDetailPo insertPo = getMongoTemplate(typeEnum.getGameType()).insert(po, typeEnum.getEventDetailCollectionName());
        return insertPo.getId();
    }

    public boolean update(OperationEventDetailDo detailDo, OperationEventTypeEnum typeEnum) {
        AssertUtil.assertNotNull(detailDo, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(detailDo.getId(), ErrorCode.ERR_PARAM, "param \'id\' is null");
        Criteria criteria = new Criteria();
        criteria.and("_id").is(detailDo.getId());
        Query query = new Query(criteria);
        UpdateResult result = getMongoTemplate(typeEnum.getGameType()).updateFirst(query,
                buildUpdate(detailDo),
                OperationEventDetailPo.class,
                typeEnum.getEventDetailCollectionName());
        return result.getModifiedCount() > 0;
    }

    public List<OperationEventDetailDo> list(OperationEventDetailQuery query, OperationEventTypeEnum typeEnum) {
        List<OperationEventDetailPo> poList = getMongoTemplate(typeEnum.getGameType()).find(buildQuery(query),
                OperationEventDetailPo.class,
                typeEnum.getEventDetailCollectionName());
        return poToDoList(poList);
    }
}
