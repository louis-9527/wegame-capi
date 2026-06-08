package com.wegame.capi.biz.service.intervention;

import com.alibaba.fastjson.JSON;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.InterventionChainIdTypeEnum;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 玩家干预服务
 */
@Slf4j
@Service
public class PlayerInterventionService {
    @Resource
    private FlowExecutor flowExecutor;

    public PlayerInterventionResultDo preIntervention(GameTypeEnum gameType, PlayerInterventionParamDo param, final PlayerInterventionContextDo contextDo) {
        return this.intervention(gameType, InterventionChainIdTypeEnum.PRE_INTERVENTION, param, contextDo);
    }

    public PlayerInterventionResultDo postIntervention(GameTypeEnum gameType, PlayerInterventionParamDo param, final PlayerInterventionContextDo contextDo) {
        return this.intervention(gameType, InterventionChainIdTypeEnum.POST_INTERVENTION, param, contextDo);
    }

    public PlayerInterventionResultDo intervention(GameTypeEnum gameType, InterventionChainIdTypeEnum chainIdType, final PlayerInterventionParamDo param, PlayerInterventionContextDo contextDo) {
        log.info("PlayerInterventionService#{} start.param:{},context:{}", chainIdType.formatChainId(gameType), JSON.toJSONString(param), JSON.toJSONString(contextDo));
        AssertUtil.assertNotNull(gameType, ErrorCode.ERR_INTER, "gameType is null");
        AssertUtil.assertNotNull(param, ErrorCode.ERR_INTER, "param is null");
        AssertUtil.assertNotNull(contextDo, ErrorCode.ERR_INTER, "contextDo is null");
        LiteflowResponse liteflowResponse = flowExecutor.execute2Resp(chainIdType.formatChainId(gameType), param, contextDo);
        AssertUtil.assertNotNull(liteflowResponse, ErrorCode.ERR_INTER, "liteflowResponse is null");
        AssertUtil.assertTrue(liteflowResponse.isSuccess(), ErrorCode.ERR_INTER, "liteflowResponse is not success");
        log.info("PlayerInterventionService#{} end.param:{},context:{}", chainIdType.formatChainId(gameType), JSON.toJSONString(param), JSON.toJSONString(contextDo));
        return contextDo.getChainResult();
    }

}
