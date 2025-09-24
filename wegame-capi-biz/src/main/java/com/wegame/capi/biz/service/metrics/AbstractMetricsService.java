package com.wegame.capi.biz.service.metrics;

import com.alibaba.fastjson.JSON;
import com.wegame.base.lock.DistributeLock;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.metrics.MetricsCacheKeyDo;
import com.wegame.capi.client.enums.innerdos.GameMetricsPropertyNameEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.client.enums.innerdos.UserPlayMetricsPropertyNameEnum;
import com.wegame.capi.common.utils.LockNameUtil;
import com.wegame.capi.infrastructure.config.ThreadPoolConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import static com.wegame.capi.common.Constants.OPT_BUSY;


/**
 * 指标聚合处理服务
 */
@Slf4j
public abstract class AbstractMetricsService implements MetricsService {
    @Autowired
    private GameMetricsDataService gameMetricsDataService;
    @Autowired
    private UserPlayMetricsDataService userPlayMetricsDataService;
    @Autowired
    private MetricsFlushTimerService metricsFlushTimerService;
    @Autowired
    @Qualifier(ThreadPoolConfig.innerDosThreadPoolExecutor)
    private ThreadPoolExecutor executor;
    @Autowired
    private DistributeLock distributeLock;

    /**
     * 获取大盘增量指标
     *
     * @param betRecordDo betRecordDo
     * @param extInfo     extInfo 自定义数据
     * @return do
     */
    protected abstract Map<GameMetricsPropertyNameEnum, Object> getGameMetricsMap(BetRecordDo betRecordDo, Object extInfo);

    /**
     * 获取用户增量指标
     *
     * @param betRecordDo betRecordDo
     * @param extInfo     extInfo 自定义数据
     * @return do
     */
    protected abstract Map<UserPlayMetricsPropertyNameEnum, Object> getUserPlayMetricsMap(BetRecordDo betRecordDo, Object extInfo);

    /**
     * 指标获取
     * 使用分布式锁，非锁的会有数据一致性问题
     *
     * @param betRecordDo 投注记录do
     * @param metricsType metricsType
     * @return true/false
     */
    @Override
    public boolean metrics(BetRecordDo betRecordDo, MetricsTypeEnum metricsType, Object extInfo) {
        // 用户采集同步
        this.userPlayMetrics(betRecordDo, metricsType, extInfo);
        // 大盘采集async
        executor.submit(() -> {
            distributeLock.tryLockThenCall(LockNameUtil.getMetricsLockName(metricsType.getGameType()),
                    2000,
                    3000,
                    () -> {
                        this.gameMetrics(betRecordDo, metricsType, extInfo);
                        return true;
                    },
                    OPT_BUSY);
        });
        return true;
    }

    /**
     * 大盘指标采集服务
     * 1.指标采集
     * 2.刷盘任务添加
     *
     * @param betRecordDo betRecordDo
     * @param metricsType metricsType
     * @param extInfo     extInfo
     */
    private void gameMetrics(BetRecordDo betRecordDo, MetricsTypeEnum metricsType, Object extInfo) {
        // 大盘指标缓存
        gameMetricsDataService.cache(this.getGameMetricsMap(betRecordDo, extInfo), this.buildCacheKeyDo(betRecordDo), metricsType);
        // 大盘指标刷盘任务添加
        metricsFlushTimerService.addGameMetricsFlushTask(betRecordDo.getPId(), betRecordDo.getChannelId(), metricsType);
    }

    /**
     * 用户指标采集服务
     * 1.指标采集
     * 2.刷盘任务添加
     *
     * @param betRecordDo betRecordDo
     * @param metricsType metricsType
     * @param extInfo     extInfo
     */
    private void userPlayMetrics(BetRecordDo betRecordDo, MetricsTypeEnum metricsType, Object extInfo) {
        try {
            // 用户指标缓存
            userPlayMetricsDataService.cache(this.getUserPlayMetricsMap(betRecordDo, extInfo), this.buildCacheKeyDo(betRecordDo), metricsType);
            // 用户指标刷盘任务添加
            metricsFlushTimerService.addUserMetricsFlushTask(betRecordDo.getPId(),
                    betRecordDo.getChannelId(),
                    betRecordDo.getPlayerId(),
                    metricsType);
        } catch (Exception e) {
            log.error("AbstractMetricsService#userPlayMetrics error,betRecordDo:{}", JSON.toJSONString(betRecordDo), e);
        }
    }

    private MetricsCacheKeyDo buildCacheKeyDo(BetRecordDo betRecordDo) {
        return MetricsCacheKeyDo.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .playerId(betRecordDo.getPlayerId())
                .playerName(betRecordDo.getPlayerName())
                .build();
    }


}
