package com.wegame.capi.biz.converter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.business.bet.*;
import com.wegame.capi.client.domain.request.update.BetBaseRequest;
import com.wegame.capi.client.domain.request.update.BetBuyDetailRequest;
import com.wegame.capi.client.domain.request.update.BetNoneBuyDetailRequest;
import com.wegame.capi.client.domain.request.update.BetNormalDetailRequest;
import com.wegame.capi.client.domain.vo.GameBetBaseVo;
import com.wegame.capi.client.domain.vo.GameBetNormalVo;
import com.wegame.capi.client.domain.vo.GameBetPayOutDetailVo;
import com.wegame.capi.client.domain.vo.SymbolIndexVo;
import com.wegame.capi.common.enums.BetSubTypeEnum;
import com.wegame.capi.common.enums.BetTypeEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * UserConverter
 *
 * @author generate
 */
public class BetConverter {

    public static <T> GameBetReqDo<T> reqToDo(BetBaseRequest<Object> betRequest) {
        if (Objects.isNull(betRequest)) {
            return null;
        }
        GameBetReqDo<T> betReqDo = new GameBetReqDo<T>();
        betReqDo.setGameId(betRequest.getGameId());
        betReqDo.setBetType(BetTypeEnum.valueOfCode(betRequest.getBetType()));
        BetSubTypeEnum betSubTypeEnum = BetSubTypeEnum.valueOfCode(betRequest.getBetSubType());
        betReqDo.setBetSubType(betSubTypeEnum);

        if (BetSubTypeEnum.NORMAL_BET == betSubTypeEnum) {
            //正常投注
            String dataJson = JSON.toJSONString(betRequest.getBetDetail());
            BetNormalDetailRequest request = JSON.parseObject(dataJson, BetNormalDetailRequest.class);

            GameBetNormalReqDo normalReqDo = new GameBetNormalReqDo();
            normalReqDo.setBetAmount(request.getBetAmount());
            betReqDo.setBetDetail((T) normalReqDo);
        } else if (BetSubTypeEnum.NON_BUY_FREE_SPINS_BET == betSubTypeEnum) {
            //非购买免费轮
            String dataJson = JSON.toJSONString(betRequest.getBetDetail());
            BetNoneBuyDetailRequest request = JSON.parseObject(dataJson, BetNoneBuyDetailRequest.class);

            GameBetNoneBuyReqDo noneBuyReqDo = new GameBetNoneBuyReqDo();
            noneBuyReqDo.setRecordId(Long.valueOf(request.getGameRecordId()));
            betReqDo.setBetDetail((T) noneBuyReqDo);
        } else if (BetSubTypeEnum.BUY_FREE_SPINS_BET == betSubTypeEnum) {
            //购买免费轮
            String dataJson = JSON.toJSONString(betRequest.getBetDetail());
            BetBuyDetailRequest request = JSON.parseObject(dataJson, BetBuyDetailRequest.class);

            GameBetBuyReqDo buyReqDo = new GameBetBuyReqDo();
            buyReqDo.setBetAmount(request.getBetAmount());
            buyReqDo.setBuyAmount(request.getBuyAmount());
            betReqDo.setBetDetail((T) buyReqDo);
        }
        return betReqDo;
    }

    public static <R> GameBetBaseVo<Object> doToVo(GameBetResultDo<R> betResult) {
        GameBetBaseVo<Object> betBaseVo = new GameBetBaseVo<>();
        betBaseVo.setGameRecordId(String.valueOf(betResult.getGameRecordId()));
        betBaseVo.setGameId(betResult.getGameId());
        betBaseVo.setBetType(betResult.getBetType().getCode());
        betBaseVo.setBetSubType(betResult.getBetSubType().getCode());
        if (betResult.resultDetail instanceof GameBetNormalResultDo) {
            GameBetNormalResultDo normalResult = (GameBetNormalResultDo) betResult.resultDetail;
            GameBetResultDetailDo detailDo = normalResult.getResultDetailDo();
            betBaseVo.setResultDetail(detailToNormalVo(detailDo));
        } else if (betResult.resultDetail instanceof GameBetNoneBuyResultDo) {
            GameBetNoneBuyResultDo normalResult = (GameBetNoneBuyResultDo) betResult.resultDetail;
            List<Integer> freeSpinIds = normalResult.getFreeSpinIds();
            betBaseVo.setResultDetail(freeSpinIds);
        } else if (betResult.resultDetail instanceof GameBetBuyResultDo) {
            GameBetBuyResultDo buyResult = (GameBetBuyResultDo) betResult.resultDetail;
            List<Integer> freeSpinIds = buyResult.getFreeSpinIds();

            betBaseVo.setResultDetail(freeSpinIds);
        }
        return betBaseVo;
    }

    public static GameBetNormalVo detailToNormalVo(GameBetResultDetailDo detailDo) {
        GameBetNormalVo normalVo = new GameBetNormalVo();
        normalVo.setPayOutAmountType(detailDo.getRewardAmountType() == null ? null : detailDo.getRewardAmountType().getCode());
        normalVo.setPayOutTotalAmount(detailDo.getRewardAmount());
        normalVo.setSpinsResult(convertToIntegerArray(detailDo.getGameResult()));
        List<GameBetPayOutDetailVo> detailVos = new ArrayList<>();
        List<GameBetResultDetailDo.RewardResult> rewardResults = detailDo.getRewardResults();
        if (CollectionUtils.isNotEmpty(rewardResults)) {
            for (GameBetResultDetailDo.RewardResult rewardResult : rewardResults) {
                GameBetPayOutDetailVo detailVo = new GameBetPayOutDetailVo();
                List<String> rewardSymbolIdList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(rewardResult.getRewardSymbolList())) {
                    rewardResult.getRewardSymbolList().forEach(symbol -> rewardSymbolIdList.add(symbol.getId()));
                }
                detailVo.setRewardSymbolIdList(rewardSymbolIdList);
                detailVo.setMultiple(rewardResult.getMultiple());
                detailVo.setOdds(rewardResult.getOdds());
                detailVo.setPayOutAmount(rewardResult.getRewardAmount());
                List<SymbolIndexVo> symbolIndexVo = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(rewardResult.getRewardIndex())) {
                    rewardResult.getRewardIndex().forEach(indexDo -> {
                        symbolIndexVo.add(SymbolIndexVo.build(indexDo.getRowIndex(), indexDo.getColumIndex()));
                    });
                }

                detailVo.setRewardIndex(symbolIndexVo);
                detailVos.add(detailVo);
            }
        }

        normalVo.setPayOutResult(detailVos);
        return normalVo;
    }


    public static String[][] convertToIntegerArray(SymbolDo[][] symbolDoArray) {
        if (symbolDoArray == null) {
            return null;
        }
        int rows = symbolDoArray.length;
        int cols = symbolDoArray[0].length;
        String[][] integerArray = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                integerArray[i][j] = symbolDoArray[i][j].getId();
            }
        }
        return integerArray;
    }

}