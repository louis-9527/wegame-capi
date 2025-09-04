package com.wegame.capi.biz.service.metrics;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.MetricsTypeEnum;
import com.wegame.capi.infrastructure.config.properties.GameInnerDosProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 指标聚合处理服务
 */
@Slf4j
public abstract class AbstractMetricsService implements MetricsService{
    @Autowired
    private GameMetricsService gameMetricsService;
    @Autowired
    private UserPlayMetricsService userPlayMetricsService;
    @Autowired
    private MetricsFlushTimerService metricsFlushTimerService;
    @Autowired
    private GameInnerDosProperties gameInnerDosProperties;

    /**
     * 指标获取
     *
     * @param betRecordDo 投注记录do
     * @param gameType    gameType
     * @return true/false
     */
    public boolean metrics(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        if (!this.canMetrics(gameType)) {
            log.info("MetricsService#metrics.game inner dos not enable:{}", gameType);
            return true;
        }
        this.gameMetrics(betRecordDo, gameType);
        this.userPlayMetrics(betRecordDo, gameType);
        return true;
    }

    private void gameMetrics(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        // 大盘指标缓存
        gameMetricsService.cache();
        // 大盘指标刷盘任务添加
        metricsFlushTimerService.addGameMetricsFlushTask(betRecordDo.getPId(), betRecordDo.getChannelId(), MetricsTypeEnum.valueOfGameType(gameType));

    }

    private void userPlayMetrics(BetRecordDo betRecordDo, GameTypeEnum gameType) {
        // 用户指标缓存
        userPlayMetricsService.cache();
        // 用户指标刷盘任务添加
        metricsFlushTimerService.addUserMetricsFlushTask(betRecordDo.getPId(),
                betRecordDo.getChannelId(),
                betRecordDo.getPlayerId(),
                MetricsTypeEnum.valueOfGameType(gameType));

    }


    private boolean canMetrics(GameTypeEnum gameType) {
        return this.gameEnable(gameType);
    }

    private boolean gameEnable(GameTypeEnum gameType) {
        return gameInnerDosProperties.getEnableGameList().contains(gameType);
    }

    @Override
    public boolean metrics(BetRecordDo betRecordDo, MetricsTypeEnum metricsType, Object extInfo) {
        return false;
    }
}
