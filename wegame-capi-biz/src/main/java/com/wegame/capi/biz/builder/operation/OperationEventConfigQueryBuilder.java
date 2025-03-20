package com.wegame.capi.biz.builder.operation;

import com.wegame.capi.dao.mapper.domain.business.OperationEventConfigQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class OperationEventConfigQueryBuilder {

    public static Query buildQuery(OperationEventConfigQuery recordQuery) {
        Criteria criteria = new Criteria();
        criteria.and("multiple").is(recordQuery.getMultiple());
        criteria.and("gameId").is(recordQuery.getGameId());
        return new Query(criteria);
    }
}
