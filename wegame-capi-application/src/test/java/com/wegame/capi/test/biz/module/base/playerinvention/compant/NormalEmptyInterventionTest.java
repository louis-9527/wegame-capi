package com.wegame.capi.test.biz.module.base.playerinvention.compant;

import com.alibaba.fastjson.JSON;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.biz.module.base.playerintervention.compant.NormalEmptyIntervention;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.wegame.capi.common.enums.InterventionChainIdTypeEnum;
import com.wegame.capi.common.enums.RepeatTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
@Slf4j
@ExtendWith(MockitoExtension.class)
public class NormalEmptyInterventionTest {
    private static final String TAG="3";
    private static final String RTP_NAME="96rtp";
    private static final String CMP_ID= InterventionRuleIdConstants.NORMAL_EMPTY_INTERVENTION;
    private static final InterventionChainIdTypeEnum CHAIN_ID_TYPE=InterventionChainIdTypeEnum.POST_INTERVENTION;

    @Mock
    private RandomService randomService;
    @Spy
    @InjectMocks
    private NormalEmptyIntervention normalEmptyIntervention;

    @Test
    public void processTest_success() throws Exception {
        doReturn(100).when(randomService).randomInt(1,10000);
        doReturn(CHAIN_ID_TYPE.formatChainId(GameTypeEnum.LAM_ANG)).when(normalEmptyIntervention).getChainId();
        doReturn(TAG).when(normalEmptyIntervention).getTag();
        doReturn(CMP_ID).when(normalEmptyIntervention).getNodeId();
        PlayerInterventionParamDo paramDo = PlayerInterventionParamDo.builder()
                .userMetricInfoDo(
                        UserMetricInfoDo.builder()
                                .totalBetTimes(100)
                                .continueEmptyTimes(6)
                                .build())
                .betType(BetTypeEnum.NORMAL_BET)
                .betAmount(1000)
                .payAmount(1000)
                .betRecordTemplateEsDo(BetRecordTemplateEsDo.builder().multiple(0d).build())
                .build();

        NormalEmptyIntervention.NormalEmptyInterventionConfigDo config = new NormalEmptyIntervention.NormalEmptyInterventionConfigDo();
        config.setMinTotalGameCount(100);
        config.setMinContinuousEmptyCount(6);
        config.setTriggerProbability(new BigDecimal("0.5"));
        config.setRepeatType(RepeatTypeEnum.ALWAYS_REPEAT.getCode());
        config.setInterventionTemplateRange("0_1");
        PlayerInterventionContextDo contextDo = new PlayerInterventionContextDo();
        normalEmptyIntervention.doProcess(paramDo, config, contextDo);
        log.info("processTest_success param:{},context:{}", JSON.toJSONString(paramDo),JSON.toJSONString(contextDo));
        this.assertIt(contextDo);
    }

    private void  assertIt(PlayerInterventionContextDo contextDo){
        assertNotNull(contextDo.getPreCmpResult());
//        assertNull(contextDo.getCurrentCmpResultPoint());
        assertNotNull(contextDo.getChainInterventionResultMap());
        PlayerInterventionResultDo cmpResult = contextDo.getChainResult(CMP_ID);
        assertNotNull(cmpResult);
        assertEquals(cmpResult.getChainId(), CHAIN_ID_TYPE.formatChainId(GameTypeEnum.LAM_ANG));
        assertEquals(cmpResult.getChainSort() + "", CHAIN_ID_TYPE.getDefaultSort() + "");
        assertEquals(cmpResult.getCmpSort() + "", TAG);
        assertEquals(CMP_ID, cmpResult.getCmpId());
        assertNotNull(cmpResult.getTemplateMultipleRange());

    }



}
