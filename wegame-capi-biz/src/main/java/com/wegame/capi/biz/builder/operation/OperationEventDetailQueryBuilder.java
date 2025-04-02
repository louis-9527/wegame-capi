package com.wegame.capi.biz.builder.operation;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDetailDo;
import com.wegame.capi.dao.repository.domain.operation.OperationEventDetailQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class OperationEventDetailQueryBuilder {

    public static Query buildQuery(OperationEventDetailQuery detailQuery) {
        Query query = new Query(buildCriteria(detailQuery));
        if (Objects.nonNull(detailQuery.getLimit())) {
            query.limit(detailQuery.getLimit());
        }
        if (Objects.nonNull(detailQuery.getGtLastId())) {
            query.addCriteria(Criteria.where("_id").gt(detailQuery.getGtLastId()));
        }
        if (StringUtils.isNotEmpty(detailQuery.getOrderByClause())) {
            if (Objects.isNull(detailQuery.getDirection())) {
                query.with(Sort.by(Sort.Direction.DESC, detailQuery.getOrderByClause()));
            } else {
                query.with(Sort.by(detailQuery.getDirection(), detailQuery.getOrderByClause()));
            }
        }
        return query;
    }

    public static Criteria buildCriteria(OperationEventDetailQuery detailQuery) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(detailQuery.getRecordId())) {
            criteria.and("operatorRecordId").is(detailQuery.getRecordId());
        }
        if (Objects.nonNull(detailQuery.getBetStatus())) {
            criteria.and("betStatus").is(detailQuery.getBetStatus());
        }
        if (Objects.nonNull(detailQuery.getLtCreateTime())) {
            criteria.and("createTime").lt(detailQuery.getLtCreateTime());
        }
        return criteria;
    }

    public static Update buildUpdate(OperationEventDetailDo detailDo) {
        if (detailDo == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(detailDo.getBetStatus())) {
            update.set("betStatus", detailDo.getBetStatus().getCode());
        }
        if (Objects.nonNull(detailDo.getBetId())) {
            update.set("betId", detailDo.getBetId());
        }
        if (Objects.nonNull(detailDo.getUpdateTime())) {
            update.set("updateTime", detailDo.getUpdateTime());
        }
        return update;
    }
}
