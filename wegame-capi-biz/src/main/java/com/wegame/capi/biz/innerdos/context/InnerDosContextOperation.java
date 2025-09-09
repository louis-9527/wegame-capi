package com.wegame.capi.biz.innerdos.context;

import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.metrics.GameMetricsDo;
import com.wegame.capi.biz.domain.metrics.UserPlayMetricsDo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 定义context行为：关键行为统一
 */
public interface InnerDosContextOperation {

    /**
     * 获取投注BetDo
     */
    BetDo getBetDo();

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
    Map<String, BigDecimal> getTemplateRangeMap(Object... args);

    /**
     * 是否新人判断
     *
     * @param args 灵活参数
     */
    boolean isNewPlayer(Object... args);

}
