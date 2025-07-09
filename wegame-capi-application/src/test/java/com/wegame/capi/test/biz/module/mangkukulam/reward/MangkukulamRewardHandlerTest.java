package com.wegame.capi.test.biz.module.mangkukulam.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.module.mangkukulam.reward.MangkukulamRewardHandler;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MangkukulamRewardHandlerTest extends BaseTest {
    @Autowired
    MangkukulamRewardHandler mangkukulamRewardHandler;

    @Test
    public void testAll() {
        test_reward_one_special_wild_round1();
        test_reward_one_special_wild_round2();
        test_reward_one_special_wild_round3();

        test_reward_two_special_wild_round1();
        test_reward_two_special_wild_round2();
        test_reward_two_special_wild_round3();

        test_reward_H1();
    }

    /**
     * 正常流程-
     * specialWild 第一轮
     */
    @Test
    public void test_reward_one_special_wild_round1() {
        MangkukulamSymbolDo[][] symbolArr = MangkukulamRewardHandlerTestMocker.mock_one_special_wild(3);
        RewardResultDo<MangkukulamSymbolDo> handleResult = mangkukulamRewardHandler.handle(MangkukulamRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖总倍率，1
        assertEquals(BigDecimal.valueOf(2), handleResult.getTotalMultiple());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(5L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }

    @Test
    public void test_reward_one_special_wild_round2() {
        MangkukulamSymbolDo[][] symbolArr = MangkukulamRewardHandlerTestMocker.mock_one_special_wild(2);
        RewardResultDo<MangkukulamSymbolDo> handleResult = mangkukulamRewardHandler.handle(MangkukulamRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖总倍率，1
        assertEquals(BigDecimal.valueOf(5), handleResult.getTotalMultiple());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(5L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }

    @Test
    public void test_reward_one_special_wild_round3() {
        MangkukulamSymbolDo[][] symbolArr = MangkukulamRewardHandlerTestMocker.mock_one_special_wild(1);
        RewardResultDo<MangkukulamSymbolDo> handleResult = mangkukulamRewardHandler.handle(MangkukulamRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖总倍率，1
        assertEquals(BigDecimal.valueOf(9), handleResult.getTotalMultiple());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(5L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }

    /**
     * 正常流程-
     * specialWild 第一轮
     */
    @Test
    public void test_reward_two_special_wild_round1() {
        MangkukulamSymbolDo[][] symbolArr = MangkukulamRewardHandlerTestMocker.mock_two_special_wild(3);
        RewardResultDo<MangkukulamSymbolDo> handleResult = mangkukulamRewardHandler.handle(MangkukulamRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.MULTIPLE, handleResult.getRewardType());
        // 中奖总倍率，1
        assertEquals(BigDecimal.valueOf(4), handleResult.getTotalMultiple());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(4, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(25L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }

    @Test
    public void test_reward_two_special_wild_round2() {
        MangkukulamSymbolDo[][] symbolArr = MangkukulamRewardHandlerTestMocker.mock_two_special_wild(2);
        RewardResultDo<MangkukulamSymbolDo> handleResult = mangkukulamRewardHandler.handle(MangkukulamRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.MULTIPLE, handleResult.getRewardType());
        // 中奖总倍率，1
        assertEquals(BigDecimal.valueOf(10), handleResult.getTotalMultiple());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(4, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(25L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }

    @Test
    public void test_reward_two_special_wild_round3() {
        MangkukulamSymbolDo[][] symbolArr = MangkukulamRewardHandlerTestMocker.mock_two_special_wild(1);
        RewardResultDo<MangkukulamSymbolDo> handleResult = mangkukulamRewardHandler.handle(MangkukulamRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.MULTIPLE, handleResult.getRewardType());
        // 中奖总倍率，1
        assertEquals(BigDecimal.valueOf(18), handleResult.getTotalMultiple());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(4, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(25L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }

    @Test
    public void test_reward_H1() {
        MangkukulamSymbolDo[][] symbolArr = MangkukulamRewardHandlerTestMocker.mock_reward_H1();
        RewardResultDo<MangkukulamSymbolDo> handleResult = mangkukulamRewardHandler.handle(MangkukulamRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖总倍率，1
        assertEquals(BigDecimal.valueOf(1), handleResult.getTotalMultiple());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(1L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }
}
