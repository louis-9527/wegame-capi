package com.wegame.capi.biz.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegame.capi.biz.domain.business.bet.GameBetNormalReqDo;
import com.wegame.capi.biz.domain.business.bet.GameBetReqDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDo;
import com.wegame.capi.client.domain.request.update.BetBaseRequest;
import com.wegame.capi.client.domain.request.update.BetNormalDetailRequest;
import com.wegame.capi.client.domain.vo.GameBetBaseVo;
import com.wegame.capi.common.enums.GameBetSubTypeEnum;
import com.wegame.capi.common.enums.GameBetTypeEnum;

import java.util.Objects;

/**
 * UserConverter
 *
 * @author generate
 */
public class BetConverter {

    public static <R> GameBetReqDo<R> reqToDo(BetBaseRequest<Object> betRequest) {
        if (Objects.isNull(betRequest)) {
            return null;
        }
        GameBetReqDo<R> betReqDo = new GameBetReqDo<R>();
        betReqDo.setGameId(betRequest.getGameId());
        betReqDo.setBetType(GameBetTypeEnum.valueOfCode(betRequest.getBetType()));
        betReqDo.setBetSubType(GameBetSubTypeEnum.valueOfCode(betRequest.getBetSubType()));
        ObjectMapper objectMapper = new ObjectMapper();
        R serviceRequestData = objectMapper.convertValue(betRequest.getBetDetail(), (Class<R>)GameBetNormalReqDo.class);
        betReqDo.setBetDetail(serviceRequestData);
        return betReqDo;
    }

    public static GameBetBaseVo doToVo(GameBetResultDo resultDo) {
        if (Objects.isNull(resultDo)) {
            return null;
        }
        GameBetBaseVo betBaseVo = new GameBetBaseVo();
        betBaseVo.setGameRecordId(String.valueOf(resultDo.getGameRecordId()));
        betBaseVo.setGameId(resultDo.getGameId());
        betBaseVo.setBetType(resultDo.getBetType().getCode());
        betBaseVo.setBetSubType(resultDo.getBetSubType().getCode());
        betBaseVo.setResultDetail(resultDo.getResultDetail());
        return betBaseVo;
    }


}