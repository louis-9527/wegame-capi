package com.wegame.capi.biz.builder;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.operation.DynamicOperationEventDo;
import com.wegame.capi.dao.mapper.domain.business.DynamicOperationQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class DynamicOperationQueryBuilder {


    public static Query buildQueryBuyUserNameAndTime(DynamicOperationQuery recordQuery) {
        Criteria criteria = new Criteria();
        long currentMilli = DateUtil.getCurrentMilli();
        criteria.and("playerName").is(recordQuery.getPlayerName());
        criteria.and("operatorGameId").is(recordQuery.getOperatorGameId());
        criteria.and("eventStatus").is(recordQuery.getEventStatus());
        criteria.and("effectedTime").gte(currentMilli);
        criteria.and("expiredTime").lte(currentMilli);
        Query query = new Query(criteria);
        query.fields().include("id", "bonus", "bonusBalance", "eventKind", "eventId");
        query.limit(1);
        return query;
    }

    public static Update buildUpdateById(DynamicOperationEventDo eventDo) {
        if (eventDo == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(eventDo.getBonusBalance())) {
            update.set("bonusBalance", eventDo.getBonusBalance());
        }
        if (Objects.nonNull(eventDo.getEventStatus())) {
            update.set("eventStatus", eventDo.getEventStatus().getCode());
        }
        if (Objects.nonNull(eventDo.getUpdateTime())) {
            update.set("updateTime", eventDo.getUpdateTime());
        }

        return update;
    }
}
