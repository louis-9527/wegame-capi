package com.wegame.capi.test.biz.module.diwatamayari.multiple;

import com.google.common.collect.Lists;
import com.wegame.capi.biz.config.domain.DiwataMayariConfig;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDo;
import com.wegame.capi.biz.domain.biz.symbol.DiwataMayariSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.diawatamayari.DiwataMayariGameHelper;
import com.wegame.capi.biz.module.diawatamayari.multiple.DiwataMayariMultipleCalculateStrategy;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.*;

public class DiwataMayariMultipleCalculateStrategyTest extends BaseTest {
    @Autowired
    private DiwataMayariMultipleCalculateStrategy diwataMayariMultipleCalculateStrategy;
    @Autowired
    private DiwataMayariConfig diwataMayariConfig;
    private DiwataMayariConfig.MultiplePieRuleConfig multiplePieRuleConfig;

    @Before
    public void before() {
        multiplePieRuleConfig = diwataMayariConfig.getMultiplePieRuleConfig();
    }

    /**
     * 免费轮，正常业务测试
     * 当前轮：第二轮
     * <p>
     * case1：中奖
     * <p>
     * case2: 未中奖
     */
    @Test
    public void calculateMultiple_normal_freeWheel_test() {
        BigDecimal currentMultiple = multiplePieRuleConfig.getFreePlayMinMultiple().multiply(BigDecimal.valueOf(2));
        RewardHandleDo<DiwataMayariSymbolDo> rewardHandleDo = this.buildRewardHandleDo(1, true, currentMultiple);
        // 中奖
        BigDecimal calculateMultiple = diwataMayariMultipleCalculateStrategy.calculateMultiple(rewardHandleDo, Lists.newArrayList(RewardLineDo.<DiwataMayariSymbolDo>builder().build()));
        assertEquals(calculateMultiple, currentMultiple.multiply(BigDecimal.valueOf(2)));
        // 不中奖
        BigDecimal calculateMultiple1 = diwataMayariMultipleCalculateStrategy.calculateMultiple(rewardHandleDo, null);
        assertEquals(calculateMultiple1, currentMultiple);
    }


    /**
     * 正常轮，正常业务测试
     * 当前轮：第二轮
     * <p>
     * case1：中奖
     * <p>
     * case2: 未中奖
     */
    @Test
    public void calculateMultiple_normal_commonWheel_test() {
        BigDecimal currentMultiple = multiplePieRuleConfig.getCommonPlayMinMultiple().multiply(BigDecimal.valueOf(2));
        RewardHandleDo<DiwataMayariSymbolDo> rewardHandleDo = this.buildRewardHandleDo(1, false, currentMultiple);
        // 中奖
        BigDecimal calculateMultiple = diwataMayariMultipleCalculateStrategy.calculateMultiple(rewardHandleDo, Lists.newArrayList(RewardLineDo.<DiwataMayariSymbolDo>builder().build()));
        assertEquals(calculateMultiple, currentMultiple.multiply(BigDecimal.valueOf(2)));
        // 不中奖
        BigDecimal calculateMultiple1 = diwataMayariMultipleCalculateStrategy.calculateMultiple(rewardHandleDo, null);
        assertEquals(calculateMultiple1, currentMultiple);
    }


    /**
     * 正常轮，边界测试
     * <p>
     * case1：currentMultiple=null,第一轮,中奖
     * <p>
     * case2: currentMultiple*2 大于最大的,中奖
     */
    @Test
    public void calculateMultiple_boundary_commonWheel_test() {
        BigDecimal currentMultiple = null;
        RewardHandleDo<DiwataMayariSymbolDo> rewardHandleDo = this.buildRewardHandleDo(0, false, currentMultiple);
        // case1
        BigDecimal calculateMultiple = diwataMayariMultipleCalculateStrategy.calculateMultiple(rewardHandleDo, Lists.newArrayList(RewardLineDo.<DiwataMayariSymbolDo>builder().build()));
        assertEquals(calculateMultiple, multiplePieRuleConfig.getCommonPlayMinMultiple());
        // case2
        currentMultiple = multiplePieRuleConfig.getMaxMultiple().divide(BigDecimal.valueOf(2), RoundingMode.FLOOR);
        rewardHandleDo = this.buildRewardHandleDo(20, false, currentMultiple);
        BigDecimal calculateMultiple1 = diwataMayariMultipleCalculateStrategy.calculateMultiple(rewardHandleDo, Lists.newArrayList(RewardLineDo.<DiwataMayariSymbolDo>builder().build()));
        assertEquals(calculateMultiple1, multiplePieRuleConfig.getMaxMultiple());
    }


    private RewardHandleDo<DiwataMayariSymbolDo> buildRewardHandleDo(int wheelIndex, boolean freeFlag, BigDecimal currentMultiple) {
        return RewardHandleDo.<DiwataMayariSymbolDo>builder()
                .gameType(GameTypeEnum.DIWATA_MAYARI)
                .gameInfo(DiwataMayariGameHelper.DIWATA_MAYARI_GAME_INFO)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .gamePlayType(GamePlayTypeEnum.NORMAL_PLAY)
                .betAmount(10000L)
                .currentMultiple(currentMultiple)
                .currentTotalRewardAmount(10000L)
                .wheel(WheelDo.<DiwataMayariSymbolDo>builder().index(wheelIndex).freeFlag(freeFlag).build())
                .build();
    }


}
