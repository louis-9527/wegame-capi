package com.wegame.capi.test.biz.module.diwatamayari.reward;

import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.module.diawatamayari.reward.DiwataMayariRewardHandler;
import com.wegame.capi.common.enums.RewardTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.*;
@Slf4j
public class DiwataMayariRewardHandlerTest extends BaseTest {
    @Autowired
    private DiwataMayariRewardHandler diwataMayariRewardHandler;
    @Autowired
    private DiwataMayariConfig diwataMayariConfig;
    private DiwataMayariConfig.FreeWheelTimesRuleConfig freeWheelTimesRuleConfig;

    @Before
    public void before() {
        freeWheelTimesRuleConfig = diwataMayariConfig.getFreeWheelTimesRuleConfig();
    }

    /**
     * 中奖线数量测试
     * case1:不中
     * case2:中一个
     * case3:中多个-8
     * case4:中全部
     */
    @Test
    public void handler_normal_somLines_test() {
        // case1:不中
        DiwataMayariSymbolDo[][] diwataMayariSymbolDos = DiwataMayariRewardHandlerTestMocker.buildSymbolArr_zeroLine_test();
        RewardResultDo<DiwataMayariSymbolDo> zeroLineResult = diwataMayariRewardHandler.handle(DiwataMayariRewardHandlerTestMocker.buildRewardHandleDo(diwataMayariSymbolDos, 1000L, 0, false, BigDecimal.ONE));
        assertNotNull(zeroLineResult);
        zeroLineResult.print();
        assertEquals(RewardTypeEnum.NONE, zeroLineResult.getRewardType());
        assertNull(zeroLineResult.getRewardLineDetailList());

        // case2:中一个
        DiwataMayariSymbolDo[][] diwataMayariSymbolDos_one = DiwataMayariRewardHandlerTestMocker.buildSymbolArr_oneLine_test();
        RewardResultDo<DiwataMayariSymbolDo> zeroLineResult_one = diwataMayariRewardHandler.handle(DiwataMayariRewardHandlerTestMocker.buildRewardHandleDo(diwataMayariSymbolDos_one, 1000L, 0, false, BigDecimal.ONE));
        assertNotNull(zeroLineResult_one);
        zeroLineResult_one.print();
        assertEquals(RewardTypeEnum.SINGLE, zeroLineResult_one.getRewardType());
        assertNotNull(zeroLineResult_one.getRewardLineDetailList());
        assertEquals(1, zeroLineResult_one.getRewardLineDetailList().size());
        assertEquals(1, (long) zeroLineResult_one.getRewardLineDetailList().get(0).getRewardLineCount());

        // case3:中多个-8
        DiwataMayariSymbolDo[][] diwataMayariSymbolDos_eight = DiwataMayariRewardHandlerTestMocker.buildSymbolArr_eightLine_test();
        RewardResultDo<DiwataMayariSymbolDo> zeroLineResult_eight = diwataMayariRewardHandler.handle(DiwataMayariRewardHandlerTestMocker.buildRewardHandleDo(diwataMayariSymbolDos_eight, 1000L, 0, false, BigDecimal.ONE));
        assertNotNull(zeroLineResult_eight);
        zeroLineResult_eight.print();
        assertEquals(RewardTypeEnum.SINGLE, zeroLineResult_eight.getRewardType());
        assertNotNull(zeroLineResult_eight.getRewardLineDetailList());
        assertEquals(1, zeroLineResult_eight.getRewardLineDetailList().size());
        assertEquals(8, (long) zeroLineResult_eight.getRewardLineDetailList().get(0).getRewardLineCount());

        // case4:中全部
        DiwataMayariSymbolDo[][] diwataMayariSymbolDos_all = DiwataMayariRewardHandlerTestMocker.buildSymbolArr_allLine_test();
        RewardResultDo<DiwataMayariSymbolDo> zeroLineResult_all = diwataMayariRewardHandler.handle(DiwataMayariRewardHandlerTestMocker.buildRewardHandleDo(diwataMayariSymbolDos_all, 1000L, 0, false, BigDecimal.ONE));
        assertNotNull(zeroLineResult_all);
        zeroLineResult_all.print();
        assertEquals(RewardTypeEnum.SINGLE, zeroLineResult_all.getRewardType());
        assertNotNull(zeroLineResult_all.getRewardLineDetailList());
        assertEquals(1, zeroLineResult_all.getRewardLineDetailList().size());
        assertEquals(3600, (long) zeroLineResult_all.getRewardLineDetailList().get(0).getRewardLineCount());
    }


