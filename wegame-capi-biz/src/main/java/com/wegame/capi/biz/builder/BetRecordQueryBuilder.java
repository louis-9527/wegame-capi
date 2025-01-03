package com.wegame.capi.biz.builder;

import cn.hutool.core.collection.CollectionUtil;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Objects;

public class BetRecordQueryBuilder {


    public static Query buildMongodbQuery(BetRecordQuery recordQuery) {
        Query query = new Query(buildMongodbCriteria(recordQuery));
        if (Objects.nonNull(recordQuery.getOffset()) && Objects.nonNull(recordQuery.getLimit())) {
            query.skip(recordQuery.getOffset()).limit(recordQuery.getLimit());
        }
        query.with(Sort.by(Sort.Direction.DESC, "createTime"));

        return query;
    }

    public static Criteria buildMongodbCriteria(BetRecordQuery recordQuery) {
        Criteria criteria = new Criteria();

        if (Objects.nonNull(recordQuery.getId())) {
            criteria.and("id").is(recordQuery.getId());
        }
        if (Objects.nonNull(recordQuery.getUserId())) {
            criteria.and("userId").is(recordQuery.getUserId());
        }
        if (Objects.nonNull(recordQuery.getPlayerId())) {
            criteria.and("playerId").is(recordQuery.getPlayerId());
        }
        if (Objects.nonNull(recordQuery.getPId())) {
            criteria.and("pId").is(recordQuery.getPId());
        }
        if (Objects.nonNull(recordQuery.getChannelId())) {
            criteria.and("channelId").is(recordQuery.getChannelId());
        }

        if (Objects.nonNull(recordQuery.getGameId())) {
            criteria.and("gameId").is(recordQuery.getGameId());

            if (Objects.nonNull(recordQuery.getStartCreateTime()) && Objects.nonNull(recordQuery.getEndCreateTime())) {
                criteria.andOperator(
                        Criteria.where("createTime").gt(recordQuery.getStartCreateTime()),
                        Criteria.where("createTime").lte(recordQuery.getEndCreateTime())
                );
            }
        }
        return criteria;
    }
}
