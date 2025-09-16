package com.wegame.capi.biz.builder.metrics;


import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.MetricsCacheKeyDo;
import com.wegame.capi.biz.innerdos.config.GameRtpDosCondition;
import com.wegame.capi.client.enums.innerdos.GameInnerDosTypeEnum;
import com.wegame.capi.client.enums.innerdos.GameMetricsPropertyNameEnum;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.common.Constants;

import java.util.Map;
import java.util.Objects;

public class GameMetricsBuilder {
    public static GameMetricsDo build(Map<GameMetricsPropertyNameEnum, Object> metricsDataMap, MetricsCacheKeyDo cacheKeyDo, Map<GameInnerDosTypeEnum, GameRtpDosCondition> gameRtpDosConditionMap, MetricsTypeEnum metricsType) {
        if (Objects.isNull(metricsDataMap)) {
            return null;
        }
        GameMetricsDo aDo = new GameMetricsDo();
        aDo.injectNewData(metricsDataMap, gameRtpDosConditionMap);
        aDo.setPId(cacheKeyDo.getPId());
        aDo.setChannelId(cacheKeyDo.getChannelId());
        aDo.setGameId(metricsType.getGameType().getId());
        aDo.setCreator(Constants.SYSTEM);
        aDo.setCreateTime(System.currentTimeMillis());
        return aDo;
    }

}
