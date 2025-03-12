package com.wegame.capi.biz.builder;

import com.wegame.capi.dao.mapper.domain.business.DynamicOperationConfigQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class DynamicOperationConfigQueryBuilder {

    public static Query buildQuery(DynamicOperationConfigQuery recordQuery) {

        Criteria criteria = new Criteria();
        criteria.and("multiple").is(recordQuery.getMultiple());
        criteria.and("gameId").is(recordQuery.getGameId());
        criteria.and("betType").is(recordQuery.getBetType());
        return new Query(criteria);
    }
}
