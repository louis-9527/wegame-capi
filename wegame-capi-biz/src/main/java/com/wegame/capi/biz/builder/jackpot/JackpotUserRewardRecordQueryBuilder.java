package com.wegame.capi.biz.builder.jackpot;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordPo;
import com.wegame.capi.dao.repository.domain.jackpot.JackpotUserRewardRecordQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

public class JackpotUserRewardRecordQueryBuilder {

    public static Query buildMongodbQuery(JackpotUserRewardRecordQuery recordQuery) {
        Query query = new Query(buildMongodbCriteria(recordQuery));
        if (Objects.nonNull(recordQuery.getLimit())) {
            query.limit(recordQuery.getLimit());
        }
        if (Objects.nonNull(recordQuery.getLtLastId())) {
            query.addCriteria(Criteria.where("_id").lt(recordQuery.getLtLastId()));
        }

        if (Objects.nonNull(recordQuery.getGtLastId())) {
            query.addCriteria(Criteria.where("_id").gt(recordQuery.getGtLastId()));
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

    public static Query buildHighestRecordQuery() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "rewardAmount"));
        query.limit(1);
        return query;
    }

    public static Criteria buildMongodbCriteria(JackpotUserRewardRecordQuery recordQuery) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(recordQuery.getId())) {
            criteria.and("_id").is(recordQuery.getId());
        }
        if (Objects.nonNull(recordQuery.getUserId())) {
            criteria.and("userId").is(recordQuery.getUserId());
        }
        if (Objects.nonNull(recordQuery.getPlayerName())) {
            criteria.and("playerName").is(recordQuery.getPlayerName());
        }
        if (Objects.nonNull(recordQuery.getBetRecordId())) {
            criteria.and("betRecordId").is(recordQuery.getBetRecordId());
        }
        if (Objects.nonNull(recordQuery.getGameRecordId())) {
            criteria.and("gameRecordId").is(recordQuery.getGameRecordId());
        }
        if (Objects.nonNull(recordQuery.getBetId())) {
            criteria.and("betId").is(recordQuery.getBetId());
        }
        if (Objects.nonNull(recordQuery.getRewardPoolId())) {
            criteria.and("rewardPoolId").is(recordQuery.getRewardPoolId());
        }
        if (Objects.nonNull(recordQuery.getRewardPoolType())) {
            criteria.and("rewardPoolType").is(recordQuery.getRewardPoolType());
        }
        if (!CollectionUtils.isEmpty(recordQuery.getRewardPoolTypeList())) {
            criteria.and("rewardPoolType").in(recordQuery.getRewardPoolTypeList());
        }
        if (Objects.nonNull(recordQuery.getRewardAmount())) {
            criteria.and("rewardAmount").is(recordQuery.getRewardAmount());
        }
        if (Objects.nonNull(recordQuery.getRewardGrantSerialNo())) {
            criteria.and("rewardGrantSerialNo").is(recordQuery.getRewardGrantSerialNo());
        }
        if (Objects.nonNull(recordQuery.getRewardGrantStatus())) {
            criteria.and("rewardGrantStatus").is(recordQuery.getRewardGrantStatus());
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

    public static Update buildUpdate(JackpotUserRewardRecordPo recordPo) {
        if (recordPo == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(recordPo.getRewardGrantStatus())) {
            update.inc("rewardGrantStatus", recordPo.getRewardGrantStatus());
        }
        if (Objects.nonNull(recordPo.getUpdater())) {
            update.set("updater", recordPo.getUpdater());
        }
        if (Objects.nonNull(recordPo.getUpdateTime())) {
            update.set("updateTime", recordPo.getUpdateTime());
        } else {
            update.set("updateTime", DateUtil.getCurrentMilli());
        }
        return update;
    }


}
