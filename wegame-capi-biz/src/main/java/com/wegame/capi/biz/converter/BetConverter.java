package com.wegame.capi.biz.converter;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.MariaMakilingBetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;

import java.util.Map;
import java.util.Objects;

/**
 * BetConverter
 *
 * @author generate
 */
public class BetConverter {

    private static final Map<GameTypeEnum, Class<?>> GAME_BET_CLASS_MAP = new ImmutableMap.Builder<GameTypeEnum,Class<?>>()
            .put(GameTypeEnum.MARIA_MAKILING, MariaMakilingBetDo.class)
            .build();

    public static BetDo requestToDo(BetRequest betRequest) {
        if (Objects.isNull(betRequest)) {
            return null;
        }
        GameTypeEnum gameType = GameTypeEnum.valueOfCode(betRequest.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        Class<?> clazz = GAME_BET_CLASS_MAP.get(gameType);
        AssertUtil.assertNotNull(clazz, ErrorCode.ERR_PARAM);
        return BetDo.builder()
                .gameId(betRequest.getGameId())
                .gameType(GameTypeEnum.valueOfCode(betRequest.getGameId()))
                .betType(BetTypeEnum.valueOfCode(betRequest.getBetType()))
                .betSubType(BetSubTypeEnum.valueOfCode(betRequest.getBetSubType()))
                .betDetail(FastJsonUtils.fromJSONString(FastJsonUtils.toJSONString(betRequest.getBetDetail()), clazz))
                .build();
    }
}