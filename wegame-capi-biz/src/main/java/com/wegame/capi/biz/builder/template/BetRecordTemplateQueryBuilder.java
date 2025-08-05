package com.wegame.capi.biz.builder.template;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class BetRecordTemplateQueryBuilder {

    public static Query buildQuery(String multipleRangeStart, String multipleRangeEnd) {
        // 将字符串参数转换为Double类型
        Double start = Double.parseDouble(multipleRangeStart);
        Double end = Double.parseDouble(multipleRangeEnd);

        // 使用Double类型进行数值比较
        Criteria criteria = Criteria.where("multiple").gte(start).lte(end);
        Query query = new Query(criteria);
        query.fields().include("_id");
        return query;
    }
}
