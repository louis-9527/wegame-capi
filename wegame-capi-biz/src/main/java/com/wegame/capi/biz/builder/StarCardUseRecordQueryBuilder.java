package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.promotion.StarCardUseRecordDo;
import com.wegame.capi.dao.mapper.domain.star.StarCardUseRecordQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class StarCardUseRecordQueryBuilder {

    public static Update buildMongoUpdate(StarCardUseRecordDo recordDo) {
        AssertUtil.assertNotNull(recordDo, ErrorCode.ERR_INTER);
        Update update = new Update();
        if (Objects.nonNull(recordDo.getReportStatus())) {
            update.set("reportStatus", recordDo.getReportStatus().getCode());
        }
        if (Objects.nonNull(recordDo.getUseStatus())) {
            update.set("useStatus", recordDo.getUseStatus().getCode());
        }
        if (Objects.nonNull(recordDo.getPayOutAmount())) {
            update.set("payOutAmount", recordDo.getPayOutAmount());
        }
        if (Objects.nonNull(recordDo.getFailMsg())) {
            update.set("failMsg", recordDo.getFailMsg());
        }
        if (Objects.nonNull(recordDo.getBetRecordId())) {
            update.set("betRecordId", recordDo.getBetRecordId());
        }
        if (Objects.nonNull(recordDo.getUpdateTime())) {
            update.set("updateTime", recordDo.getUpdateTime());
        } else {
            update.set("updateTime", DateUtil.getCurrentMilli());
        }
        return update;
    }


    public static Query buildQuery(StarCardUseRecordQuery starCardQuery) {
        Query query = new Query(buildCriteria(starCardQuery));
        if (Objects.nonNull(starCardQuery.getLimit())) {
            query.limit(starCardQuery.getLimit());
        }
        if (Objects.nonNull(starCardQuery.getGtLastId())) {
            query.addCriteria(Criteria.where("_id").gt(starCardQuery.getGtLastId()));
        }
        if (StringUtils.isNotEmpty(starCardQuery.getOrderByClause())) {
            if (Objects.isNull(starCardQuery.getDirection())) {
                query.with(Sort.by(Sort.Direction.DESC, starCardQuery.getOrderByClause()));
            } else {
                query.with(Sort.by(starCardQuery.getDirection(), starCardQuery.getOrderByClause()));
            }
        }
        return query;
    }

    public static Criteria buildCriteria(StarCardUseRecordQuery starCardQuery) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(starCardQuery.getUseStatus())) {
            criteria.and("useStatus").is(starCardQuery.getUseStatus());
        }
        if (Objects.nonNull(starCardQuery.getReportStatus())) {
            criteria.and("reportStatus").is(starCardQuery.getReportStatus());
        }
        if (Objects.nonNull(starCardQuery.getLtCreateTime()) && Objects.nonNull(starCardQuery.getGteCreateTime())) {
            criteria.andOperator(
                    Criteria.where("createTime").gte(starCardQuery.getGteCreateTime()),
                    Criteria.where("createTime").lt(starCardQuery.getLtCreateTime())
            );

        }
        return criteria;
    }


}
