package com.wegame.capi.biz.builder.operation;

import com.wegame.capi.dao.repository.domain.operation.OperationEventConfigQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class OperationEventConfigQueryBuilder {

    public static Query buildQuery(OperationEventConfigQuery recordQuery) {
        Criteria criteria = new Criteria();
        criteria.and("gameId").is(recordQuery.getGameId());
        criteria.and("multiple").is(recordQuery.getMultiple());
        return new Query(criteria);
    }
}
