package com.wegame.capi.biz.service.metrics;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.client.enums.MetricsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 默认指标服务
 */
@Slf4j
@Service("defaultMetricsService")
public class DefaultMetricsService extends AbstractMetricsService {
    @Override
    public MetricsTypeEnum metricsType() {
        // 默认指标服务返回null
        return null;
    }

    @Override
    protected GameMetricsDo getGameMetricsDo(BetRecordDo betRecordDo, Object extInfo) {
        BigDecimal totalBetAmount = new BigDecimal(betRecordDo.getPayAmount());
        BigDecimal totalPayoutAmount = new BigDecimal(betRecordDo.getPayOutAmount());
        return GameMetricsDo.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .gameId(betRecordDo.getGameId())
                .totalBetAmount(betRecordDo.getPayAmount())
                .totalPayoutAmount(betRecordDo.getPayOutAmount())
                // 只代表增量
                .currentRtp(totalPayoutAmount.divide(totalBetAmount, 5, RoundingMode.FLOOR))
                .build();
    }

    @Override
    protected UserPlayMetricsDo getUserPlayMetricsDo(BetRecordDo betRecordDo, Object extInfo) {
        Boolean hasFreeSpin = (Boolean) extInfo;
        UserPlayMetricsDo.builder()
                .pId(betRecordDo.getPId())
                .channelId(betRecordDo.getChannelId())
                .gameId(betRecordDo.getGameId())
                .playerId(betRecordDo.getPlayerId())
                .playerName(betRecordDo.getPlayerName())
                .totalGameCount(1L)
                .totalBetAmount(betRecordDo.getPayAmount())
                .totalPayoutAmount(betRecordDo.getPayOutAmount())
                .continuousLostCount(betRecordDo.getPayOutAmount() > 0 ? 0 : 1)
                .lastBetTime(betRecordDo.getBetTime())
                // 这个值在各个游戏的地方给到,会涉及betRecord 生成的地方改造
                .continuousNoFreeSpinCount(hasFreeSpin ? 0L : 1L)
                .build();

        return null;
    }

}
