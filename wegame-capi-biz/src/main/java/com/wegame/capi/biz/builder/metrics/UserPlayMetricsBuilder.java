package com.wegame.capi.biz.builder.metrics;

import com.wegame.capi.biz.domain.metrics.MetricsCacheKeyDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;
import com.wegame.capi.client.enums.innerdos.MetricsTypeEnum;
import com.wegame.capi.client.enums.innerdos.UserPlayMetricsPropertyNameEnum;
import com.wegame.capi.common.Constants;

import java.util.Map;
import java.util.Objects;

public class UserPlayMetricsBuilder {

    public static UserPlayMetricsDo build(Map<UserPlayMetricsPropertyNameEnum, Object> metricsDataMap, MetricsCacheKeyDo cacheKeyDo, MetricsTypeEnum metricsType) {
        if (Objects.isNull(metricsDataMap)) {
            return null;
        }
        UserPlayMetricsDo aDo = new UserPlayMetricsDo();
        aDo.injectNewData(metricsDataMap);
        aDo.setPId(cacheKeyDo.getPId());
        aDo.setChannelId(cacheKeyDo.getChannelId());
        aDo.setPlayerId(cacheKeyDo.getPlayerId());
        aDo.setPlayerName(cacheKeyDo.getPlayerName());
        aDo.setGameId(metricsType.getGameType().getId());
        aDo.setCreator(Constants.SYSTEM);
        aDo.setCreateTime(System.currentTimeMillis());
        return aDo;
    }


}
