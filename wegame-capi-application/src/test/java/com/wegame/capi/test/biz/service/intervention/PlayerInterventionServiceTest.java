package com.wegame.capi.test.biz.service.intervention;

import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionContextDo;
import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.module.base.intervention.component.NewUserIntervention;
import com.wegame.capi.biz.service.intervention.PlayerInterventionService;
import com.wegame.capi.common.enums.RepeatTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
public class PlayerInterventionServiceTest extends BaseTest {
    @Autowired
    private PlayerInterventionService playerInterventionService;

    // 新用户
    @Test
    public void test_new_user_intervention() {
        PlayerInterventionParamDo paramDo = PlayerInterventionParamDo.builder()
                .userMetricInfoDo(UserMetricInfoDo.builder().registerTime(new Date()).totalBetTimes(0).build())
                .build();
        NewUserIntervention.NewUserInterventionConfigDo config = new NewUserIntervention.NewUserInterventionConfigDo();
//        config.setToDayRegister(true);
        config.setMaxTotalGameCount(100);
        config.setTriggerProbability(new BigDecimal(1));
        config.setRepeatType(RepeatTypeEnum.ALWAYS_REPEAT.getCode());
        config.setRtpName("98rtp");
        PlayerInterventionContextDo contextDo = new PlayerInterventionContextDo();


//        playerInterventionService.preIntervention(GameTypeEnum.LAMANG, paramDo, contextDo)

    }






}
