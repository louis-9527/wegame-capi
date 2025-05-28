package com.wegame.capi.test.biz.module.lamang.reward;

import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.LamangSymbolDo;
import com.wegame.capi.biz.module.lamang.reward.LamangRewardHandler;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.*;

public class LamangRewardHandlerTest extends BaseTest {
    @Autowired
    LamangRewardHandler lamangRewardHandler;

    /**
     * 正常流程-
     * 中一条线-
     * 第一回合
     */
    @Test
    public void handle_normal_oneLine_theFirstWheelIndex_test() {
        LamangSymbolDo[][] symbolArr = LamangRewardHandlerTestMocker.mockSymbolArr_handle_normal_oneLine();
        RewardResultDo<LamangSymbolDo> handleResult = lamangRewardHandler.handle(LamangRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 0, false, 1000L, null));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖总倍率，1
        assertEquals(BigDecimal.ONE.setScale(1, RoundingMode.FLOOR), handleResult.getTotalMultiple());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(1L, (long)handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }


    /**
     * 正常流程-
     * 中一条线-
     * 第二回合
     */
    @Test
    public void handle_normal_oneLine_theSecondWheelIndex_test() {
        LamangSymbolDo[][] symbolArr = LamangRewardHandlerTestMocker.mockSymbolArr_handle_normal_oneLine();
        RewardResultDo<LamangSymbolDo> handleResult = lamangRewardHandler.handle(LamangRewardHandlerTestMocker.buildTestRewardHandleDo(symbolArr, 1, false, 1000L, BigDecimal.ONE));
        assertNotNull(handleResult);
        // 中奖类型，单奖励
        assertEquals(RewardTypeEnum.SINGLE, handleResult.getRewardType());
        // 中奖总倍率，2
        assertEquals(new BigDecimal("2.0").setScale(1, RoundingMode.FLOOR), handleResult.getTotalMultiple());
        // 中奖线列表,1
        assertNotNull(handleResult.getRewardLineDetailList());
        assertEquals(1, handleResult.getRewardLineDetailList().size());
        // 中奖详情中奖数量，1
        assertEquals(1L, (long)handleResult.getRewardLineDetailList().get(0).getRewardLineCount());
    }




}
