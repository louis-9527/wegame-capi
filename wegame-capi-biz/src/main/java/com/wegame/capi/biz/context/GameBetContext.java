package com.wegame.capi.biz.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.business.bet.GameBetNormalReqDo;
import com.wegame.capi.biz.domain.business.bet.GameBetReqDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDo;
import com.wegame.capi.biz.service.bet.AbsGameBetService;
import com.wegame.capi.client.domain.request.update.BetBaseRequest;
import com.wegame.capi.client.domain.vo.GameBetBaseVo;
import com.wegame.capi.client.domain.vo.GameBetNormalVo;
import com.wegame.capi.client.domain.vo.GameBetPayOutDetailVo;
import com.wegame.capi.common.enums.GameBetSubTypeEnum;
import com.wegame.capi.common.enums.GameBetTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class GameBetContext {


    private static final Map<GameBetSubTypeEnum, String> betStrategyNameMap = new ImmutableMap.Builder<GameBetSubTypeEnum, String>()
            .put(GameBetSubTypeEnum.NORMAL, "gameBetNormalService")
            .put(GameBetSubTypeEnum.NON_BUY_FREE, "gameBetNoneBuyFreeService")
            .put(GameBetSubTypeEnum.BUY_FREE, "gameBetBuyFreeService")
            .build();

    @Autowired
    Map<String, AbsGameBetService<?, ?>> betStrategyMap;

    /**
     * 投注
     *
     * @param betDo
     * @return
     */
    public <T, R> GameBetBaseVo<Object> bet(BetBaseRequest<Object> betRequest) {
        GameBetSubTypeEnum betSubType = GameBetSubTypeEnum.valueOfCode(betRequest.getBetSubType());
        String betStrategyName = betStrategyNameMap.get(betSubType);
        AssertUtil.assertStrNotEmpty(betStrategyName, ErrorCode.ERR_PARAM, "Not Support BetType");
        AbsGameBetService<T, R> betStrategy = (AbsGameBetService<T, R>) betStrategyMap.get(betStrategyName);
        AssertUtil.assertNotNull(betStrategy, ErrorCode.ERR_PARAM, "Not Support BetType");
        //对象转换
        GameBetReqDo<T> betReqDo = new GameBetReqDo<T>();
        betReqDo.setGameId(betRequest.getGameId());
        betReqDo.setBetType(GameBetTypeEnum.valueOfCode(betRequest.getBetType()));
        betReqDo.setBetSubType(GameBetSubTypeEnum.valueOfCode(betRequest.getBetSubType()));
        ObjectMapper objectMapper = new ObjectMapper();
        T serviceRequestData = objectMapper.convertValue(betRequest.getBetDetail(), betStrategy.tClass());
        betReqDo.setBetDetail(serviceRequestData);

        GameBetResultDo<R> betResult = betStrategy.bet(betReqDo);
        GameBetBaseVo<Object> betBaseVo = new GameBetBaseVo<Object>();
        betBaseVo.setGameRecordId(String.valueOf(betResult.getGameRecordId()));
        betBaseVo.setGameId(betResult.getGameId());
        betBaseVo.setBetType(betResult.getBetType().getCode());
        betBaseVo.setBetSubType(betResult.getBetSubType().getCode());
        if (betResult.resultDetail instanceof GameBetResultDetailDo) {
            GameBetResultDetailDo detailDo = (GameBetResultDetailDo) betResult.resultDetail;
            GameBetNormalVo normalVo = new GameBetNormalVo();
            normalVo.setPayOutAmountType(detailDo.getRewardAmountType().getCode());
            normalVo.setPayOutTotalAmount(detailDo.getRewardAmount());
            normalVo.setSpinsResult(detailDo.getGameResult());
            List<GameBetPayOutDetailVo> detailVos = new ArrayList<>();
            List<GameBetResultDetailDo.RewardResult> rewardResults = detailDo.getRewardResults();
            for (GameBetResultDetailDo.RewardResult rewardResult : rewardResults) {
                GameBetPayOutDetailVo detailVo = new GameBetPayOutDetailVo();
                detailVo.setIndex(rewardResult.getRewardIndex());
                detailVo.setRewardSymbolIdList(rewardResult.getRewardSymbolIdList());
                detailVo.setMultiple(rewardResult.getMultiple());
                detailVo.setOdds(rewardResult.getOdds());
                detailVo.setPayOutAmount(rewardResult.getRewardAmount());
                detailVos.add(detailVo);
            }

            normalVo.setPayOutDetail(detailVos);
            betBaseVo.setResultDetail(normalVo);

        }


        return betBaseVo;
    }
}
