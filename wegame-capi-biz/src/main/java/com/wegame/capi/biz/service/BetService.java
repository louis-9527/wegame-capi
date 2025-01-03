package com.wegame.capi.biz.service;

import com.google.common.collect.ImmutableMap;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.module.bet.AbstractBetContext;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.client.domain.request.query.QueryFreeSpinRequest;
import com.wegame.capi.client.domain.vo.MariaMakilingNormalBetVo;
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
            .put(GameTypeEnum.MARIA_MAKILING, "mysteriousBetContext")
            .build();

    @Autowired
    Map<String, AbstractBetContext> contextMap;

    public BetResultDo bet(BetDo betDo) {
        GameTypeEnum gameEnum = GameTypeEnum.valueOfCode(betDo.getGameId());
        AssertUtil.assertNotNull(gameEnum, ErrorCode.ERR_PARAM);
        AssertUtil.assertNotNull(betDo.getBetDetail(), ErrorCode.ERR_PARAM);
        String betContextName = contextNameMap.get(gameEnum);
        AssertUtil.assertStrNotEmpty(betContextName, ErrorCode.ERR_PARAM, "Not Support Game");
        AbstractBetContext betContext = contextMap.get(betContextName);
        AssertUtil.assertNotNull(betContext, ErrorCode.ERR_PARAM, "Not Support Game");
        return betContext.bet(betDo);
    }
    //
    //    public MariaMakilingNormalBetVo queryFreeSpinDetail(QueryFreeSpinRequest request) {
    //        // todo fix
    //        GameTypeEnum gameEnum = GameTypeEnum.valueOfCode(request.getGameId());
    //        AssertUtil.assertNotNull(gameEnum, ErrorCode.ERR_PARAM);
    //        String betContextName = contextNameMap.get(gameEnum);
    //        AssertUtil.assertStrNotEmpty(betContextName, ErrorCode.ERR_PARAM, "Not Support Game");
    //        AbstractBetContext betContext = contextMap.get(betContextName);
    //        AssertUtil.assertNotNull(betContext, ErrorCode.ERR_PARAM, "Not Support Game");
    //        return betContext.queryBetFreeSpin(request);
    //    }
}