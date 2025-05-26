package com.wegame.capi.biz.module.base.jackpot;

import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeDo;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotWinPrizeReqDo;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JackpotHandler {

    /**
     * 计算奖池提成
     *
     * @param betTypeEnum 投注类型
     * @param betAmount   投注金额
     * @return Map<奖池类型, 提成金额>
     */
    public Map<JackpotRewardPoolTypeEnum, Long> calculateRewardPoolCommission(BetTypeEnum betTypeEnum, Long betAmount) {


        return null;
    }

    private List<JackpotWinPrizeDo> calculateWinPrize(Long betRecordId, List<JackpotWinPrizeReqDo> prizeReqDos) {
        return null;
    }

}
