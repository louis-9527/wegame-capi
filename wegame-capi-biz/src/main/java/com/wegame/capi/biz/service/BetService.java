package com.wegame.capi.biz.service;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetContextDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.module.base.bet.AbstractBetContext;
import com.wegame.capi.client.enums.GameTypeEnum;
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

    private static final Map<GameTypeEnum, String> contextNameMap = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.MARIA_MAKILING, "mariaMakilingBetContext")
            .put(GameTypeEnum.MALAKAS, "malakasBetContext")
            .put(GameTypeEnum.IBONG_ADARNA, "ibongAdarnaBetContext")
            .build();

    @Autowired
    Map<String, AbstractBetContext> contextMap;

    public BetResultDo bet(BetContextDo betContextDo) {
        AssertUtil.assertNotNull(betContextDo, ErrorCode.ERR_PARAM);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(betContextDo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        String betContextName = contextNameMap.get(gameType);
        AssertUtil.assertStrNotEmpty(betContextName, ErrorCode.ERR_PARAM, "Not Support Game");
        AbstractBetContext betContext = contextMap.get(betContextName);
        AssertUtil.assertNotNull(betContext, ErrorCode.ERR_PARAM, "Not Support Game");
        return betContext.bet(betContextDo);
    }
}