package com.wegame.capi.biz.handler;

import com.wegame.capi.biz.domain.game.OddsDo;
import com.wegame.capi.biz.domain.game.OddsResultDo;

import java.math.BigDecimal;

/**
 * 赔率handler
 */
public interface OddsHandler {

    /**
     * 处理
     *
     * @param oddsDo rewardDo
     * @return result
     */
    OddsResultDo handle(OddsDo oddsDo);

    /**
     * 获取赔率
     *
     * @param oddsDo rewardDo
     * @return result
     */
    BigDecimal getOdds(OddsDo oddsDo);


}
