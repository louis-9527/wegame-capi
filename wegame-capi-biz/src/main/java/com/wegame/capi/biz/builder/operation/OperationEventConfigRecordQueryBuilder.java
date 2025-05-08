package com.wegame.capi.biz.builder.operation;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class OperationEventConfigRecordQueryBuilder {

    public static Query buildQuery(Long recordId) {
        Criteria criteria = new Criteria();
        criteria.and("betRecordId").is(recordId);
        return new Query(criteria);
    }
}
