package com.wegame.capi.biz.service.metrics;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.metrics.UserPlayMetricsBuilder;
import com.wegame.capi.biz.builder.metrics.UserPlayMetricsQueryBuilder;
import com.wegame.capi.biz.converter.metrics.UserPlayMetricsConverter;
import com.wegame.capi.biz.domain.metrics.MetricsCacheKeyDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.client.enums.innerdos.UserPlayMetricsPropertyNameEnum;
import com.wegame.capi.common.constants.MetricsConstants;
import com.wegame.capi.dao.repository.domain.metrics.UserPlayMetricsPo;
import com.wegame.capi.dao.repository.domain.metrics.UserPlayMetricsQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 用户指标服务
 */
@Slf4j
@Service
public class UserPlayMetricsDataService extends AbstractService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    /**
     * 指标数据缓存存储
     *
     * @param metricsDataMap 指标数据map
     * @param metricsType    metricsType
     * @return true/false
     */
    public synchronized boolean cache(Map<UserPlayMetricsPropertyNameEnum, Object> metricsDataMap, MetricsCacheKeyDo cacheKeyDo, MetricsTypeEnum metricsType) {
        String key = MetricsConstants.buildUserMetricsCacheKey(cacheKeyDo.getPId(), cacheKeyDo.getChannelId(), metricsType.getGameType().getId(), cacheKeyDo.getPlayerId());
        // 先查找缓存数据，再找库数据
        UserPlayMetricsDo existMetricsDo = this.query(cacheKeyDo.getPId(), cacheKeyDo.getPlayerId(), cacheKeyDo.getChannelId(), metricsType);
        if (Objects.nonNull(existMetricsDo)) {
            // 修改数据
            existMetricsDo.injectNewData(metricsDataMap);
            // 每次修改都续约时间
            redisTemplate.opsForValue().set(key, existMetricsDo, MetricsConstants.USER_METRICS_CACHE_TIME, TimeUnit.SECONDS);
        } else {
            // 没有，则新建一条数据
            redisTemplate.opsForValue().set(key, UserPlayMetricsBuilder.build(metricsDataMap, cacheKeyDo, metricsType),
                    MetricsConstants.USER_METRICS_CACHE_TIME, TimeUnit.SECONDS);
        }
        return true;
    }

    /**
     * 指标数据刷盘
     *
     * @param pId         pid
     * @param playerId    playerId
     * @param channelId   channelId
     * @param metricsType metricsType
     * @return 刷盘结果
     */
    public boolean flush(String pId, String playerId, String channelId, MetricsTypeEnum metricsType) {
        log.info("UserPlayMetricsDataService#flush start,pId:{},channelId:{},playerId:{},metricsType:{}", pId, channelId, playerId, metricsType);
        AssertUtil.assertStrNotEmpty(pId, ErrorCode.ERR_PARAM, "pId is empty");
        AssertUtil.assertStrNotEmpty(channelId, ErrorCode.ERR_PARAM, "channelId is empty");
        AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM, "playerId is empty");
        AssertUtil.assertNotNull(metricsType, ErrorCode.ERR_PARAM, "metricsType is null");
        UserPlayMetricsDo userPlayMetricsDo = this.queryCache(pId, playerId, channelId, metricsType);
        boolean resultFlag = true;
        // 缓存没有则不处理
        if (Objects.nonNull(userPlayMetricsDo)) {
            UserPlayMetricsQuery metricsQuery = UserPlayMetricsQuery.builder()
                    .pId(pId)
                    .playerId(playerId)
                    .channelId(channelId)
                    .gameId(metricsType.getGameType().getId())
                    .build();
            UpdateResult result = super.getMongoTemplate(metricsType.getGameType()).updateFirst(UserPlayMetricsQueryBuilder.buildMongodbQuery(metricsQuery), UserPlayMetricsQueryBuilder.buildMongoUpdate(userPlayMetricsDo), UserPlayMetricsPo.class,
                    metricsType.getUserPlayMetricsCollectionName());
            if (result.getMatchedCount() > 0) {
                resultFlag = result.getModifiedCount() >= 1;
            } else {
                log.warn("UserPlayMetricsService#flush update matched count 0,insert a new data.data:{}", JSON.toJSONString(userPlayMetricsDo));
                // 如果没有数据，则插入数据
                UserPlayMetricsPo po = UserPlayMetricsConverter.doToPo(userPlayMetricsDo);
                po.setId(snowflakeIdGenerator.nextId());
                super.getMongoTemplate(metricsType.getGameType()).insert(po, metricsType.getUserPlayMetricsCollectionName());
            }
        }
        log.info("UserPlayMetricsDataService#flush end,pId:{},channelId:{},playerId:{},metricsType:{},flushData:{}", pId, channelId, playerId, metricsType, JSON.toJSONString(userPlayMetricsDo));
        return resultFlag;
    }

    /**
     * 查询指标
     * 指标数据查询.缓存优先
     *
     * @param pId         pid
     * @param playerId    playerId
     * @param channelId   channelId
     * @param metricsType metricsType
     * @return do
     */
    public UserPlayMetricsDo query(String pId, String playerId, String channelId, MetricsTypeEnum metricsType) {
        UserPlayMetricsDo userPlayMetricsDo = this.queryCache(pId, playerId, channelId, metricsType);
        if (Objects.nonNull(userPlayMetricsDo)) {
            return userPlayMetricsDo;
        } else {
            UserPlayMetricsQuery metricsQuery = UserPlayMetricsQuery.builder()
                    .pId(pId)
                    .playerId(playerId)
                    .channelId(channelId)
                    .gameId(metricsType.getGameType().getId())
                    .build();
            UserPlayMetricsPo po = super.getMongoTemplate(metricsType.getGameType()).findOne(UserPlayMetricsQueryBuilder.buildMongodbQuery(metricsQuery), UserPlayMetricsPo.class, metricsType.getUserPlayMetricsCollectionName());
            return UserPlayMetricsConverter.poToDo(po);
        }
    }

    /**
     * 查询缓存
     *
     * @param pId         pid
     * @param playerId    playerId
     * @param channelId   channelId
     * @param metricsType metricsType
     * @return do
     */
    private UserPlayMetricsDo queryCache(String pId, String playerId, String channelId, MetricsTypeEnum metricsType) {
        return (UserPlayMetricsDo) redisTemplate.opsForValue().get(MetricsConstants.buildUserMetricsCacheKey(pId, channelId, metricsType.getGameType().getId(), playerId));
    }
}
