package com.wegame.capi.test.biz.module.base.playerinvention.compant;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.template.BetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.biz.template.lamang.LamangBetRecordTemplateEsDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionResultDo;
import com.wegame.capi.biz.module.base.intervention.component.FgLimitIntervention;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class FgLimitInterventionTest {
    private static final String TAG = "8";
    private static final String RTP_NAME = "fgrtp";
    private static final String CMP_ID = InterventionRuleIdConstants.FG_LIMIT_INTERVENTION;
    private static final InterventionChainIdTypeEnum CHAIN_ID_TYPE = InterventionChainIdTypeEnum.POST_INTERVENTION;

    @Mock
    private RandomService randomService;
    @Spy
    @InjectMocks
    private FgLimitIntervention fgLimitIntervention;

    @Test
    public void processTest_success() throws Exception {
        Map<String, Function> extOperator = new HashMap<>();
        extOperator.put(CMP_ID, this.buildRetryFc());
        PlayerInterventionParamDo paramDo = PlayerInterventionParamDo.builder()
                .userMetricInfoDo(
                        UserMetricInfoDo.builder()
                                .totalBetTimes(200)
                                .build())
                .betType(BetTypeEnum.NORMAL_BET)
                .betAmount(1000)
                .payAmount(1000)
                .betRecordTemplateEsDo(
                        BetRecordTemplateEsDo.builder()
                                .hasFreeGame(true)
                                .multiple(30d)
                                .build())
                .extOperator(extOperator)
                .build();

        FgLimitIntervention.FgLimitInterventionConfigDo config = new FgLimitIntervention.FgLimitInterventionConfigDo();
        config.setSupportBetTypeList(Lists.newArrayList(BetTypeEnum.NORMAL_BET));
        config.setMaxTotalGameCount(200);
        config.setMinTemplateMultiple(new BigDecimal(30));
        config.setTriggerProbability(new BigDecimal("1"));
        config.setRepeatType(RepeatTypeEnum.ALWAYS_REPEAT.getCode());
        config.setRtpName(RTP_NAME);
        config.setInterventionRetryCount(3);
        PlayerInterventionContextDo contextDo = new PlayerInterventionContextDo();



        //        doReturn(10000).when(randomService).randomInt(1,10000);
        doReturn(CHAIN_ID_TYPE.formatChainId(GameTypeEnum.LAM_ANG)).when(fgLimitIntervention).getChainId();
        doReturn(TAG).when(fgLimitIntervention).getTag();
        doReturn(CMP_ID).when(fgLimitIntervention).getNodeId();
        doReturn(paramDo).when(fgLimitIntervention).getRequestData();
        doReturn(config).when(fgLimitIntervention).getCmpData(FgLimitIntervention.FgLimitInterventionConfigDo.class);
        doReturn(contextDo).when(fgLimitIntervention).getContextBean(PlayerInterventionContextDo.class);
        fgLimitIntervention.process();
        log.info("processTest_success param:{},context:{}", JSON.toJSONString(paramDo),JSON.toJSONString(contextDo));
        this.assertIt(contextDo);
    }

    private void assertIt(PlayerInterventionContextDo contextDo) {
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
        assertNotNull(cmpResult.getBetRecordTemplateEsDo());
        assertTrue(cmpResult.getBetRecordTemplateEsDo().getHasFreeGame());
        assertEquals(cmpResult.getBetRecordTemplateEsDo().getMultiple(),29d);
    }

    public static AtomicInteger fcHasRetryCount = new AtomicInteger(0);

    private Function<PlayerInterventionResultDo, LamangBetRecordTemplateEsDo> buildRetryFc() {

        return interventionResultDo -> {
            fcHasRetryCount.addAndGet(1);
            // 不抛异常情况,最后一次成功
            double multiple;
            if (fcHasRetryCount.get() >= 3) {
                multiple = 29d;
            } else {
                multiple = 30d;
            }

            LamangBetRecordTemplateEsDo esDo = new LamangBetRecordTemplateEsDo();
            esDo.setMultiple(multiple);
            esDo.setHasFreeGame(true);
            log.info("buildRetryFc apply,fcHasRetryCount:{},esDo:{}", fcHasRetryCount.get(), JSON.toJSONString(esDo));
            return esDo;
        };

    }


}
