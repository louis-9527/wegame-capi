package com.wegame.capi.biz.builder.template;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.client.enums.BetTypeEnum;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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
}
