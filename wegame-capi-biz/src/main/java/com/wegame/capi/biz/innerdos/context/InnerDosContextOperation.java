package com.wegame.capi.biz.innerdos.context;

import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;

import java.util.Map;

/**
 * 定义context行为
 */
public interface InnerDosContextOperation {
    /**
     * 获取用户指标数据
     *
     * @return do
     */
    UserPlayMetricsDo getUserPlayMetricsDo();

    /**
     * 获取大盘指标数据
     *
     * @return do
     */
    GameMetricsDo getGameMetricsDo();

    /**
     * 获取指定模版范围配置
     * key:倍率范围.如：1-10
     * value:权重
     */
    Map<String, Integer> getTemplateRangeMap(Object... args);

}
