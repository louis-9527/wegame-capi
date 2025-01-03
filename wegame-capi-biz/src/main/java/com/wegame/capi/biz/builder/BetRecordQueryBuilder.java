package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.dao.mapper.domain.user.BetRecordQuery;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class BetRecordQueryBuilder {


    public static Query buildMongodbQuery(BetRecordQuery recordQuery) {
        Query query = new Query(buildMongodbCriteria(recordQuery));
        if (Objects.nonNull(recordQuery.getOffset()) && Objects.nonNull(recordQuery.getLimit())) {
            query.skip(recordQuery.getOffset()).limit(recordQuery.getLimit());
        }
        query.with(Sort.by(Sort.Direction.DESC, recordQuery.getOrderByClause()));

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
        if (Objects.nonNull(recordDo.getFreeSpinsFlag())) {
            update.set("freeSpinsFlag", recordDo.getFreeSpinsFlag());
        }
        if (Objects.nonNull(recordDo.getFreeSpinsTimes())) {
            update.set("freeSpinsTimes", recordDo.getFreeSpinsTimes());
        }
        if (Objects.nonNull(recordDo.getDetail())) {
            update.set("detail", recordDo.getDetail());
        }
        if (Objects.nonNull(recordDo.getUpdater())) {
            update.set("updater", recordDo.getUpdater());
        }
        if (Objects.nonNull(recordDo.getUpdateTime())) {
            update.set("updateTime", recordDo.getUpdateTime());
        }
        return update;
    }

}
