package com.wegame.capi.test.biz.module.base.playerinvention.compant;

import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.biz.module.base.playerintervention.compant.NewUserEmptyIntervention;
import com.wegame.capi.biz.module.base.playerintervention.compant.ReturnUserHelpIntervention;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.wegame.capi.common.enums.InterventionChainIdTypeEnum;
import com.wegame.capi.common.enums.RepeatTypeEnum;
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

@ExtendWith(MockitoExtension.class)
public class NewUserEmptyInterventionTest {
    private static final String TAG="4";
    private static final String RTP_NAME="96rtp";
    private static final String CMP_ID= InterventionRuleIdConstants.NEW_USER_EMPTY_INTERVENTION;
    private static final InterventionChainIdTypeEnum CHAIN_ID_TYPE=InterventionChainIdTypeEnum.POST_INTERVENTION;

    @Mock
    private RandomService randomService;
    @Spy
    @InjectMocks
    private NewUserEmptyIntervention newUserEmptyIntervention;

    @Test
    public void processTest_success() throws Exception {
        doReturn(100).when(randomService).randomInt(1,10000);
        doReturn(CHAIN_ID_TYPE.formatChainId(GameTypeEnum.LAM_ANG)).when(newUserEmptyIntervention).getChainId();
        doReturn(TAG).when(newUserEmptyIntervention).getTag();
        doReturn(CMP_ID).when(newUserEmptyIntervention).getNodeId();
        PlayerInterventionParamDo paramDo = PlayerInterventionParamDo.builder()
                .userMetricInfoDo(
                        UserMetricInfoDo.builder()
                                .totalBetTimes(99)
                                .continueEmptyTimes(5)
                                .build())
                .betType(BetTypeEnum.NORMAL_BET)
                .betAmount(1000)
                .payAmount(1000)
                .betRecordTemplateEsDo(BetRecordTemplateEsDo.builder().multiple(0d).build())
                .build();

        NewUserEmptyIntervention.NewUserEmptyInterventionConfigDo config = new NewUserEmptyIntervention.NewUserEmptyInterventionConfigDo();
        config.setMaxTotalGameCount(100);
        config.setMinContinuousEmptyCount(5);
        config.setTriggerProbability(new BigDecimal("0.5"));
        config.setRepeatType(RepeatTypeEnum.ALWAYS_REPEAT.getCode());
        config.setInterventionTemplateRange("0_1");
        PlayerInterventionContextDo contextDo = new PlayerInterventionContextDo();
        newUserEmptyIntervention.doProcess(paramDo, config, contextDo);
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
        assertEquals(CMP_ID, cmpResult.getComponentId());
        assertNotNull(cmpResult.getTemplateMultipleRange());

    }




}
