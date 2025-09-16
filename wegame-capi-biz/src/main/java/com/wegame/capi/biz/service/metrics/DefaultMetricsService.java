package com.wegame.capi.biz.service.metrics;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.client.enums.innerdos.GameMetricsPropertyNameEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.client.enums.innerdos.UserPlayMetricsPropertyNameEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

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
    protected Map<GameMetricsPropertyNameEnum, Object> getGameMetricsMap(BetRecordDo betRecordDo, Object extInfo) {
        Map<GameMetricsPropertyNameEnum, Object> gameMetricsMap = new HashMap<>();
        gameMetricsMap.put(GameMetricsPropertyNameEnum.totalBetAmount, betRecordDo.getPayAmount());
        gameMetricsMap.put(GameMetricsPropertyNameEnum.totalPayoutAmount, betRecordDo.getPayOutAmount());
        return gameMetricsMap;
    }

    @Override
    protected Map<UserPlayMetricsPropertyNameEnum, Object> getUserPlayMetricsMap(BetRecordDo betRecordDo, Object extInfo) {
        Map<UserPlayMetricsPropertyNameEnum, Object> metricsDataMap = new HashMap<>();
        Boolean hasFreeSpin = (Boolean) extInfo;
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.totalGameCount, 1);
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.totalBetAmount, betRecordDo.getBetAmount());
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.totalPayoutAmount, betRecordDo.getPayOutAmount());
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.continuousLostCount, betRecordDo.getPayOutAmount() > 0 ? 0 : 1);
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.lastBetTime, betRecordDo.getBetTime());
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.continuousNoFreeSpinCount, hasFreeSpin ? 0L : 1L);
        return metricsDataMap;
    }

}
