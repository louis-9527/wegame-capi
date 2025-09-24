package com.wegame.capi.biz.service.metrics;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
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
import com.wegame.capi.common.utils.RtpUtil;
import com.wegame.capi.dao.repository.domain.metrics.GameMetricsPo;
import com.wegame.capi.dao.repository.domain.metrics.GameMetricsQuery;
import com.wegame.capi.dao.repository.domain.metrics.UserPlayMetricsPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 大盘指标服务
 */
@Slf4j
@Service
public class GameMetricsDataLuaService extends GameMetricsDataService {
    private static final String LUA_INCREMENT_SCRIPT =
            "local hashKey = KEYS[1]\n" +
                    "local betAmountIncrement = tonumber(ARGV[1])\n" +
                    "local payoutAmountIncrement = tonumber(ARGV[2])\n" +
                    "local newBetAmount = redis.call('HINCRBY', hashKey, 'totalBetAmount', betAmountIncrement)\n" +
                    "local newPayoutAmount = redis.call('HINCRBY', hashKey, 'totalPayoutAmount', payoutAmountIncrement)\n" +
                    "return {newBetAmount, newPayoutAmount}";

    private static final String LUA_GET_SCRIPT =
            "local hashKey = KEYS[1]\n" +
                    "local values = redis.call('HMGET', hashKey, 'totalBetAmount', 'totalPayoutAmount')\n" +
                    "local betAmount = values[1] and tonumber(values[1]) or 0\n" +
                    "local payoutAmount = values[2] and tonumber(values[2]) or 0\n" +
                    "return {betAmount, payoutAmount}";

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
        String hashKey = MetricsConstants.buildGameMetricsCacheKey(cacheKeyDo.getPId(), cacheKeyDo.getChannelId(), metricsType.getGameType().getId());
        Object totalBetAmountIncObj = metricsDataMap.get(GameMetricsPropertyNameEnum.totalBetAmount);
        Object totalPayoutAmountIncObj = metricsDataMap.get(GameMetricsPropertyNameEnum.totalPayoutAmount);
        Long totalBetAmountInc = (Long) totalBetAmountIncObj;
        Long totalPayoutAmountInc = (Long) totalPayoutAmountIncObj;

        // 构建lua脚本
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptText(LUA_INCREMENT_SCRIPT);
        script.setResultType(List.class);
        List<String> keys = Lists.newArrayList(hashKey);
        List<Long> args = Lists.newArrayList(totalBetAmountInc, totalPayoutAmountInc);
        List executeResult = redisTemplate.execute(script, keys, args.toArray());

        Object totalBetAmountObj = executeResult.get(0);
        Object totalPayoutAmountObj = executeResult.get(1);
        Long totalBetAmount = (Long) totalBetAmountObj;
        Long totalPayoutAmount = (Long) totalPayoutAmountObj;
        BigDecimal currentRtp = RtpUtil.calculateRtp(totalBetAmount, totalPayoutAmount);
        // 常态干预类型处理
        String inUseDosTypeCacheKey = MetricsConstants.buildGameInUseDosTypeCacheKey(cacheKeyDo.getPId(), cacheKeyDo.getChannelId(), metricsType.getGameType().getId());
        String inUseDosType = (String) redisTemplate.opsForValue().get(inUseDosTypeCacheKey);
        GameInnerDosTypeEnum gameInnerDosTypeEnum = this.injectInUseNormalDosType(inUseDosType, currentRtp, metricsType);
        // 如果和当前的不一样则设置值
        if (!Objects.equals(inUseDosType, gameInnerDosTypeEnum.getCode())) {
            redisTemplate.opsForValue().set(inUseDosTypeCacheKey, gameInnerDosTypeEnum.getCode(), MetricsConstants.USER_METRICS_CACHE_TIME, TimeUnit.SECONDS);
        }
        return true;
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
        String hashKey = MetricsConstants.buildGameMetricsCacheKey(pId, channelId, metricsType.getGameType().getId());
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptText(LUA_GET_SCRIPT);
        script.setResultType(List.class);

        List<String> keys = Lists.newArrayList(hashKey);
        List execResult = redisTemplate.execute(script, keys);
        Object totalBetAmountObj = execResult.get(0);
        Object totalPayoutAmountObj = execResult.get(1);
        Long totalBetAmount = (Long) totalBetAmountObj;
        Long totalPayoutAmount = (Long) totalPayoutAmountObj;
        String inUseDosTypeCacheKey = MetricsConstants.buildGameInUseDosTypeCacheKey(pId, channelId, metricsType.getGameType().getId());
        String inUseDosType = (String) redisTemplate.opsForValue().get(inUseDosTypeCacheKey);

        return GameMetricsDo.builder()
                .totalBetAmount(totalBetAmount)
                .totalPayoutAmount(totalPayoutAmount)
                .currentRtp(RtpUtil.calculateRtp(totalBetAmount, totalPayoutAmount))
                .inUseNormalDosType(inUseDosType)
                .pId(pId)
                .channelId(channelId)
                .gameId(metricsType.getGameType().getId())
                .build();
    }

    private GameInnerDosTypeEnum injectInUseNormalDosType(String inUseNormalDosType, BigDecimal currentRtp, MetricsTypeEnum metricsType) {
        GameInnerDosTypeEnum gameInnerDosTypeEnum = GameInnerDosTypeEnum.valueOfCode(inUseNormalDosType);
        Map<GameInnerDosTypeEnum, GameRtpDosCondition> gameRtpDosConditionMap
                = innerDosContextInitializerManager.getInitializer(metricsType.getGameType()).getGameRtpDosConditionMap();
        if (Objects.isNull(gameInnerDosTypeEnum)) {
            // 默认走normal
            return GameInnerDosTypeEnum.NORMAL_DOS;
        } else {
            GameRtpDosCondition gameRtpDosCondition = gameRtpDosConditionMap.get(gameInnerDosTypeEnum);
            // 为空校验一下，因为normal 一般来说是不配置的
            if (Objects.nonNull(gameRtpDosCondition)) {
                // 先校验是否满足退出条件
                if (!gameRtpDosCondition.getOut().assertIt(currentRtp)) {
                    // 没有退出当前条件，则返回
                    return gameInnerDosTypeEnum;
                }
            }

            // 单个匹配是否满足对应干预
            // 特殊干预
            GameRtpDosCondition specialCondition = gameRtpDosConditionMap.get(GameInnerDosTypeEnum.SPECIAL_DOS);
            if (Objects.nonNull(specialCondition) && specialCondition.getIn().assertIt(currentRtp)) {
                return GameInnerDosTypeEnum.SPECIAL_DOS;
            }
            // 过高干预
            GameRtpDosCondition tooHighCondition = gameRtpDosConditionMap.get(GameInnerDosTypeEnum.TOO_HIGH_DOS);
            if (Objects.nonNull(tooHighCondition) && tooHighCondition.getIn().assertIt(currentRtp)) {
                return GameInnerDosTypeEnum.TOO_HIGH_DOS;
            }
            // 过低干预
            GameRtpDosCondition tooLowCondition = gameRtpDosConditionMap.get(GameInnerDosTypeEnum.TOO_LOW_DOS);
            if (Objects.nonNull(tooLowCondition) && tooLowCondition.getIn().assertIt(currentRtp)) {
                return GameInnerDosTypeEnum.TOO_LOW_DOS;
            }
            // 如果没有匹配，则使用默认normal
            return GameInnerDosTypeEnum.NORMAL_DOS;
        }
    }

}
