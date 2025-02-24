package com.wegame.capi.biz.converter;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.BetRequestValidate;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;

import java.util.Map;

/**
 * BetConverter
 *
 * @author generate
 */
public class BetConverter {

    // 定义转换模型
    private static final Map<GameTypeEnum, BetToDoConvert<?, ?>> GAME_BET_CLASS_MAP = ImmutableMap.<GameTypeEnum, BetToDoConvert<?, ?>>builder()
            .put(GameTypeEnum.MARIA_MAKILING, new MariaMakilingBetRequestConvert<>())
            .put(GameTypeEnum.IBONG_ADARNA, new IbongAdarnaBetRequestConvert())
            .build();

    public static <T extends BetRequestValidate, R> BetDo<R> requestToDo(BetRequest betRequest) {GameTypeEnum gameType = GameTypeEnum.valueOfId(betRequest.getGameId());
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_PARAM);
        BetToDoConvert<T, R> converter = (BetToDoConvert<T, R>) GAME_BET_CLASS_MAP.get(gameType);
        AssertUtil.assertNotNull(converter, ErrorCode.ERR_PARAM);
        //入参转换
        T detail = converter.toDetail(betRequest);
        detail.validateParam(
                gameType,
                BetTypeEnum.valueOfCode(betRequest.getBetType()),
                BetSubTypeEnum.valueOfCode(betRequest.getBetSubType())
        );
        R betDetail = converter.toDo(detail);
        return BetDo.<R>builder()
                .gameId(betRequest.getGameId())
                .gameType(gameType)
                .betType(BetTypeEnum.valueOfCode(betRequest.getBetType()))
                .betSubType(BetSubTypeEnum.valueOfCode(betRequest.getBetSubType()))
                .betDetail(betDetail)
                .build();
    }


}