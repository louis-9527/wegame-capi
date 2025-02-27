package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.common.enums.DynamicOperationEventStatusEnum;
import com.wegame.capi.common.enums.DynamicOperationReportStatusEnum;
import com.wegame.capi.dao.mapper.domain.business.BetRecordQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationDetailQuery;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class DynamicOperationQueryBuilder {


    public static Query buildQuery(DynamicOperationQuery recordQuery) {
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
        query.fields().include("_id","eventId","playerName","bonus","eventKind","gameId","channelId","effectedTime","expiredTime","eventStatus","reportStatus");

        return query;

    }


    public static Criteria buildCriteria(DynamicOperationQuery recordQuery) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(recordQuery.getGameId())) {
            criteria.and("gameId").is(recordQuery.getGameId());
        }
        if (Objects.nonNull(recordQuery.getEventStatus())) {
            criteria.and("eventStatus").is(recordQuery.getEventStatus());
        }
        if (Objects.nonNull(recordQuery.getEventStatusList())) {
            criteria.and("eventStatus").in(recordQuery.getEventStatusList());
        }
        if (Objects.nonNull(recordQuery.getReportStatus())) {
            criteria.and("reportStatus").is(recordQuery.getReportStatus());
        }

        if (Objects.nonNull(recordQuery.getCreateTimeLt())) {
            criteria.and("createTime").lt(recordQuery.getCreateTimeLt());
        }
        if (!Objects.isNull(recordQuery.getLastId())) {
            criteria.and("_id").lt(recordQuery.getLastId());
        }
        return criteria;
    }


    public static Query buildQueryBuyUserNameAndTime(DynamicOperationQuery recordQuery) {
        Criteria criteria = new Criteria();
        long currentMilli = DateUtil.getCurrentMilli();
        criteria.and("playerName").is(recordQuery.getPlayerName());
        criteria.and("operatorGameId").is(recordQuery.getOperatorGameId());
        criteria.and("eventStatus").is(recordQuery.getEventStatus());
        criteria.and("effectedTime").lte(currentMilli);
        criteria.and("expiredTime").gte(currentMilli);
        Query query = new Query(criteria);
        query.fields().include("_id","eventId","playerName","bonus","eventKind","gameId","channelId","effectedTime","expiredTime","eventStatus","reportStatus");
        //query.limit(1);
        return query;
    }


    public static Update buildUpdateById(DynamicOperationEventDo eventDo) {
        if (eventDo == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(eventDo.getEventStatus())) {
            update.set("eventStatus", eventDo.getEventStatus().getCode());
        }
        if (Objects.nonNull(eventDo.getReportStatus())) {
            update.set("reportStatus", eventDo.getReportStatus().getCode());
        }
        if (Objects.nonNull(eventDo.getUpdateTime())) {
            update.set("updateTime", eventDo.getUpdateTime());
        }

        return update;
    }
}
