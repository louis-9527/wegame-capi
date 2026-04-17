package com.wegame.capi.biz.service;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.module.base.bet.AbstractBetContext;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

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
            .put(GameTypeEnum.LAM_ANG, "lamangBetContext")
            .put(GameTypeEnum.BAKUNAWA, "bakunawaBetContext")
            .put(GameTypeEnum.DIWATA_MAYARI, "diwataMayariBetContext")
            .put(GameTypeEnum.KAPRE, "kapreBetContext")
            .put(GameTypeEnum.MANGKUKULAM, "mangkukulamBetContext")
            .put(GameTypeEnum.SIRENA, "sirenaBetContext")
            .build();

    @Autowired
    Map<String, AbstractBetContext> contextMap;

    public BetResultDo bet(BetDo betDo) {
        AssertUtil.assertNotNull(betDo, ErrorCode.ERR_PARAM);
        GameTypeEnum gameType = GameTypeEnum.valueOfId(betDo.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        buildGamePlayType(betDo);
        String betContextName = contextNameMap.get(gameType);
        AssertUtil.assertStrNotEmpty(betContextName, ErrorCode.ERR_PARAM, "Not Support Game");
        AbstractBetContext betContext = contextMap.get(betContextName);
        AssertUtil.assertNotNull(betContext, ErrorCode.ERR_PARAM, "Not Support Game");
        return betContext.bet(betDo);
    }

    public void buildGamePlayType(BetDo betDo) {
        betDo.setGamePlayType(GamePlayTypeEnum.TEMPLATE_PLAY);
        if (Objects.equals(betDo.getGameType(), GameTypeEnum.KAPRE)) {
            betDo.setGamePlayType(GamePlayTypeEnum.NORMAL_PLAY);
        }
    }
}