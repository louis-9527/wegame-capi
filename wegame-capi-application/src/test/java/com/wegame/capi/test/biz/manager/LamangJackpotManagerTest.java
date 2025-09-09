package com.wegame.capi.test.biz.manager;

import com.mongodb.DuplicateKeyException;
import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.capi.biz.domain.biz.jackpot.JackpotRewardPoolInputRecordDo;
import com.wegame.capi.biz.manager.jackpot.LamangJackpotManager;
import com.wegame.capi.common.enums.JackpotRewardPoolInputTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class LamangJackpotManagerTest extends BaseTest {
    @Autowired
    @Qualifier("lamangJackpotManager")
    private LamangJackpotManager lamangJackpotManager;


    @Test
    public void batchAddJackpotRewardRecord() {
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

        try {
            lamangJackpotManager.batchAddRewardPoolInputRecord(rewardPoolInputRecordDos);
        } catch (Exception e) {
            List<Long> failList = new ArrayList<>();
            rewardPoolInputRecordDos.forEach(record -> {
                try {
                    lamangJackpotManager.batchAddRewardPoolInputRecord(Arrays.asList(record));
                } catch (DuplicateKeyException t) {
                    log.warn("Error add user reward record, record: {}, error: {}", record, t.getErrorMessage());
                } catch (Exception t) {
                    failList.add(record.getId());
                    log.error("Error add user reward record", t);
                }

            });

        }

    }

}
