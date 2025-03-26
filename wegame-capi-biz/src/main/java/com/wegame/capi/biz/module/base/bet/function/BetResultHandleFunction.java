package com.wegame.capi.biz.module.base.bet.function;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;

@FunctionalInterface
public interface BetResultHandleFunction<RD extends ToVoConverter, R> {

    BetResultDo<RD> handle(BetRecordDo<R> betRecord, SingleWalletBetResultDo result);

}
