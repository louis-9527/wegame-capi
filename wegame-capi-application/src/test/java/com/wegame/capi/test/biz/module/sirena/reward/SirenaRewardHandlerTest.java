package com.wegame.capi.test.biz.module.sirena.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SirenaSymbolDo;
import com.wegame.capi.biz.module.sirena.reward.SirenaRewardHandler;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SirenaRewardHandlerTest extends BaseTest {
    @Autowired
    SirenaRewardHandler sirenaRewardHandler;

    /**
     * 正常流程-
     * 中一条线-
     * 无免费轮
     */
    @Test
    public void handle_normal_oneLine_theFirstWheelIndex_test() {
        SirenaSymbolDo[][] symbolArr = SirenaRewardHandlerTestMocker.mockSymbolArr_handle_normal_oneLine();
        RewardResultDo<SirenaSymbolDo> handleResult = sirenaRewardHandler.handle(SirenaRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, false, 1000L));
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
     * 正常流程-
     * 中一条线-
     * 中2个小珍珠-
     * 无免费轮
     */
    @Test
    public void handle_normal_oneLine_theSecondWheelIndex_test() {
        SirenaSymbolDo[][] symbolArr = SirenaRewardHandlerTestMocker.mockSymbolArr_handle_normal_smallPearl();
        RewardResultDo<SirenaSymbolDo> handleResult = sirenaRewardHandler.handle(SirenaRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, false, 1000L));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(3L, (long)handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }
}
