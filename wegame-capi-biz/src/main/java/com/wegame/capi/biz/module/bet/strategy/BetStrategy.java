package com.wegame.capi.biz.module.bet.strategy;


import com.wegame.capi.biz.converter.Converter;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;

/**
 * 投注策略
 */
public interface BetStrategy<T, R extends Converter> {

    /**
     * 投注
     *
     * @param betDo 投注请求
     * @return 投注结果
     */
    BetResultDo<R> bet(BetDo<T> betDo);
}
