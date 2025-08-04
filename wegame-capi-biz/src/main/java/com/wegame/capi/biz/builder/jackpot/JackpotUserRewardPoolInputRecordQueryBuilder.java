package com.wegame.capi.biz.builder.jackpot;

import com.wegame.capi.common.enums.JackpotAccFlagEnum;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolInputRecordQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Objects;

public class JackpotUserRewardPoolInputRecordQueryBuilder {

    public static Query buildDeleteQuery(long lgCreateTime, JackpotAccFlagEnum jackpotAccFlag) {
        Criteria criteria = Criteria.where("createTime").lt(lgCreateTime)
                .and("accFlag").is(jackpotAccFlag.getCode());
        return new Query(criteria);
    }

    //    public static Update buildMongoUpdate(JackpotRewardPoolInputRecordDo recordDo) {
    //        if (recordDo == null) {
    //            throw new BusinessException(ErrorCode.ERR_INTER);
    //        }
    //        Update update = new Update();
    //        if (Objects.nonNull(recordDo.getAccFlag())) {
    //            update.set("accFlag", recordDo.getAccFlag().getCode());
    //        }
    //        if (Objects.nonNull(recordDo.getUpdater())) {
    //            update.set("updater", recordDo.getUpdater());
    //        }
    //        update.set("updateTime", System.currentTimeMillis());
    //        return update;
    //    }

    public static Query buildMongodbQuery(JackpotRewardPoolInputRecordQuery recordQuery) {
        Query query = new Query(buildMongodbCriteria(recordQuery));
        if (Objects.nonNull(recordQuery.getLimit())) {
            query.limit(recordQuery.getLimit());
        }
        if (StringUtils.isNotBlank(recordQuery.getOrderByClause())) {
            if (Objects.isNull(recordQuery.getDirection())) {
                query.with(Sort.by(Sort.Direction.DESC, recordQuery.getOrderByClause()));
            } else {
                query.with(Sort.by(recordQuery.getDirection(), recordQuery.getOrderByClause()));
            }
        }
        return query;
    }

    public static Criteria buildMongodbCriteria(JackpotRewardPoolInputRecordQuery recordQuery) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(recordQuery.getId())) {
            criteria.and("_id").is(recordQuery.getId());
        }
        if (Objects.nonNull(recordQuery.getLtLastId())) {
            criteria.and("_id").lt(recordQuery.getLtLastId());
        }
        if (Objects.nonNull(recordQuery.getGtLastId())) {
            criteria.and("_id").gt(recordQuery.getGtLastId());
        }
        if (Objects.nonNull(recordQuery.getAccFlag())) {
            criteria.and("accFlag").is(recordQuery.getAccFlag());
        }
        if (Objects.nonNull(recordQuery.getStartCreateTime()) && Objects.nonNull(recordQuery.getEndCreateTime())) {
            criteria.andOperator(
                    Criteria.where("createTime").gt(recordQuery.getStartCreateTime()),
                    Criteria.where("createTime").lte(recordQuery.getEndCreateTime())
            );
        }
        if (Objects.nonNull(recordQuery.getStartCreateTime()) && Objects.isNull(recordQuery.getEndCreateTime())) {
            criteria.and("createTime").gte(recordQuery.getStartCreateTime());
        }
        if (Objects.isNull(recordQuery.getStartCreateTime()) && Objects.nonNull(recordQuery.getEndCreateTime())) {
            criteria.and("createTime").lte(recordQuery.getEndCreateTime());
        }
        return criteria;
    }

}
