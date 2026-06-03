package com.wegame.capi.biz.service.intervention;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.InterventionChainIdTypeEnum;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 玩家干预服务
 */
@Service
public class PlayerInterventionService {
    @Resource
    private FlowExecutor flowExecutor;

    public PlayerInterventionResultDo preIntervention(GameTypeEnum gameType, PlayerInterventionParamDo param,final PlayerInterventionContextDo contextDo) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER, "gameType is null");
        AssertUtil.assertNotNull(param, ErrorCode.ERR_INTER, "param is null");
        AssertUtil.assertNotNull(contextDo, ErrorCode.ERR_INTER, "contextDo is null");
        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp(InterventionChainIdTypeEnum.PRE_INTERVENTION.formatChainId(gameType), param, contextDo);
        AssertUtil.assertNotNull(liteflowResponse, ErrorCode.ERR_INTER, "liteflowResponse is null");
        AssertUtil.assertTrue(liteflowResponse.isSuccess(), ErrorCode.ERR_INTER, "liteflowResponse is not success");
        PlayerInterventionContextDo context = liteflowResponse.getFirstContextBean();
        return context.getChainResult();
    }

    public PlayerInterventionResultDo postIntervention(GameTypeEnum gameType, PlayerInterventionParamDo param, final PlayerInterventionContextDo contextDo) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER, "gameType is null");
        AssertUtil.assertNotNull(param, ErrorCode.ERR_INTER, "param is null");
        AssertUtil.assertNotNull(contextDo, ErrorCode.ERR_INTER, "contextDo is null");
        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp(InterventionChainIdTypeEnum.PRE_INTERVENTION.formatChainId(gameType), param, contextDo);
        AssertUtil.assertNotNull(liteflowResponse, ErrorCode.ERR_INTER, "liteflowResponse is null");
        AssertUtil.assertTrue(liteflowResponse.isSuccess(), ErrorCode.ERR_INTER, "liteflowResponse is not success");
        return contextDo.getChainResult();
    }

    @Deprecated
    public PlayerInterventionResultDo intervention(GameTypeEnum gameType, InterventionChainIdTypeEnum chainIdType, final PlayerInterventionParamDo param, PlayerInterventionContextDo contextDo) {
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER, "gameType is null");
        AssertUtil.assertNotNull(param, ErrorCode.ERR_INTER, "param is null");
        contextDo = contextDo == null ? new PlayerInterventionContextDo() : contextDo;
        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp(chainIdType.formatChainId(gameType), param, contextDo == null ? new PlayerInterventionContextDo() : contextDo);
        AssertUtil.assertNotNull(liteflowResponse, ErrorCode.ERR_INTER, "liteflowResponse is null");
        AssertUtil.assertTrue(liteflowResponse.isSuccess(), ErrorCode.ERR_INTER, "liteflowResponse is not success");
        return contextDo.getChainResult();
    }

}
