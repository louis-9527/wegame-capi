package com.wegame.capi.biz.builder.template;

import com.wegame.capi.client.enums.BetTypeEnum;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;

public class BetRecordTemplateQueryBuilder {

    public static Query buildQuery(BetTypeEnum betTypeEnum,BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd) {
        Criteria criteria = Criteria.where("multiple")
                .gte(multipleRangeStart.doubleValue())
                .lte(multipleRangeEnd.doubleValue())
                .and("betType").is(betTypeEnum.getCode());
        Query query = new Query(criteria);
        query.fields().include("_id");
        return query;
    }
}
