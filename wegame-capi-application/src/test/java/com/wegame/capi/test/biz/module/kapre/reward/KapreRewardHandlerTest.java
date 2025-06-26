package com.wegame.capi.test.biz.module.kapre.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.KapreSymbolDo;
import com.wegame.capi.biz.module.kapre.reward.KapreRewardHandler;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KapreRewardHandlerTest extends BaseTest {
    @Autowired
    KapreRewardHandler kapreRewardHandler;

    /**
     * 中一条线 - 1001 3连
     */
    @Test
    public void handle_normal_reward_1001_test() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_reward_1001();
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
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
    public void handle_normal_reward_cash_test() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_reward_cash();
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
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
     * 中一条线 - cash + wild 3连  20倍
     */
    @Test
    public void handle_normal_reward_cash_wild_test() {
        KapreSymbolDo[][] symbolArr = KapreRewardHandlerTestMocker.mockSymbolArr_reward_cash_wild();
        RewardResultDo<KapreSymbolDo> handleResult = kapreRewardHandler.handle(KapreRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(1L, (long) handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }

}
