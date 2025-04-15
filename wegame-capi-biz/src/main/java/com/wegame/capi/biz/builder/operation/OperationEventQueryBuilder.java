package com.wegame.capi.biz.builder.operation;

import com.google.common.collect.Lists;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.OperationEventDo;
import com.wegame.capi.common.enums.OperationEventStatusEnum;
import com.wegame.capi.dao.repository.domain.operation.OperationEventQuery;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.Objects;


public class OperationEventQueryBuilder {

    public static Query buildQuery(OperationEventQuery recordQuery) {
        Query query = new Query(buildCriteria(recordQuery));
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
        query.fields().include("_id", "eventId", "playerName", "minBonus", "maxBonus", "finishBonus", "bonus", "eventKind", "gameId", "channelId", "effectedTime", "expiredTime", "eventStatus", "eventSubStatus", "reportStatus");
        return query;
    }

    public static Criteria buildCriteria(OperationEventQuery recordQuery) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(recordQuery.getGameId())) {
            criteria.and("gameId").is(recordQuery.getGameId());
        }
        if (Objects.nonNull(recordQuery.getEventIdList())) {
            criteria.and("eventId").in(recordQuery.getEventIdList());
        }
        if (Objects.nonNull(recordQuery.getEventStatus())) {
            criteria.and("eventStatus").is(recordQuery.getEventStatus());
        }
        if (Objects.nonNull(recordQuery.getReportStatus())) {
            criteria.and("reportStatus").is(recordQuery.getReportStatus());
        }
        if (Objects.nonNull(recordQuery.getExpiredTimeLt())) {
            criteria.and("expiredTime").lt(recordQuery.getExpiredTimeLt());
        }
        if (Objects.nonNull(recordQuery.getCreateTimeLt())) {
            criteria.and("createTime").lt(recordQuery.getCreateTimeLt());
        }
        if (Objects.nonNull(recordQuery.getLtLastId())) {
            criteria.and("_id").lt(recordQuery.getLtLastId());
        }
        return criteria;
    }


    public static Query buildQueryEffectList(String playName, String gameId) {
        Criteria criteria = new Criteria();
        long currentMilli = DateUtil.getCurrentMilli();
        criteria.and("playerName").is(playName);
        criteria.and("gameId").is(gameId);
        criteria.and("eventStatus").in(Lists.newArrayList(OperationEventStatusEnum.NONE.getCode(), OperationEventStatusEnum.ING.getCode()));
        criteria.and("effectedTime").lte(currentMilli);
        criteria.and("expiredTime").gte(currentMilli);
        criteria.and("$expr").is(
                new Document("lt", Arrays.asList("$finishBonus", "$minBonus"))
        );
        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.ASC, "effectedTime"));
        query.limit(10);
        query.fields().include("_id", "gameId", "effectedTime", "expiredTime", "eventStatus");
        return query;
    }

    public static Query buildQueryOne(Long id) {
        Criteria criteria = new Criteria();
        criteria.and("_id").is(id);
        Query query = new Query(criteria);
        query.fields().include("_id", "eventId", "playerName", "bonus", "minBonus", "finishBonus", "betType", "maxBonus", "eventKind", "gameId", "channelId", "effectedTime", "expiredTime", "eventStatus", "eventSubStatus", "reportStatus");
        return query;
    }


    public static Update buildUpdateById(OperationEventDo eventDo) {
        if (eventDo == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(eventDo.getEventStatus())) {
            update.set("eventStatus", eventDo.getEventStatus().getCode());
        }
        if (Objects.nonNull(eventDo.getEventSubStatus())) {
            update.set("eventSubStatus", eventDo.getEventSubStatus().getCode());
        }
        if (Objects.nonNull(eventDo.getReportStatus())) {
            update.set("reportStatus", eventDo.getReportStatus().getCode());
        }
        if (Objects.nonNull(eventDo.getUpdateTime())) {
            update.set("updateTime", eventDo.getUpdateTime());
        }
        if (Objects.nonNull(eventDo.getFinishBonus())) {
            update.set("finishBonus", eventDo.getFinishBonus());
        }
        if (Objects.nonNull(eventDo.getReportFailMsg())) {
            update.set("reportFailMsg", eventDo.getReportFailMsg());
        }

        return update;
    }
}
