package com.wegame.capi.test.biz.module.base.playerinvention.compant;

import com.wegame.capi.biz.domain.intervention.PlayerInterventionParamDo;
import com.wegame.capi.biz.module.base.playerintervention.compant.NewUserIntervention;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class NewUserInterventionTest extends BaseTest {
    @Autowired
    private NewUserIntervention newUserIntervention;

    @Test
    public void processTest(){

//        newUserIntervention.doProcess(PlayerInterventionParamDo.buildDo(userMetricInfoDo, betRecordTemplateEsDo, BetTypeEnum.BET_TYPE_NORMAL, 1000));


    }



}
