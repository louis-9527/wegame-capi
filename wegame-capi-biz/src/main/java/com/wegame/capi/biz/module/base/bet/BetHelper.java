package com.wegame.capi.biz.module.base.bet;

import com.wegame.capi.biz.converter.ToVoConverter;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;

import java.util.List;

public interface BetHelper<RD extends ToVoConverter, R> {

    /**
     * 构建投注记录
     *
     * @param betRecordId      投注记录id
     * @param betPromotionType 投注营销类型
     * @param betDo            投注参数
     * @param extendInfo       投注扩展信息
     * @param gameRecordList   游戏记录列表
     * @return 投注记录
     */
    BetRecordDo<R> buildBetRecord(long betRecordId,
                                  BetPromotionTypeEnum betPromotionType,
                                  BetDo betDo,
                                  BetRecordDo.ExtendInfo extendInfo,
                                  List<R> gameRecordList);

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
