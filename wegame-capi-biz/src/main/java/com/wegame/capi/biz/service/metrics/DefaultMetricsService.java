package com.wegame.capi.biz.service.metrics;

import com.wegame.capi.client.enums.MetricsTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 默认指标服务
 */
@Slf4j
@Service
public class DefaultMetricsService extends AbstractMetricsService {
    @Override
    public MetricsTypeEnum metricsType() {
        // 默认指标服务返回null
        return null;
    }
}
