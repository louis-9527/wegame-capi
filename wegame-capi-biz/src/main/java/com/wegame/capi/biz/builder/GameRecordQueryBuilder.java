package com.wegame.capi.biz.builder;

import com.wegame.capi.dao.reposity.domain.business.GameRecordQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Objects;

public class GameRecordQueryBuilder {

    public static Query buildMongodbQuery(GameRecordQuery recordQuery) {
        Query query = new Query(buildMongodbCriteria(recordQuery));
        if (Objects.nonNull(recordQuery.getLimit())) {
            query.limit(recordQuery.getLimit());
        }
        if (StringUtils.isNotEmpty(recordQuery.getOrderByClause())) {
            if (Objects.isNull(recordQuery.getDirection())) {
                query.with(Sort.by(Sort.Direction.DESC, recordQuery.getOrderByClause()));
            } else {
                query.with(Sort.by(recordQuery.getDirection(), recordQuery.getOrderByClause()));
            }
        }
        return query;
    }

    public static Criteria buildMongodbCriteria(GameRecordQuery recordQuery) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(recordQuery.getId())) {
            criteria.and("_id").is(recordQuery.getId());
        }
        if (Objects.nonNull(recordQuery.getParentId())) {
            criteria.and("parentId").is(recordQuery.getParentId());
        }
        if (Objects.nonNull(recordQuery.getBatchId())) {
            criteria.and("batchId").is(recordQuery.getBatchId());
        }
        if (Objects.nonNull(recordQuery.getBetRecordId())) {
            criteria.and("betRecordId").is(recordQuery.getBetRecordId());
        }
        if (Objects.nonNull(recordQuery.getPlayerId())) {
            criteria.and("playerId").is(recordQuery.getPlayerId());
        }
        if (Objects.nonNull(recordQuery.getPlayerName())) {
            criteria.and("playerName").is(recordQuery.getPlayerName());
        }
        if (Objects.nonNull(recordQuery.getIndex())) {
            criteria.and("index").is(recordQuery.getIndex());
        }
        if (Objects.nonNull(recordQuery.getFreeWheelFlag())) {
            criteria.and("freeWheelFlag").is(recordQuery.getFreeWheelFlag());
        }
        return criteria;
    }
}
