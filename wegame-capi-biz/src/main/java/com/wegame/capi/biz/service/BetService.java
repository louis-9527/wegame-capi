package com.wegame.capi.biz.service;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.EnvEnum;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.module.base.bet.AbstractBetContext;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.AbTypeEnum;
import com.wegame.capi.client.enums.AtTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 投注服务
 *
 * @author genereate
 */
@Slf4j
@Service
public class BetService {

    private static final String AT = "at";

    @Value("${spring.application.env}")
    private String env;

    private static final Map<GameTypeEnum, String> contextNameMap = new ImmutableMap.Builder<GameTypeEnum, String>()
            .put(GameTypeEnum.MARIA_MAKILING, "mariaMakilingBetContext")
            .put(GameTypeEnum.MALAKAS, "malakasBetContext")
            .put(GameTypeEnum.IBONG_ADARNA, "ibongAdarnaBetContext")
            .put(GameTypeEnum.LAM_ANG, "lamangBetContext")
            .put(GameTypeEnum.BAKUNAWA, "bakunawaBetContext")
            .put(GameTypeEnum.DIWATA_MAYARI, "diwataMayariBetContext")
            .put(GameTypeEnum.KAPRE, "kapreBetContext")
            .put(GameTypeEnum.MANGKUKULAM, "mangkukulamBetContext")
            .build();
    private static final List<GameTypeEnum> AB_CONTROL_GAME_LIST = Arrays.asList(
            GameTypeEnum.MARIA_MAKILING,
            GameTypeEnum.MALAKAS,
            GameTypeEnum.IBONG_ADARNA,
            GameTypeEnum.BAKUNAWA

    );

    private static final List<GameTypeEnum> FORCE_TEMPLATE_BET_CONTROL_GAME_LIST = Arrays.asList(
            GameTypeEnum.LAM_ANG,
            GameTypeEnum.DIWATA_MAYARI,
            GameTypeEnum.MANGKUKULAM
    );

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
        if (StringUtils.equals(env, EnvEnum.PROD.getValue())) {
            if (StringUtils.equals(betDo.getGameType().getId(), GameTypeEnum.KAPRE.getId())) {
                betDo.setGamePlayType(GamePlayTypeEnum.NORMAL_PLAY);
            } else {
                betDo.setGamePlayType(GamePlayTypeEnum.TEMPLATE_PLAY);
            }
        } else {
            UserInfo userInfo = UserInfoContext.getUserInfo();
            String at = Optional.ofNullable(userInfo.getExt())
                    .map(ext -> ext.getOrDefault(AT, AtTypeEnum.NORMAL.getId()))
                    .map(Object::toString)
                    .orElse(AtTypeEnum.NORMAL.getId());
            if (Objects.equals(at, AtTypeEnum.NORMAL.getId())) {
                betDo.setGamePlayType(GamePlayTypeEnum.NORMAL_PLAY);
            } else {
                log.info("userInfo.at: {}, template bet", at);
                betDo.setGamePlayType(GamePlayTypeEnum.TEMPLATE_PLAY);
            }
            //if (AB_CONTROL_GAME_LIST.contains(betDo.getGameType())) {
            //    if (Objects.equals(ab, AbTypeEnum.A.getId())) {
            //        betDo.setGamePlayType(GamePlayTypeEnum.NORMAL_PLAY);
            //    } else {
            //        log.info("userInfo.ut: {}, template bet", userInfo.getUt());
            //        betDo.setGamePlayType(GamePlayTypeEnum.TEMPLATE_PLAY);
            //    }
            //} else if (FORCE_TEMPLATE_BET_CONTROL_GAME_LIST.contains(betDo.getGameType())) {
            //    betDo.setGamePlayType(GamePlayTypeEnum.TEMPLATE_PLAY);
            //} else {
            //    // Kapre 强制不走模版
            //    betDo.setGamePlayType(GamePlayTypeEnum.NORMAL_PLAY);
            //}
        }

    }
}