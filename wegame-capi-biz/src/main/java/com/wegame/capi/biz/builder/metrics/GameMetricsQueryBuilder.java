package com.wegame.capi.biz.builder.metrics;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.dao.repository.domain.business.BetRecordQuery;
import com.wegame.capi.dao.repository.domain.metrics.GameMetricsQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class GameMetricsQueryBuilder {

    public static Query buildMongodbQuery(GameMetricsQuery metricsQuery) {
        Query query = new Query(buildMongodbCriteria(metricsQuery));
        if (Objects.nonNull(metricsQuery.getLimit())) {
            query.limit(metricsQuery.getLimit());
        }
        if (Objects.nonNull(metricsQuery.getLtLastId())) {
            query.addCriteria(Criteria.where("_id").lt(metricsQuery.getLtLastId()));
        }
        if (Objects.nonNull(metricsQuery.getGtLastId())) {
            query.addCriteria(Criteria.where("_id").gt(metricsQuery.getGtLastId()));
        }
        if (StringUtils.isNotBlank(metricsQuery.getOrderByClause())) {
            if (Objects.isNull(metricsQuery.getDirection())) {
                query.with(Sort.by(Sort.Direction.DESC, metricsQuery.getOrderByClause()));
            } else {
                query.with(Sort.by(metricsQuery.getDirection(), metricsQuery.getOrderByClause()));
            }
        }
        return query;
    }

    public static Criteria buildMongodbCriteria(GameMetricsQuery metricsQuery) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(metricsQuery.getId())) {
            criteria.and("_id").is(metricsQuery.getId());
        }
        if (Objects.nonNull(metricsQuery.getPId())) {
            criteria.and("pId").is(metricsQuery.getPId());
        }
        if (Objects.nonNull(metricsQuery.getChannelId())) {
            criteria.and("channelId").is(metricsQuery.getChannelId());
        }
        if (Objects.nonNull(metricsQuery.getGameId())) {
            criteria.and("gameId").is(metricsQuery.getGameId());
        }
        // currentRtp
        if (Objects.nonNull(metricsQuery.getCurrentRtp())) {
            criteria.and("currentRtp").is(metricsQuery.getCurrentRtp());
        }
        // totalPayoutAmount
        if (Objects.nonNull(metricsQuery.getTotalPayoutAmount())) {
            criteria.and("totalPayoutAmount").is(metricsQuery.getTotalPayoutAmount());
        }
        // totalBetAmount
        if (Objects.nonNull(metricsQuery.getTotalBetAmount())) {
            criteria.and("totalBetAmount").is(metricsQuery.getTotalBetAmount());
        }
        // version
        if (Objects.nonNull(metricsQuery.getVersion())) {
            criteria.and("version").is(metricsQuery.getVersion());
        }
        if (Objects.nonNull(metricsQuery.getStartCreateTime()) && Objects.nonNull(metricsQuery.getEndCreateTime())) {
            criteria.andOperator(
                    Criteria.where("createTime").gt(metricsQuery.getStartCreateTime()),
                    Criteria.where("createTime").lte(metricsQuery.getEndCreateTime())
            );
        }
        if (Objects.isNull(metricsQuery.getStartCreateTime()) && Objects.nonNull(metricsQuery.getEndCreateTime())) {
            criteria.and("createTime").lte(metricsQuery.getEndCreateTime());
        }
        return criteria;
    }

    /**
     * 其他信息不允许修改
     *
     * @param metricsDo metricsDo
     * @return update
     */
    public static Update buildMongoUpdate(GameMetricsDo metricsDo) {
        if (metricsDo == null) {
            throw new BusinessException(ErrorCode.ERR_INTER);
        }
        Update update = new Update();
        if (Objects.nonNull(metricsDo.getCurrentRtp())) {
            update.set("currentRtp", metricsDo.getCurrentRtp());
        }
        if (Objects.nonNull(metricsDo.getTotalBetAmount())) {
            update.set("totalBetAmount", metricsDo.getTotalBetAmount());
        }
        if (Objects.nonNull(metricsDo.getTotalPayoutAmount())) {
            update.set("totalPayoutAmount", metricsDo.getTotalPayoutAmount());
        }
        if (Objects.nonNull(metricsDo.getExtend())) {
            update.set("extend", metricsDo.getExtend());
        }
        if (Objects.nonNull(metricsDo.getVersion())) {
            update.set("version", metricsDo.getVersion());
        }
        if (Objects.nonNull(metricsDo.getUpdater())) {
            update.set("updater", metricsDo.getUpdater());
        }
        update.set("updateTime", System.currentTimeMillis());
        return update;
    }

}
