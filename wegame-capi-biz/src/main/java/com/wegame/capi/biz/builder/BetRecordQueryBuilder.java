package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class BetRecordQueryBuilder {

    public static Query buildMongodbQuery(BetRecordQuery recordQuery) {
        Query query = new Query(buildMongodbCriteria(recordQuery));
        if (Objects.nonNull(recordQuery.getLimit())) {
            query.limit(recordQuery.getLimit());
        }

        if (Objects.nonNull(recordQuery.getLastId())) {
            query.addCriteria(Criteria.where("_id").lt(recordQuery.getLastId()));
        }

        if (StringUtils.isNotEmpty(recordQuery.getOrderByClause())) {
            query.with(Sort.by(Sort.Direction.DESC, recordQuery.getOrderByClause()));
        }
        return query;
    }

    public static Query buildMongodbQueryForPage(BetRecordQuery recordQuery) {
        Query query = buildMongodbQuery(recordQuery);
        // 指定需要查询的字段
        Field fields = query.fields();
        fields.include("_id");
        fields.include("userId");
        fields.include("playerId");
        fields.include("playerName");
        fields.include("betAmount");
        fields.include("payAmount");
        fields.include("payOutAmount");
        fields.include("status");
        fields.include("createTime");
        return query;
    }

    public static Criteria buildMongodbCriteria(BetRecordQuery recordQuery) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(recordQuery.getId())) {
            criteria.and("_id").is(recordQuery.getId());
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
        if (Objects.nonNull(recordQuery.getStatus())) {
            criteria.and("status").is(recordQuery.getStatus());
        }
        if (Objects.nonNull(recordQuery.getGameId())) {
            criteria.and("gameId").is(recordQuery.getGameId());
        }
        if (Objects.nonNull(recordQuery.getStartCreateTime()) && Objects.nonNull(recordQuery.getEndCreateTime())) {
            criteria.andOperator(
                    Criteria.where("createTime").gt(recordQuery.getStartCreateTime()),
                    Criteria.where("createTime").lte(recordQuery.getEndCreateTime())
            );
        }

        if (Objects.isNull(recordQuery.getStartCreateTime()) && Objects.nonNull(recordQuery.getEndCreateTime())) {
            criteria.and("createTime").lte(recordQuery.getEndCreateTime());
        }
        return criteria;

    }

    public static Update buildMongoUpdate(BetRecordDo recordDo) {
        if (recordDo == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(recordDo.getPId())) {
            update.set("pId", recordDo.getPId());
        }
        if (Objects.nonNull(recordDo.getChannelId())) {
            update.set("channelId", recordDo.getChannelId());
        }
        if (Objects.nonNull(recordDo.getGameId())) {
            update.set("gameId", recordDo.getGameId());
        }
        if (Objects.nonNull(recordDo.getUserId())) {
            update.set("userId", recordDo.getUserId());
        }
        if (Objects.nonNull(recordDo.getPlayerId())) {
            update.set("playerId", recordDo.getPlayerId());
        }
        if (Objects.nonNull(recordDo.getPlayerName())) {
            update.set("playerName", recordDo.getPlayerName());
        }
        if (Objects.nonNull(recordDo.getBetType())) {
            update.set("betType", recordDo.getBetType());
        }
        if (Objects.nonNull(recordDo.getBetSubType())) {
            update.set("betSubType", recordDo.getBetSubType());
        }
        if (Objects.nonNull(recordDo.getBetId())) {
            update.set("betId", recordDo.getBetId());
        }
        if (Objects.nonNull(recordDo.getBetSerialNo())) {
            update.set("betSerialNo", recordDo.getBetSerialNo());
        }
        if (Objects.nonNull(recordDo.getBetAmount())) {
            update.set("betAmount", recordDo.getBetAmount());
        }
        if (Objects.nonNull(recordDo.getPayOutAmount())) {
            update.set("payOutAmount", recordDo.getPayOutAmount());
        }
        if (Objects.nonNull(recordDo.getMarketFlag())) {
            update.set("marketFlag", recordDo.getMarketFlag());
        }
        if (Objects.nonNull(recordDo.getStatus())) {
            update.set("status", recordDo.getStatus());
        }
        if (Objects.nonNull(recordDo.getGameRecordList())) {
            update.set("detail", recordDo.getGameRecordList());
        }
        if (Objects.nonNull(recordDo.getUpdater())) {
            update.set("updater", recordDo.getUpdater());
        }
        update.set("updateTime", System.currentTimeMillis());
        return update;
    }

}
