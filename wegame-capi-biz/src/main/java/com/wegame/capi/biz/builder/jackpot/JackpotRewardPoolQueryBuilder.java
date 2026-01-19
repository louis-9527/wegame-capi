package com.wegame.capi.biz.builder.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotRewardPoolQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

public class JackpotRewardPoolQueryBuilder {

    public static Query buildListAllQuery() {
        Query query = new Query();
        query.limit(10);
        return query;
    }

    public static Query buildMongodbQuery(JackpotRewardPoolQuery recordQuery) {
        Query query = new Query(buildMongodbCriteria(recordQuery));
        if (Objects.nonNull(recordQuery.getLimit())) {
            query.limit(recordQuery.getLimit());
        }
        return query;
    }

    public static Criteria buildMongodbCriteria(JackpotRewardPoolQuery query) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(query.getId())) {
            criteria.and("_id").is(query.getId());
        }
        if (Objects.nonNull(query.getType())) {
            criteria.and("type").is(query.getType());
        }
        if (Objects.nonNull(query.getStartCreateTime()) && Objects.nonNull(query.getEndCreateTime())) {
            criteria.andOperator(
                    Criteria.where("createTime").gt(query.getStartCreateTime()),
                    Criteria.where("createTime").lte(query.getEndCreateTime())
            );
        }
        if (Objects.isNull(query.getStartCreateTime()) && Objects.nonNull(query.getEndCreateTime())) {
            criteria.and("createTime").lte(query.getEndCreateTime());
        }
        return criteria;
    }

    public static Update buildAddAmountUpdate(JackpotRewardPoolPo rewardPool) {
        if (rewardPool == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(rewardPool.getAmount())) {
            update.inc("amount", rewardPool.getAmount());
        }
        if (Objects.nonNull(rewardPool.getReservedAmount())) {
            update.inc("reservedAmount", rewardPool.getReservedAmount());
        }
        if (Objects.nonNull(rewardPool.getUpdater())) {
            update.set("updater", rewardPool.getUpdater());
        }
        if (Objects.nonNull(rewardPool.getUpdateTime())) {
            update.set("updateTime", rewardPool.getUpdateTime());
        } else {
            update.set("updateTime", DateUtil.getCurrentMilli());
        }
        return update;
    }
}
