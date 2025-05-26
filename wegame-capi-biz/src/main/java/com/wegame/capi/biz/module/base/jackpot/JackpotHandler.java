package com.wegame.capi.biz.module.base.jackpot;

import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeReqDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JackpotHandler {

    /**
     * 计算奖池分布
     * @param amount
     * @return
     */
    public List<JackpotRewardPoolDo> calculateRewardPool(BetTypeEnum betTypeEnum,Long amount){
        return null;
    }

    private List<JackpotWinPrizeDo> calculateWinPrize(Long betRecordId, List<JackpotWinPrizeReqDo> prizeReqDos){
        return null;
    }

}
