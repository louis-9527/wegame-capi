package com.wegame.capi.test.biz.module.base.playerinvention.compant;

import com.alibaba.fastjson.JSON;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.biz.module.base.playerintervention.compant.RetentionUserIntervention;
import com.wegame.capi.biz.service.RandomService;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.constants.InterventionRuleIdConstants;
import com.wegame.capi.common.enums.InterventionChainIdTypeEnum;
import com.wegame.capi.common.enums.RepeatTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.doReturn;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class RetentionUserInterventionTest {
    @Mock
    private RandomService randomService;
    @Spy
    @InjectMocks
    private RetentionUserIntervention retentionUserIntervention;

    @Test
    public void  processTest_success() throws Exception {
//        doReturn(10000).when(randomService).randomInt(1,10000);
        doReturn(InterventionChainIdTypeEnum.PRE_INTERVENTION.formatChainId(GameTypeEnum.LAM_ANG)).when(retentionUserIntervention).getChainId();
        doReturn("5").when(retentionUserIntervention).getTag();
        doReturn(InterventionRuleIdConstants.RETENTION_USER_INTERVENTION).when(retentionUserIntervention).getNodeId();

        PlayerInterventionParamDo paramDo = PlayerInterventionParamDo.builder()
                .userMetricInfoDo(UserMetricInfoDo.builder().yesterdayLoginTime(DateUtil.getYesterdayBegin()).totalBetTimes(0).build())
                .build();

        RetentionUserIntervention.RetentionUserInterventionConfigDo config = new RetentionUserIntervention.RetentionUserInterventionConfigDo();
        config.setMaxTotalGameCount(200);
        config.setTriggerProbability(new BigDecimal(1));
        config.setRepeatType(RepeatTypeEnum.ALWAYS_REPEAT.getCode());
        config.setRtpName("98rtp");

        PlayerInterventionContextDo contextDo = new PlayerInterventionContextDo();
        retentionUserIntervention.doProcess(paramDo, config, contextDo);
        log.info("processTest_success param:{},context:{}", JSON.toJSONString(paramDo),JSON.toJSONString(contextDo));
        this.assertIt(contextDo);
    }

    private void  assertIt(PlayerInterventionContextDo contextDo){
        Assertions.assertNotNull(contextDo.getPreCmpResult());
//        assertNull(contextDo.getCurrentCmpResultPoint());
        Assertions.assertNotNull(contextDo.getChainInterventionResultMap());
        PlayerInterventionResultDo cmpResult = contextDo.getChainResult(InterventionRuleIdConstants.RETENTION_USER_INTERVENTION);
        Assertions.assertNotNull(cmpResult);
        Assertions.assertEquals(cmpResult.getChainId(), InterventionChainIdTypeEnum.PRE_INTERVENTION.formatChainId(GameTypeEnum.LAM_ANG));
        Assertions.assertEquals(cmpResult.getChainSort() + "", InterventionChainIdTypeEnum.PRE_INTERVENTION.getDefaultSort() + "");
        Assertions.assertEquals(cmpResult.getCmpSort() + "", "5");
        Assertions.assertEquals(InterventionRuleIdConstants.RETENTION_USER_INTERVENTION, cmpResult.getCmpId());
        Assertions.assertEquals(cmpResult.getRtpName(), "98rtp");
    }



}
