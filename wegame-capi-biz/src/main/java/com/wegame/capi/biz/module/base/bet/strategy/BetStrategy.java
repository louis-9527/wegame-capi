package com.wegame.capi.biz.module.base.bet.strategy;


import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.bet.BetContextDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;

/**
 * 投注策略
 *
 * @param <R> 投注结果详情
 */
public interface BetStrategy<R extends ToVoConverter> {

    /**
     * 投注
     *
     * @param betContextDo 投注请求
     * @return 投注结果
     */
    BetResultDo<R> bet(BetContextDo betContextDo);
}