    /**
     * 免费轮测试
     * case1:中0条线-中3个scatter
     * case2:中0条线-中2个scatter
     * case3:中0条线-中5个scatter
     * case4:中0条线-中30个scatter(全中)
     * case4:中1条线-中5个scatter
     */
    @Test
    public void handler_normal_freeTimes_test() {
        // case1:中0条线-中3个scatter
        DiwataMayariSymbolDo[][] diwataMayariSymbolDos_zeroLine_3Scatter = DiwataMayariRewardHandlerTestMocker.buildSymbolArr_zeroLine_3Scatter_test();
        RewardResultDo<DiwataMayariSymbolDo> zeroLineResult_zeroLine_3Scatter = diwataMayariRewardHandler.handle(DiwataMayariRewardHandlerTestMocker.buildRewardHandleDo(diwataMayariSymbolDos_zeroLine_3Scatter, 1000L, 0, false, BigDecimal.ONE));
        assertNotNull(zeroLineResult_zeroLine_3Scatter);
        assertEquals(RewardTypeEnum.NONE, zeroLineResult_zeroLine_3Scatter.getRewardType());
        assertNull(zeroLineResult_zeroLine_3Scatter.getRewardLineDetailList());
        assertTrue(zeroLineResult_zeroLine_3Scatter.isHasRewardFreeWheel());
        assertEquals((int) freeWheelTimesRuleConfig.getBaseFreeWheelTimes(), zeroLineResult_zeroLine_3Scatter.getRewardFreeWheelTimes());

        // case2:中0条线-中2个scatter
        DiwataMayariSymbolDo[][] diwataMayariSymbolDos_zeroLine_2Scatter = DiwataMayariRewardHandlerTestMocker.buildSymbolArr_zeroLine_2Scatter_test();
        RewardResultDo<DiwataMayariSymbolDo> zeroLineResult_zeroLine_2Scatter = diwataMayariRewardHandler.handle(DiwataMayariRewardHandlerTestMocker.buildRewardHandleDo(diwataMayariSymbolDos_zeroLine_2Scatter, 1000L, 0, false, BigDecimal.ONE));
        assertNotNull(zeroLineResult_zeroLine_2Scatter);
        assertEquals(RewardTypeEnum.NONE, zeroLineResult_zeroLine_2Scatter.getRewardType());
        assertNull(zeroLineResult_zeroLine_2Scatter.getRewardLineDetailList());
        assertFalse(zeroLineResult_zeroLine_2Scatter.isHasRewardFreeWheel());
        assertEquals(0, zeroLineResult_zeroLine_2Scatter.getRewardFreeWheelTimes());

        // case3:中0条线-中5个scatter
        DiwataMayariSymbolDo[][] diwataMayariSymbolDos_zeroLine_5Scatter = DiwataMayariRewardHandlerTestMocker.buildSymbolArr_zeroLine_5Scatter_test();
        RewardResultDo<DiwataMayariSymbolDo> zeroLineResult_zeroLine_5Scatter = diwataMayariRewardHandler.handle(DiwataMayariRewardHandlerTestMocker.buildRewardHandleDo(diwataMayariSymbolDos_zeroLine_5Scatter, 1000L, 0, false, BigDecimal.ONE));
        assertNotNull(zeroLineResult_zeroLine_5Scatter);
        assertEquals(RewardTypeEnum.NONE, zeroLineResult_zeroLine_5Scatter.getRewardType());
        assertNull(zeroLineResult_zeroLine_5Scatter.getRewardLineDetailList());
        assertTrue(zeroLineResult_zeroLine_5Scatter.isHasRewardFreeWheel());
        assertEquals(freeWheelTimesRuleConfig.getBaseFreeWheelTimes() + freeWheelTimesRuleConfig.getStepFreeWheelTimes() * 2, zeroLineResult_zeroLine_5Scatter.getRewardFreeWheelTimes());

        // case4:中0条线-中30个scatter(全中)
        DiwataMayariSymbolDo[][] diwataMayariSymbolDos_zeroLine_30Scatter = DiwataMayariRewardHandlerTestMocker.buildSymbolArr_zeroLine_30Scatter_test();
        RewardResultDo<DiwataMayariSymbolDo> zeroLineResult_zeroLine_30Scatter = diwataMayariRewardHandler.handle(DiwataMayariRewardHandlerTestMocker.buildRewardHandleDo(diwataMayariSymbolDos_zeroLine_30Scatter, 1000L, 0, false, BigDecimal.ONE));
        assertNotNull(zeroLineResult_zeroLine_30Scatter);
        assertEquals(RewardTypeEnum.NONE, zeroLineResult_zeroLine_30Scatter.getRewardType());
        assertNull(zeroLineResult_zeroLine_30Scatter.getRewardLineDetailList());
        assertTrue(zeroLineResult_zeroLine_30Scatter.isHasRewardFreeWheel());
        assertEquals(freeWheelTimesRuleConfig.getBaseFreeWheelTimes() + freeWheelTimesRuleConfig.getStepFreeWheelTimes() * 21L, zeroLineResult_zeroLine_30Scatter.getRewardFreeWheelTimes());
    }


}
