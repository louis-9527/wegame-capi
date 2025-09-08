package com.wegame.capi.biz.service.metrics;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import io.netty.util.HashedWheelTimer;
import io.netty.util.TimerTask;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 指标刷盘定时处理服务
 */
@Slf4j
@Service
public class MetricsFlushTimerService {
    /**
     * 大盘指标时间轮
     */
    private final HashedWheelTimer gameWheelTimer = new HashedWheelTimer(100L, TimeUnit.MILLISECONDS, 512);
    /**
     * 游戏任务map
     */
    private final Map<GameTimerKey, TimerTask> gameMetricsTimerMap = new ConcurrentHashMap<>();
    /**
     * 用户指标时间轮
     */
    private final HashedWheelTimer userPlayWheelTimer = new HashedWheelTimer(100L, TimeUnit.MILLISECONDS, 512);
    /**
     * 用户指标任务map
     */
    private final Map<UserPlayTimerKey, TimerTask> userPlayTimerMap = new ConcurrentHashMap<>();

    @Value("${game.metrics.flush.timer.interval.min:10000}")
    private Long gameMetricsFlushTimerIntervalMin;
    @Value("${game.metrics.flush.timer.interval.max:10000}")
    private Long gameMetricsFlushTimerIntervalMax;
    @Value("${user.metrics.flush.timer.interval.min:10000}")
    private Long userMetricsFlushTimerIntervalMin;
    @Value("${user.metrics.flush.timer.interval.max:10000}")
    private Long userMetricsFlushTimerIntervalMax;

    @Autowired
    private UserPlayMetricsDataService userPlayMetricsService;
    @Autowired
    private GameMetricsDataService gameMetricsService;
    @Autowired
    private RandomService randomService;

    /**
     * 添加用户指标刷盘任务
     */
    public void addUserMetricsFlushTask(String pId, String channelId, String playerId,MetricsTypeEnum metricsType) {
        // 任务是否已经存在
        UserPlayTimerKey userPlayTimerKey = new UserPlayTimerKey(pId, channelId, metricsType.getGameType(), playerId);
        if (Objects.nonNull(userPlayTimerMap.get(userPlayTimerKey))) {
            return;
        }
        // 生成新任务
        TimerTask timerTask = timeout -> {
            try {
                userPlayMetricsService.flush(pId, playerId, channelId, metricsType);
            } catch (Exception e) {
                log.error("MetricsFlushTimerService#addUserMetricsFlushTask error: {}", e.getMessage(), e);
            } finally {
                // 执行完成，删除本地
                userPlayTimerMap.remove(userPlayTimerKey);
            }
        };
        // 添加任务
        userPlayWheelTimer.newTimeout(userPlayTimerMap.get(userPlayTimerKey), this.getDelay(this.userMetricsFlushTimerIntervalMin, this.userMetricsFlushTimerIntervalMax), TimeUnit.SECONDS);
        userPlayTimerMap.put(userPlayTimerKey, timerTask);
    }


    /**
     * 添加大盘指标刷盘任务
     */
    public void addGameMetricsFlushTask(String pId, String channelId, MetricsTypeEnum metricsType) {
        // 是否有任务
        GameTimerKey gameTimerKey = new GameTimerKey(pId, channelId, metricsType.getGameType());
        if (Objects.nonNull(this.gameMetricsTimerMap.get(gameTimerKey))) {
            return;
        }
        // 生成新任务
        TimerTask timerTask = timeout -> {
            try {
                gameMetricsService.flush(pId, channelId, metricsType);
            } catch (Exception e) {
                log.error("MetricsFlushTimerService#addGameMetricsFlushTask flush error", e);
            } finally {
                // 执行完成，删除本地
                this.gameMetricsTimerMap.remove(gameTimerKey);
            }
        };
        // 添加任务
        this.gameWheelTimer.newTimeout(this.gameMetricsTimerMap.get(gameTimerKey), this.getDelay(this.gameMetricsFlushTimerIntervalMin, this.gameMetricsFlushTimerIntervalMax), TimeUnit.SECONDS);
        this.gameMetricsTimerMap.put(gameTimerKey, timerTask);
    }

    private Long getDelay(long min, long max) {
        if (max - min <= 0) {
            return min;
        } else {
            int random = randomService.randomInt(0, (int) (max - min));
            return min + random;
        }
    }


    @Data
    @ToString
    @SuperBuilder
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class UserPlayTimerKey {
        public UserPlayTimerKey(String pId, String channelId, GameTypeEnum gameType, String playerId) {
            AssertUtil.assertStrNotEmpty(pId, ErrorCode.ERR_PARAM);
            AssertUtil.assertStrNotEmpty(channelId, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
            AssertUtil.assertStrNotEmpty(playerId, ErrorCode.ERR_PARAM);
            this.pId = pId;
            this.channelId = channelId;
            this.gameId = gameType.getId();
            this.playerId = playerId;
        }

        /**
         * 平台id
         */
        private String pId;
        /**
         * 渠道id
         */
        private String channelId;
        /**
         * 游戏id
         */
        private String gameId;
        /**
         * 玩家id
         */
        private String playerId;
    }


    @Data
    @ToString
    @SuperBuilder
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class GameTimerKey {
        public GameTimerKey(String pId, String channelId, GameTypeEnum gameType) {
            AssertUtil.assertStrNotEmpty(pId, ErrorCode.ERR_PARAM);
            AssertUtil.assertStrNotEmpty(channelId, ErrorCode.ERR_PARAM);
            AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
            this.pId = pId;
            this.channelId = channelId;
            this.gameId = gameType.getId();
        }

        /**
         * 平台id
         */
        private String pId;
        /**
         * 渠道id
         */
        private String channelId;
        /**
         * 游戏id
         */
        private String gameId;
    }

    @PreDestroy
    public void destroy() {
        try {
            this.gameWheelTimer.stop();
            this.userPlayWheelTimer.stop();
        } catch (Exception e) {
            log.error("MetricsFlushTimerService destroy error", e);
        }

    }

}
