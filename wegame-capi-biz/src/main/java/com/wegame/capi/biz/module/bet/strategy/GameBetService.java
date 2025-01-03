//package com.wegame.capi.biz.module.bet.strategy;
//
//import com.wegame.capi.biz.domain.business.bet.GameBetReqDo;
//import com.wegame.capi.biz.domain.business.bet.GameBetResultDetailDo;
//import com.wegame.capi.biz.domain.business.bet.GameBetResultDo;
//
//public interface GameBetService <T,R>{
//
//    /**
//     * 投注
//     * @param t
//     * @return
//     */
//    GameBetResultDo<R> bet(GameBetReqDo<T> t);
//
//    /**
//     * 获取免费轮投注详情
//     * @param gameId
//     * @param recordId
//     * @param freeSpinId
//     * @return
//     */
//    GameBetResultDetailDo queryBetFreeSpin(String gameId, Long recordId, Integer freeSpinId);
//
//}
