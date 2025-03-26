package com.wegame.capi.biz.module.base.bet.function;

import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;

import java.util.List;

@FunctionalInterface
public interface BetRecordFunction<T> {

    BetRecordDo<T> build(long betRecordId, BetDo betDo, List<T> gameRecordList);

}
