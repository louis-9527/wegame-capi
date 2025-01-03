package com.wegame.capi.biz.converter;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.MysteriousBuyFreeSpinsBetDo;
import com.wegame.capi.biz.domain.biz.bet.MysteriousNonBuyFreeSpinsBetDo;
import com.wegame.capi.biz.domain.biz.bet.MysteriousNormalBetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Objects;

/**
 * UserConverter
 *
 * @author generate
 */
public class BetConverter {

    private static final Map<BetSubTypeEnum, Class<?>> mysteriousClassMap = new ImmutableMap.Builder<BetSubTypeEnum, Class<?>>()
            .put(BetSubTypeEnum.NORMAL_BET, MysteriousNormalBetDo.class)
            .put(BetSubTypeEnum.BUY_FREE_SPINS_BET, MysteriousBuyFreeSpinsBetDo.class)
            .put(BetSubTypeEnum.NON_BUY_FREE_SPINS_BET, MysteriousNonBuyFreeSpinsBetDo.class)
            .build();

    private static final Map<GameTypeEnum, Map<BetSubTypeEnum, Class<?>>> gameClassMap = new ImmutableMap.Builder<GameTypeEnum, Map<BetSubTypeEnum, Class<?>>>()
            .put(GameTypeEnum.MARIA_MAKILING, mysteriousClassMap)
            .build();

    public static BetDo requestToDo(BetRequest betRequest) {
        if (Objects.isNull(betRequest)) {
            return null;
        }
        GameTypeEnum gameType = GameTypeEnum.valueOfCode(betRequest.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        Map<BetSubTypeEnum, Class<?>> map = gameClassMap.get(gameType);
        AssertUtil.assertTrue(MapUtils.isNotEmpty(map), ErrorCode.ERR_PARAM);
        Class clazz = map.get(betRequest.getBetSubType());
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