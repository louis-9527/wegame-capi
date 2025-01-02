package com.wegame.capi.biz.service.bet;

import com.wegame.capi.biz.domain.business.bet.GameBetReqDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDo;

public interface GameBetService <T,R>{
    GameBetResultDo<R> bet(GameBetReqDo<T> t);
    GameBetResultDetailDo queryBetFreeSpin(String gameId, Long recordId, Integer freeSpinId);

}
