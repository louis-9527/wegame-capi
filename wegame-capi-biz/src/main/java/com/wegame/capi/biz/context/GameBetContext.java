package com.wegame.capi.biz.context;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.converter.BetConverter;
import com.wegame.capi.biz.domain.business.bet.GameBetReqDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDetailDo;
import com.wegame.capi.biz.domain.business.bet.GameBetResultDo;
import com.wegame.capi.biz.service.bet.AbsGameBetService;
import com.wegame.capi.biz.service.bet.GameBetNormalService;
import com.wegame.capi.biz.service.bet.GameBetService;
import com.wegame.capi.client.domain.request.query.QueryFreeSpinRequest;
import com.wegame.capi.client.domain.request.update.BetBaseRequest;
import com.wegame.capi.client.domain.vo.GameBetBaseVo;
import com.wegame.capi.client.domain.vo.GameBetNormalVo;
import com.wegame.capi.common.enums.BetSubTypeEnum;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 下注上下文
 */
@Component
public class GameBetContext {


    private static final Map<BetSubTypeEnum, String> betStrategyNameMap = new ImmutableMap.Builder<BetSubTypeEnum, String>()
            .put(BetSubTypeEnum.NORMAL_BET, "gameBetNormalService")
            .put(BetSubTypeEnum.NON_BUY_FREE_SPINS_BET, "gameBetNoneBuyFreeService")
            .put(BetSubTypeEnum.BUY_FREE_SPINS_BET, "gameBetBuyFreeService")
            .build();

    @Autowired
    Map<String, GameBetService<?, ?>> betStrategyMap;


    /**
     * 投注
     * * @return
     */
    public <T, R> GameBetBaseVo<Object> bet(BetBaseRequest<Object> betRequest) {
        BetSubTypeEnum betSubType = BetSubTypeEnum.valueOfCode(betRequest.getBetSubType());
        String betStrategyName = betStrategyNameMap.get(betSubType);
        AssertUtil.assertStrNotEmpty(betStrategyName, ErrorCode.ERR_PARAM, "Not Support BetType");
        AbsGameBetService<T, R> betStrategy = (AbsGameBetService<T, R>) betStrategyMap.get(betStrategyName);
        AssertUtil.assertNotNull(betStrategy, ErrorCode.ERR_PARAM, "Not Support BetType");
        //对象转换
        GameBetReqDo<T> betReqDo = BetConverter.reqToDo(betRequest, betStrategy.tClass());

        //执行下注请求
        GameBetResultDo<R> betResult = betStrategy.bet(betReqDo);
        return BetConverter.doToVo(betResult);
    }

    public  <T, R>  GameBetNormalVo queryFreeSpinDetail(QueryFreeSpinRequest request) {
        BetSubTypeEnum betSubType = BetSubTypeEnum.valueOfCode(request.getBetSubType());
        String betStrategyName = betStrategyNameMap.get(betSubType);
        AssertUtil.assertStrNotEmpty(betStrategyName, ErrorCode.ERR_PARAM, "Not Support BetType");
        AbsGameBetService<T, R> betStrategy = (AbsGameBetService<T, R>) betStrategyMap.get(betStrategyName);
        GameBetResultDetailDo resultDetailDo = betStrategy.queryBetFreeSpin(request.getGameId(), Long.valueOf(request.getGameRecordId()), Integer.valueOf(request.getFreeSpinId()));
        return BetConverter.detailToNormalVo(resultDetailDo);
    }
}
