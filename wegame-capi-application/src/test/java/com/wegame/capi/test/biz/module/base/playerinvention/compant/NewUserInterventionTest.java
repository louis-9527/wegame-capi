package com.wegame.capi.test.biz.module.base.playerinvention.compant;

import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.biz.module.base.playerintervention.compant.NewUserIntervention;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.wegame.capi.common.enums.InterventionChainIdTypeEnum;
import com.wegame.capi.common.enums.RepeatTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class NewUserInterventionTest {
    @Mock
    private RandomService randomService;
    @InjectMocks
    private NewUserIntervention newUserIntervention;

    @BeforeEach
    public void before() {
        newUserIntervention = spy(NewUserIntervention.class);
    }

    @Test
    public void processTest_success() throws Exception {
        doReturn(10000).when(randomService).randomInt(anyInt(),anyInt());
        doReturn(InterventionChainIdTypeEnum.PRE_INTERVENTION.formatChainId(GameTypeEnum.LAM_ANG)).when(newUserIntervention).getChainId();
        doReturn("7").when(newUserIntervention).getTag();
        doReturn(InterventionRuleIdConstants.NEW_USER_INTERVENTION).when(newUserIntervention).getNodeId();

        PlayerInterventionParamDo paramDo = PlayerInterventionParamDo.builder()
                .userMetricInfoDo(UserMetricInfoDo.builder().registerTime(new Date()).totalBetTimes(0).build())
                .build();
        NewUserIntervention.NewUserInterventionConfigDo config = new NewUserIntervention.NewUserInterventionConfigDo();
        config.setToDayRegister(true);
        config.setMaxTotalGameCount(100);
        config.setTriggerProbability(new BigDecimal(1));
        config.setRepeatType(RepeatTypeEnum.ALWAYS_REPEAT.getCode());
        config.setRtpName("98rtp");
        PlayerInterventionContextDo contextDo = new PlayerInterventionContextDo();
        newUserIntervention.doProcess(paramDo, config, contextDo);
        Assertions.assertNotNull(contextDo.getPreCmpResult());
//        assertNull(contextDo.getCurrentCmpResultPoint());
        Assertions.assertNotNull(contextDo.getChainInterventionResultMap());
        PlayerInterventionResultDo cmpResult = contextDo.getChainResult(InterventionRuleIdConstants.NEW_USER_INTERVENTION);
        Assertions.assertNotNull(cmpResult);
        Assertions.assertEquals(cmpResult.getChainId(), InterventionChainIdTypeEnum.PRE_INTERVENTION.formatChainId(GameTypeEnum.LAM_ANG));
        Assertions.assertEquals(cmpResult.getChainSort() + "", InterventionChainIdTypeEnum.PRE_INTERVENTION.getDefaultSort() + "");
        Assertions.assertEquals(cmpResult.getCmpSort() + "", "7");
        Assertions.assertEquals(InterventionRuleIdConstants.NEW_USER_INTERVENTION, cmpResult.getComponentId());
        Assertions.assertEquals(cmpResult.getRtpName(), "98rtp");


    }


}
