package com.wegame.capi.biz.builder.template;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class BetRecordTemplateQueryBuilder {

    public static Query buildQuery(String multipleRangeStart, String multipleRangeEnd) {
        Criteria criteria = Criteria.where("multiple").gte(multipleRangeStart).lte(multipleRangeEnd);
        Query query = new Query(criteria);
        query.fields().include("_id");
        return query;
    }
}
