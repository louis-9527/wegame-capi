package com.wegame.capi.biz.service;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.context.strategy.bet.AbstractBetContext;
import com.wegame.capi.biz.domain.BetDo;
import com.wegame.capi.biz.domain.BetResultDo;
import com.wegame.capi.common.enums.GameEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * 投注服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class BetService {

    private static final Map<GameEnum, String> contextNameMap = new ImmutableMap.Builder<GameEnum, String>()
            .put(GameEnum.MYSTERIOUS, "mysteriousBetContext")
            .build();

    @Autowired
    Map<String, AbstractBetContext> contextMap;

    public BetResultDo bet(BetDo betDo) {
        GameEnum gameEnum = GameEnum.valueOfCode(betDo.getGameId());
        AssertUtil.assertNotNull(gameEnum, ErrorCode.ERR_PARAM);
        String betContextName = contextNameMap.get(gameEnum);
        AssertUtil.assertStrNotEmpty(betContextName, ErrorCode.ERR_PARAM, "Not Support Game");
        AbstractBetContext betContext = contextMap.get(betContextName);
        AssertUtil.assertNotNull(betContext, ErrorCode.ERR_PARAM, "Not Support Game");
        return betContext.bet(betDo);
    }
}