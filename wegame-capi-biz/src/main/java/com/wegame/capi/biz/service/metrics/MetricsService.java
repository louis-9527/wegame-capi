package com.wegame.capi.biz.service.metrics;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.client.enums.MetricsTypeEnum;

/**
 * 指标采集服务
 */
public interface MetricsService {

    /**
     * 指标采集
     *
     * @param betRecordDo 投注记录
     * @param metricsType 指标类型
     * @param extInfo     扩展 信息
     * @return true/ false
     */
    boolean metrics(BetRecordDo betRecordDo, MetricsTypeEnum metricsType, Object extInfo);

    /**
     * 采集类型，一个bean只能用于一个type使用
     *
     * @return type
     */
    MetricsTypeEnum metricsType();

}
