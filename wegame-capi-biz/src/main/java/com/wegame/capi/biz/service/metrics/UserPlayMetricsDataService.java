package com.wegame.capi.biz.service.metrics;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.capi.biz.builder.metrics.UserPlayMetricsQueryBuilder;
import com.wegame.capi.biz.converter.metrics.UserPlayMetricsConverter;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.common.constants.MetricsConstants;
import com.wegame.capi.dao.repository.domain.metrics.UserPlayMetricsPo;
import com.wegame.capi.dao.repository.domain.metrics.UserPlayMetricsQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
     * @param addDataMetricsDo do
     * @param metricsType      metricsType
     * @return true/false
     */
    public boolean cache(UserPlayMetricsDo addDataMetricsDo, MetricsTypeEnum metricsType) {
        String key = MetricsConstants.buildUserMetricsCacheKey(addDataMetricsDo.getPId(), addDataMetricsDo.getChannelId(), metricsType.getGameType().getId(), addDataMetricsDo.getPlayerId());
        // 先查找缓存数据，再找库数据
        UserPlayMetricsDo existMetricsDo = this.query(addDataMetricsDo.getPId(), addDataMetricsDo.getPlayerId(), addDataMetricsDo.getChannelId(), metricsType);
        if (Objects.nonNull(existMetricsDo)) {
            // 修改数据
            existMetricsDo.injectNewData(addDataMetricsDo);
            // 每次修改都续约时间
            redisTemplate.opsForValue().set(key, existMetricsDo, MetricsConstants.USER_METRICS_CACHE_TIME, TimeUnit.SECONDS);
        } else {
            // 没有，则新建一条数据
            redisTemplate.opsForValue().set(key, addDataMetricsDo, MetricsConstants.USER_METRICS_CACHE_TIME, TimeUnit.SECONDS);
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
        UserPlayMetricsDo userPlayMetricsDo = this.queryCache(pId, playerId, channelId, metricsType);
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
                return result.getModifiedCount() >= 1;
            } else {
                log.warn("UserPlayMetricsService#flush update matched count 0,insert a new data.data:{}", JSON.toJSONString(userPlayMetricsDo));
                // 如果没有数据，则插入数据
                UserPlayMetricsPo po = UserPlayMetricsConverter.doToPo(userPlayMetricsDo);
                po.setId(snowflakeIdGenerator.nextId());
                super.getMongoTemplate(metricsType.getGameType()).insert(po, metricsType.getUserPlayMetricsCollectionName());
            }
        }
        return true;
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
