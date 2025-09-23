package com.wegame.capi.biz.service.metrics;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.result.UpdateResult;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.id.SnowflakeIdGenerator;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.builder.metrics.GameMetricsBuilder;
import com.wegame.capi.biz.builder.metrics.GameMetricsQueryBuilder;
import com.wegame.capi.biz.converter.metrics.GameMetricsConverter;
import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.MetricsCacheKeyDo;
import com.wegame.capi.biz.innerdos.config.GameRtpDosCondition;
import com.wegame.capi.biz.innerdos.initializer.InnerDosContextInitializerManager;
import com.wegame.capi.biz.service.AbstractService;
import com.wegame.capi.client.enums.innerdos.GameInnerDosTypeEnum;
import com.wegame.capi.client.enums.innerdos.GameMetricsPropertyNameEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.common.constants.MetricsConstants;
import com.wegame.capi.dao.repository.domain.metrics.GameMetricsPo;
import com.wegame.capi.dao.repository.domain.metrics.GameMetricsQuery;
import com.wegame.capi.dao.repository.domain.metrics.UserPlayMetricsPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 大盘指标服务
 */
@Slf4j
@Service
public class GameMetricsDataService extends AbstractService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    private InnerDosContextInitializerManager innerDosContextInitializerManager;

    /**
     * 指标数据缓存存储
     *
     * @param metricsType metricsType
     * @return true/false
     */
    public boolean cache(Map<GameMetricsPropertyNameEnum, Object> metricsDataMap, MetricsCacheKeyDo cacheKeyDo, MetricsTypeEnum metricsType) {
        String key = MetricsConstants.buildGameMetricsCacheKey(cacheKeyDo.getPId(), cacheKeyDo.getChannelId(), metricsType.getGameType().getId());
        // 先查找缓存数据，再找库数据
        GameMetricsDo existMetricsDo = this.query(cacheKeyDo.getPId(), cacheKeyDo.getChannelId(), metricsType);
        // 先cache 投注和派奖的金额:趋势性数据，这块先不处理
//        this.cacheBetAndPayoutAmount(metricsDataMap, existMetricsDo,cacheKeyDo, metricsType);
        Map<GameInnerDosTypeEnum, GameRtpDosCondition> gameRtpDosConditionMap
                = innerDosContextInitializerManager.getInitializer(metricsType.getGameType()).getGameRtpDosConditionMap();
        if (Objects.nonNull(existMetricsDo)) {
            // 修改数据
            existMetricsDo.injectNewData(metricsDataMap, gameRtpDosConditionMap);
            // 每次修改都续约时间
            redisTemplate.opsForValue().set(key, existMetricsDo, MetricsConstants.GAME_METRICS_CACHE_TIME, TimeUnit.SECONDS);
        } else {
            // 没有，则新建一条数据
            existMetricsDo = GameMetricsBuilder.build(metricsDataMap, cacheKeyDo, gameRtpDosConditionMap, metricsType);
            redisTemplate.opsForValue().set(key,
                    existMetricsDo,
                    MetricsConstants.GAME_METRICS_CACHE_TIME, TimeUnit.SECONDS);
        }
        log.info("GameMetricsDataService#cache,cache data:{}", JSON.toJSONString(existMetricsDo));
        return true;
    }

    private void cacheBetAndPayoutAmount(Map<GameMetricsPropertyNameEnum, Object> metricsDataMap, GameMetricsDo existMetricsDo, MetricsCacheKeyDo cacheKeyDo, MetricsTypeEnum metricsType) {
        Object totalBetAmountObj = metricsDataMap.get(GameMetricsPropertyNameEnum.totalBetAmount);
        Object totalPayoutAmountObj = metricsDataMap.get(GameMetricsPropertyNameEnum.totalPayoutAmount);
        // bet amount
        // 1.key是否存在(评估必要性)
        String betAmountCacheKey = MetricsConstants.buildGameBetAmountCacheKey(cacheKeyDo.getPId(), cacheKeyDo.getChannelId(), metricsType.getGameType().getId());
        Boolean hasBetAmountCacheKey = redisTemplate.hasKey(betAmountCacheKey);

        Long incBetAmount = Optional.ofNullable(totalBetAmountObj).map(t -> (long) t).orElse(0L);
        // 2.不存在 && 已有do不为空:获取现有的值作为基础
        if (!Objects.equals(hasBetAmountCacheKey, true) && Objects.nonNull(existMetricsDo)) {
            incBetAmount = existMetricsDo.getTotalBetAmount();
        }
        redisTemplate.opsForValue().increment(betAmountCacheKey, incBetAmount);

        // payout amount
        String payoutAmountCacheKey = MetricsConstants.buildGamePayoutAmountCacheKey(cacheKeyDo.getPId(), cacheKeyDo.getChannelId(), metricsType.getGameType().getId());
        Boolean hasPayoutAmountCacheKey = redisTemplate.hasKey(payoutAmountCacheKey);
        Long incPayoutAmount = Optional.ofNullable(totalPayoutAmountObj).map(t -> (long) t).orElse(0L);
        if (!Objects.equals(hasPayoutAmountCacheKey, true) && Objects.nonNull(existMetricsDo)) {
            incPayoutAmount = existMetricsDo.getTotalPayoutAmount();
        }
        redisTemplate.opsForValue().increment(payoutAmountCacheKey, incPayoutAmount);
    }

    /**
     * 指标数据刷盘
     *
     * @param pId         pid
     * @param channelId   channelId
     * @param metricsType metricsType
     * @return 刷盘结果
     */
    public boolean flush(String pId, String channelId, MetricsTypeEnum metricsType) {
        log.info("GameMetricsDataService#flush start,pId:{},channelId:{},metricsType:{}", pId, channelId, metricsType);
        AssertUtil.assertStrNotEmpty(pId, ErrorCode.ERR_PARAM, "pId is empty");
        AssertUtil.assertStrNotEmpty(channelId, ErrorCode.ERR_PARAM, "channelId is empty");
        AssertUtil.assertNotNull(metricsType, ErrorCode.ERR_PARAM, "metricsType is null");
        GameMetricsDo metricsDo = this.queryCache(pId, channelId, metricsType);
        boolean resultFlag = true;
        // 缓存没有则不处理
        if (Objects.nonNull(metricsDo)) {
            GameMetricsQuery metricsQuery = GameMetricsQuery.builder()
                    .pId(pId)
                    .channelId(channelId)
                    .gameId(metricsType.getGameType().getId())
                    .build();
            UpdateResult result = super.getMongoTemplate(metricsType.getGameType()).updateFirst(GameMetricsQueryBuilder.buildMongodbQuery(metricsQuery), GameMetricsQueryBuilder.buildMongoUpdate(metricsDo), UserPlayMetricsPo.class,
                    metricsType.getGameMetricsCollectionName());
            if (result.getMatchedCount() > 0) {
                resultFlag = result.getModifiedCount() >= 1;
            } else {
                log.warn("GameMetricsService#flush update matched count 0,insert a new data.data:{}", JSON.toJSONString(metricsDo));
                // 如果没有数据，则插入数据
                GameMetricsPo po = GameMetricsConverter.doToPo(metricsDo);
                po.setId(snowflakeIdGenerator.nextId());
                super.getMongoTemplate(metricsType.getGameType()).insert(po, metricsType.getGameMetricsCollectionName());
            }
        }
        log.info("GameMetricsDataService#flush end,pId:{},channelId:{},metricsType:{},flushData:{}", pId, channelId, metricsType, JSON.toJSONString(metricsDo));
        return resultFlag;
    }

    /**
     * 查询指标
     * 指标数据查询.缓存优先
     *
     * @param pId         pid
     * @param channelId   channelId
     * @param metricsType metricsType
     * @return do
     */
    public GameMetricsDo query(String pId, String channelId, MetricsTypeEnum metricsType) {
        GameMetricsDo metricsDo = this.queryCache(pId, channelId, metricsType);
        if (Objects.nonNull(metricsDo)) {
            return metricsDo;
        } else {
            GameMetricsQuery metricsQuery = GameMetricsQuery.builder()
                    .pId(pId)
                    .channelId(channelId)
                    .gameId(metricsType.getGameType().getId())
                    .build();
            GameMetricsPo po = super.getMongoTemplate(metricsType.getGameType()).findOne(GameMetricsQueryBuilder.buildMongodbQuery(metricsQuery)
                    , GameMetricsPo.class, metricsType.getGameMetricsCollectionName());
            return GameMetricsConverter.poToDo(po);
        }
    }

    /**
     * 查询缓存
     *
     * @param pId         pid
     * @param channelId   channelId
     * @param metricsType metricsType
     * @return do
     */
    private GameMetricsDo queryCache(String pId, String channelId, MetricsTypeEnum metricsType) {
        return (GameMetricsDo) redisTemplate.opsForValue().get(MetricsConstants.buildGameMetricsCacheKey(pId, channelId, metricsType.getGameType().getId()));
    }


}
