package com.wegame.capi.biz.builder.operation;

import com.wegame.capi.dao.repository.domain.operation.OperationEventConfigQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class OperationEventConfigQueryBuilder {

    public static Query buildQuery(OperationEventConfigQuery recordQuery) {
        Criteria criteria = new Criteria();
        criteria.and("multiple").is(recordQuery.getMultiple());
        return new Query(criteria);
    }

    public static Query buildQuery(String multipleRangeStart, String multipleRangeEnd) {
        Criteria criteria = Criteria.where("multiple").gte(multipleRangeStart).lte(multipleRangeEnd);
        Query query = new Query(criteria);
        query.fields().include("_id");
        return query;
    }
}
