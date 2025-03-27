package com.wegame.capi.biz.module.base.bet;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;

import java.util.List;

public interface BetHelper<RD extends ToVoConverter, R> {

    /**
     * 构建投注记录
     *
     * @param betRecordId    投注记录id
     * @param betDo          投注参数
     * @param gameRecordList 游戏记录列表
     * @return 投注记录
     */
    BetRecordDo<R> buildBetRecord(long betRecordId, BetDo betDo, List<R> gameRecordList, String ieId);

    /**
     * 投注结果处理
     *
     * @param betRecord 投注记录
     * @param result    单钱包投注结果
     * @return 投注结果
     */
    BetResultDo<RD> handleBetResult(BetRecordDo<R> betRecord, SingleWalletBetResultDo result);

    /**
     * 判断是否中免费轮
     *
     * @param gameRecordList 游戏记录列表
     * @return 是否中免费轮
     */
    boolean hasRewardFreeWheel(List<R> gameRecordList);
}
