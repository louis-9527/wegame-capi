package com.wegame.capi.test.biz.module.base.playerinvention.compant;

import com.alibaba.fastjson.JSON;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.biz.module.base.playerintervention.compant.RtpHighIntervention;
import com.wegame.capi.biz.service.RandomService;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class RtpHighInterventionTest {
    private static final String TAG="2";
    private static final String RTP_NAME="92rtp";
    private static final String CMP_ID=InterventionRuleIdConstants.RTP_HIGH_INTERVENTION;
    private static final InterventionChainIdTypeEnum CHAIN_ID_TYPE=InterventionChainIdTypeEnum.PRE_INTERVENTION;

    @Mock
    private RandomService randomService;
    @Spy
    @InjectMocks
    private RtpHighIntervention rtpHighIntervention;

    @Test
    public void processTest_success() throws Exception {
        //        doReturn(10000).when(randomService).randomInt(1,10000);
        doReturn(CHAIN_ID_TYPE.formatChainId(GameTypeEnum.LAM_ANG)).when(rtpHighIntervention).getChainId();
        doReturn(TAG).when(rtpHighIntervention).getTag();
        doReturn(CMP_ID).when(rtpHighIntervention).getNodeId();
        PlayerInterventionParamDo paramDo = PlayerInterventionParamDo.builder()
                .userMetricInfoDo(UserMetricInfoDo.builder().totalPayoutAmount(1000).totalBetAmount(1000).build())
                .build();

        RtpHighIntervention.RtpHighInterventionConfigDo config = new RtpHighIntervention.RtpHighInterventionConfigDo();
        config.setMinRtp(new BigDecimal(1));
        config.setTriggerProbability(new BigDecimal(1));
        config.setRepeatType(RepeatTypeEnum.ALWAYS_REPEAT.getCode());
        config.setRtpName(RTP_NAME);
        PlayerInterventionContextDo contextDo = new PlayerInterventionContextDo();
        rtpHighIntervention.doProcess(paramDo, config, contextDo);
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
        assertEquals(cmpResult.getRtpName(), RTP_NAME);
    }


}
