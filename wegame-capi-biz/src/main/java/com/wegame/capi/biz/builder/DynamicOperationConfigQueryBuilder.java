package com.wegame.capi.biz.builder;

import com.wegame.capi.dao.mapper.domain.business.OperationEventConfigQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class DynamicOperationConfigQueryBuilder {

    public static Query buildQuery(OperationEventConfigQuery recordQuery) {
        Criteria criteria = new Criteria();
        criteria.and("multiple").is(recordQuery.getMultiple());
        criteria.and("gameId").is(recordQuery.getGameId());
        return new Query(criteria);
    }

    public static Query buildRecordQuery(Long recordId) {
        Criteria criteria = new Criteria();
        criteria.and("betRecordId").is(recordId);
        return new Query(criteria);
    }
}
