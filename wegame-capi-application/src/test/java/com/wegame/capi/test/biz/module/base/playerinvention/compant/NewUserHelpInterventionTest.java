package com.wegame.capi.test.biz.module.base.playerinvention.compant;

import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.biz.module.base.playerintervention.compant.NewUserHelpIntervention;
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
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class NewUserHelpInterventionTest {
    private static final String TAG="11";
    private static final String RTP_NAME="96rtp";
    private static final String CMP_ID= InterventionRuleIdConstants.NEW_USER_HELP_INTERVENTION;
    private static final InterventionChainIdTypeEnum CHAIN_ID_TYPE=InterventionChainIdTypeEnum.PRE_INTERVENTION;

    @Mock
    private RandomService randomService;
    @Spy
    @InjectMocks
    private NewUserHelpIntervention newUserHelpIntervention;

    @Test
    public void processTest_success() throws Exception {
        doReturn(100).when(randomService).randomInt(1,10000);
        doReturn(CHAIN_ID_TYPE.formatChainId(GameTypeEnum.LAM_ANG)).when(newUserHelpIntervention).getChainId();
        doReturn(TAG).when(newUserHelpIntervention).getTag();
        doReturn(CMP_ID).when(newUserHelpIntervention).getNodeId();
        PlayerInterventionParamDo paramDo = PlayerInterventionParamDo.builder()
                .userMetricInfoDo(
                        UserMetricInfoDo.builder()
                                .registerTime(new Date())
                                .totalBetTimes(100)
                                .todayBetTimes(20)
                                .currentBalance(1000L)
                                .enterBalance(10000L)
                                .totalBetAmount(1000)
                                .totalPayoutAmount(800)
                                .build())
                .betType(BetTypeEnum.NORMAL_BET)
                .betAmount(1000)
                .payAmount(1000)
                .build();

        NewUserHelpIntervention.NewUserHelpInterventionConfigDo config = new NewUserHelpIntervention.NewUserHelpInterventionConfigDo();
        config.setMaxTotalGameCount(100);
        config.setMinTodayGameCount(20);
        config.setBetAmountCoefficient(new BigDecimal(5));
        config.setMaxRtp(new BigDecimal("0.8"));
        config.setTriggerProbability(new BigDecimal("0.05"));
        config.setRepeatType(RepeatTypeEnum.NEVER_REPEAT.getCode());
        config.setInterventionMultipleCoefficient(new BigDecimal("0.008"));
        config.setBackstopRtpName(RTP_NAME);
        config.setMultipleBias(new BigDecimal("0.10"));
        PlayerInterventionContextDo contextDo = new PlayerInterventionContextDo();
        newUserHelpIntervention.doProcess(paramDo, config, contextDo);
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
        assertEquals(cmpResult.getBackstopRtpName(), RTP_NAME);
        assertNotNull(cmpResult.getTemplateMultipleRange());

    }


}
