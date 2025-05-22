package com.wegame.capi.test.biz.manager;

import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.manager.jackpot.LamangJackpotManager;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class LamangJackpotManagerTest  extends BaseTest {
    @Autowired
    private LamangJackpotManager lamangJackpotManager;

    @Test
    public void batchAddJackpotRewardRecord(){
        List<JackpotRewardPoolInputRecordDo> rewardPoolInputRecordDos = new ArrayList<>();
        rewardPoolInputRecordDos.add(JackpotRewardPoolInputRecordDo.builder()
                .id(111111L)
                .grandAmount(1000L)
                .minorAmount(2000L)
                .miniAmount(3000L)
                .majorAmount(4000L).build());
        rewardPoolInputRecordDos.add(JackpotRewardPoolInputRecordDo.builder()
                .id(111112L)
                .grandAmount(1000L)
                .minorAmount(2000L)
                .miniAmount(3000L)
                .majorAmount(4000L).build());
        rewardPoolInputRecordDos.add(JackpotRewardPoolInputRecordDo.builder()
                .id(111113L)
                .grandAmount(1000L)
                .minorAmount(2000L)
                .miniAmount(3000L)
                .majorAmount(4000L).build());
        List<Long> longs = lamangJackpotManager.batchAddJackpotRewardRecord(rewardPoolInputRecordDos);
        System.out.printf(longs.toString());
    }

}
