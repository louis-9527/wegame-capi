package com.wegame.capi.biz.service.metrics;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.client.enums.innerdos.GameMetricsPropertyNameEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.client.enums.innerdos.UserPlayMetricsPropertyNameEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        // todo 测试一下扩展参数
        gameMetricsMap.put(GameMetricsPropertyNameEnum.extend_example, 2L);
        return gameMetricsMap;
    }

    @Override
    protected Map<UserPlayMetricsPropertyNameEnum, Object> getUserPlayMetricsMap(BetRecordDo betRecordDo, Object extInfo) {
        Map<UserPlayMetricsPropertyNameEnum, Object> metricsDataMap = new HashMap<>();
        Boolean hasFreeSpin = Optional.ofNullable(extInfo).map(o -> (Boolean)extInfo).orElse(false);
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.totalGameCount, 1L);
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.totalBetAmount, betRecordDo.getPayAmount());
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.totalPayoutAmount, betRecordDo.getPayOutAmount());
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.continuousLostCount, betRecordDo.getPayOutAmount() > 0 ? 0 : 1);
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.lastBetTime, betRecordDo.getBetTime());
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.continuousNoFreeSpinCount, hasFreeSpin ? 0L : 1L);
        // todo 测试一下扩展参数
        metricsDataMap.put(UserPlayMetricsPropertyNameEnum.extend_example,1L);
        return metricsDataMap;
    }

}
