package com.wegame.capi.test.biz.module.kapre.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
import com.wegame.capi.biz.module.kapre.reward.KapreRewardHandler;
import com.wegame.capi.common.enums.RewardLineSubTypeEnum;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class KapreRewardHandlerTest extends BaseTest {
    @Autowired
    KapreRewardHandler kapreRewardHandler;

    private static final long BET_AMOUNT = 1000L;


    @Test
    public void testRewardHandler() {
        test_reward_none();
        test_reward_1001();
        test_reward_1001_dynamic_wild();
        test_reward_cash();
        test_reward_cash_dynamic_wild();
        test_reward_cash_wild();
        test_reward_multi();
        test_reward_cash_and_freewheel();
    }

    /**
     * 未中奖
     */
    @Test
    public void test_reward_none() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_non_reward();
        RewardHandleDo<KapreSymbolDo> rewardHandle = KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, BET_AMOUNT, null);
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(rewardHandle);
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.NONE, handleResult.getRewardType());
        // 中奖金额 ： 0
        assertEquals(0, handleResult.getTotalRewardAmount().longValue());
    }

    /**
     * 中一条线 - 1001 3连
     */
    @Test
    public void test_reward_1001() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_reward_1001();
        RewardHandleDo<KapreSymbolDo> rewardHandle = KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, BET_AMOUNT, null);
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(rewardHandle);
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(1L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }

    /**
     * 中一条线 - 1001 带dynamicWild 3连
     */
    @Test
    public void test_reward_1001_dynamic_wild() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_reward_1001_dynamic_wild();
        RewardHandleDo<KapreSymbolDo> rewardHandle = KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, BET_AMOUNT, null);
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(rewardHandle);
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(1L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }

    /**
     * 中一条线 - cash 3连  10倍
     */
    @Test
    public void test_reward_cash() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_reward_cash();
        RewardHandleDo<KapreSymbolDo> rewardHandle = KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, BET_AMOUNT, null);
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(rewardHandle);

        long odds = 10L;
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());

        // 中奖金额 ：10000
        assertEquals(BET_AMOUNT * odds, handleResult.getTotalRewardAmount().longValue());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(1L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
        // 中奖类型为现金中奖
        assertEquals(RewardLineSubTypeEnum.CASH, handleResult.getRewardLineDetailList().get(0).getRewardLineSubType());
        // 现金中奖赔率 ： 10
        assertEquals(BigDecimal.valueOf(odds), handleResult.getRewardLineDetailList().get(0).getOdds());
    }

    /**
     * 中一条线 - cash 带dynamicWild 3连   10倍
     */
    @Test
    public void test_reward_cash_dynamic_wild() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_reward_cash_dynamic_wild();
        RewardHandleDo<KapreSymbolDo> rewardHandle = KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, BET_AMOUNT, null);
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(rewardHandle);

        long odds = 11L;
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());

        // 中奖金额 ：10000
        assertEquals(BET_AMOUNT * odds, handleResult.getTotalRewardAmount().longValue());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(1L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
        // 中奖类型为现金中奖
        assertEquals(RewardLineSubTypeEnum.CASH, handleResult.getRewardLineDetailList().get(0).getRewardLineSubType());
        // 现金中奖赔率 ： 10
        assertEquals(BigDecimal.valueOf(odds), handleResult.getRewardLineDetailList().get(0).getOdds());
    }

    /**
     * 中一条线 - cash + wild 3连  50倍
     */
    @Test
    public void test_reward_cash_wild() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_reward_cash_wild();
        RewardHandleDo<KapreSymbolDo> rewardHandle = KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, BET_AMOUNT, null);
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(rewardHandle);
        assertNotNull(handleResult);
        long odds = 50L;
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖金额 ：20000
        assertEquals(BET_AMOUNT * odds, handleResult.getTotalRewardAmount().longValue());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，2
        assertEquals(2L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
        // 中奖类型为现金中奖
        assertEquals(RewardLineSubTypeEnum.CASH, handleResult.getRewardLineDetailList().get(0).getRewardLineSubType());
        // 现金中奖赔率 ： 50
        assertEquals(BigDecimal.valueOf(odds), handleResult.getRewardLineDetailList().get(0).getOdds());
    }

    /**
     * 多中奖线 cash + 1001
     */
    @Test
    public void test_reward_multi() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_reward_cash_wild_and_normal();
        RewardHandleDo<KapreSymbolDo> rewardHandle = KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, BET_AMOUNT, null);
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(rewardHandle);
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.MULTIPLE, handleResult.getRewardType());
        // 中奖线列表,3
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(3, handleResult.getRewardLineDetailList().size());
    }

    /**
     * 单中奖线 cash 10倍
     * 3个scatter 中免费轮
     */
    @Test
    public void test_reward_cash_and_freewheel() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_reward_cash_and_freeWheel();
        RewardHandleDo<KapreSymbolDo> rewardHandle = KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, BET_AMOUNT, null);
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(rewardHandle);
        assertNotNull(handleResult);
        long odds = 10L;
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());

        // 中奖金额 ：10000
        assertEquals(BET_AMOUNT * odds, handleResult.getTotalRewardAmount().longValue());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(1L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
        // 中奖类型为现金中奖
        assertEquals(RewardLineSubTypeEnum.CASH, handleResult.getRewardLineDetailList().get(0).getRewardLineSubType());
        // 现金中奖赔率 ： 10
        assertEquals(BigDecimal.valueOf(odds), handleResult.getRewardLineDetailList().get(0).getOdds());

        // 命中免费轮
        assertTrue(handleResult.isHasRewardFreeWheel());
    }

}
