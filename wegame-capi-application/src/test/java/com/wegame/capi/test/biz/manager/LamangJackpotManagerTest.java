package com.wegame.capi.test.biz.manager;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.manager.jackpot.LamangJackpotManager;
import com.wegame.capi.common.enums.JackpotRewardPoolInputTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

public class LamangJackpotManagerTest  extends BaseTest {
    @Autowired
    @Qualifier("lamangJackpotManager")
    private LamangJackpotManager lamangJackpotManager;


    @Test
    public void batchAddJackpotRewardRecord(){
        Operator operator = Operator.builder()
                .adminId(1L)
                .userId(1L)
                .playerId("IgoTest10020")
                .name("IgoTest10020")
                .build();
        OperatorContext.setOperator(operator);
        List<JackpotRewardPoolInputRecordDo> rewardPoolInputRecordDos = new ArrayList<>();
        rewardPoolInputRecordDos.add(JackpotRewardPoolInputRecordDo.builder()
                .id(111111L)
                .type(JackpotRewardPoolInputTypeEnum.PAYOUT_IN)
                .grandAmount(1000L)
                .minorAmount(2000L)
                .miniAmount(3000L)
                .majorAmount(4000L).build());
        rewardPoolInputRecordDos.add(JackpotRewardPoolInputRecordDo.builder()
                .id(111112L)
                .type(JackpotRewardPoolInputTypeEnum.PAYOUT_IN)
                .grandAmount(1000L)
                .minorAmount(2000L)
                .miniAmount(3000L)
                .majorAmount(4000L).build());
        rewardPoolInputRecordDos.add(JackpotRewardPoolInputRecordDo.builder()
                .id(111113L)
                .type(JackpotRewardPoolInputTypeEnum.PAYOUT_IN)
                .grandAmount(1000L)
                .minorAmount(2000L)
                .miniAmount(3000L)
                .majorAmount(4000L).build());
        lamangJackpotManager.batchAddJackpotRewardRecord(rewardPoolInputRecordDos);
    }

}
