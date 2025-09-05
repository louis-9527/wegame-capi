package com.wegame.capi.biz.builder.template;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetTypeEnum;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;


import java.math.BigDecimal;

public class BetRecordTemplateQueryBuilder {

    public static Query buildQuery(BetTypeEnum betTypeEnum, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd) {
        AssertUtil.assertNotNull(betTypeEnum, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multipleRangeStart, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multipleRangeEnd, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(multipleRangeStart.compareTo(multipleRangeEnd) <= 0, ErrorCode.ERR_PARAM);
        Criteria criteria;
        if (multipleRangeStart.compareTo(multipleRangeEnd) == 0) {
            criteria = Criteria.where("multiple")
                    .is(multipleRangeStart.doubleValue())
                    .and("betType").is(betTypeEnum.getCode());
        } else {
            criteria = Criteria.where("multiple")
                    .gt(multipleRangeStart.doubleValue())
                    .lte(multipleRangeEnd.doubleValue())
                    .and("betType").is(betTypeEnum.getCode());
        }
        AssertUtil.assertNotNull(criteria, ErrorCode.ERR_PARAM);
        Query query = new Query(criteria);
        query.fields().include("_id");
        return query;
    }


    public static NativeSearchQuery buildListIdEsQuery(BetTypeEnum betTypeEnum, BigDecimal multipleRangeStart, BigDecimal multipleRangeEnd) {
        AssertUtil.assertNotNull(betTypeEnum, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multipleRangeStart, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(multipleRangeEnd, ErrorCode.ERR_PARAM);
        AssertUtil.assertTrue(multipleRangeStart.compareTo(multipleRangeEnd) <= 0, ErrorCode.ERR_PARAM);

        // 创建布尔查询构建器
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 添加betType条件
        boolQuery.filter(QueryBuilders.termQuery("betType", betTypeEnum.getCode()));

        // 添加multiple范围条件
        if (multipleRangeStart.compareTo(multipleRangeEnd) == 0) {
            // 等于条件
            boolQuery.filter(QueryBuilders.termQuery("multiple", multipleRangeStart.doubleValue()));
        } else {
            // 范围条件 (大于start，小于等于end)
            boolQuery.filter(QueryBuilders.rangeQuery("multiple")
                    .gt(multipleRangeStart.doubleValue())
                    .lte(multipleRangeEnd.doubleValue()));
        }
        // 只取id字段，不返回_source
        SourceFilter sourceFilter = new FetchSourceFilter(new String[]{"id"}, null);
        // 构建NativeSearchQuery并只返回id字段
        return new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSourceFilter(sourceFilter)
                .build();
    }
}
