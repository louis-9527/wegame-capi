package com.wegame.capi.test.biz.module.base.jackpot;

import com.wegame.capi.biz.config.domain.LamangConfig;
import com.wegame.capi.biz.module.lamang.LamangJackpotHandler;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.JackpotRewardPoolTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
public class JackpotHandlerTest extends BaseTest {
    private LamangConfig.CommissionConfig buyCommissionConfig;
    private LamangConfig.CommissionConfig commonCommissionConfig;

    @Autowired
    LamangJackpotHandler lamangJackpotHandler;
    @Autowired
    LamangConfig lamangConfig;

    @Before
    public void before() {
        buyCommissionConfig = lamangConfig.getJackPotConfig().getBuyCommissionConfig();
        commonCommissionConfig = lamangConfig.getJackPotConfig().getCommonCommissionConfig();
    }


    @Test
    public void calculateRewardPoolCommission_normal_normalBet_test() {
        // 按金额提成
        TreeMap<Long, TreeMap<String, Long>> amountCommissionConfig = commonCommissionConfig.getAmountCommissionConfig();
        amountCommissionConfig.keySet().forEach(amount -> {
            Map<JackpotRewardPoolTypeEnum, Long> jackpotRewardPoolTypeEnumLongMap = lamangJackpotHandler.calculateRewardPoolCommission(BetPromotionTypeEnum.NONE, BetTypeEnum.NORMAL_BET, amount);
            TreeMap<String, Long> betAmountCommissionConfig = this.buildBetAmountCommissionConfig(BetTypeEnum.NORMAL_BET, amount);
            assertNotNull(jackpotRewardPoolTypeEnumLongMap);
            for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
                Long actualAmount = jackpotRewardPoolTypeEnumLongMap.get(poolType);
                Long configAmount = betAmountCommissionConfig.get(poolType.name());
                assertNotNull(actualAmount);
                assertNotNull(configAmount);
                assertEquals(configAmount, actualAmount);
            }
        });


        // 比例提成 5.5
        Map<JackpotRewardPoolTypeEnum, Long> jackpotRewardPoolTypeEnumLongMap_5500 = lamangJackpotHandler.calculateRewardPoolCommission(BetPromotionTypeEnum.NONE, BetTypeEnum.NORMAL_BET, 5500L);
        TreeMap<String, BigDecimal> proportionCommission_5500 = this.buildProportionCommissionConfig(BetTypeEnum.NORMAL_BET);
        assertNotNull(jackpotRewardPoolTypeEnumLongMap_5500);
        for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
            Long actualAmount = jackpotRewardPoolTypeEnumLongMap_5500.get(poolType);
            BigDecimal configRate = proportionCommission_5500.get(poolType.name()).divide(BigDecimal.valueOf(100));
            Long configAmount = new BigDecimal(5500L).multiply(configRate).longValue();
            assertNotNull(actualAmount);
            assertNotNull(configAmount);
            assertEquals(configAmount, actualAmount);
        }

        // 比例提成 60
        Map<JackpotRewardPoolTypeEnum, Long> jackpotRewardPoolTypeEnumLongMap_60000 = lamangJackpotHandler.calculateRewardPoolCommission(BetTypeEnum.NORMAL_BET, 60000L);
        TreeMap<String, BigDecimal> proportionCommission_60000 = this.buildProportionCommissionConfig(BetTypeEnum.NORMAL_BET);
        assertNotNull(jackpotRewardPoolTypeEnumLongMap_60000);
        for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
            Long actualAmount = jackpotRewardPoolTypeEnumLongMap_60000.get(poolType);
            BigDecimal configRate = proportionCommission_60000.get(poolType.name()).divide(BigDecimal.valueOf(100));
            Long configAmount = new BigDecimal(60000L).multiply(configRate).longValue();
            assertNotNull(actualAmount);
            assertNotNull(configAmount);
            assertEquals(configAmount, actualAmount);
        }

    }

    @Test
    public void calculateRewardPoolCommission_normal_buyBet_test() {
        // 按金额提成
        TreeMap<Long, TreeMap<String, Long>> amountCommissionConfig = commonCommissionConfig.getAmountCommissionConfig();
        amountCommissionConfig.keySet().forEach(amount -> {
            Map<JackpotRewardPoolTypeEnum, Long> jackpotRewardPoolTypeEnumLongMap = lamangJackpotHandler.calculateRewardPoolCommission(BetPromotionTypeEnum.NONE, BetTypeEnum.BUY_BET, amount);
            TreeMap<String, Long> betAmountCommissionConfig = this.buildBetAmountCommissionConfig(BetTypeEnum.BUY_BET, amount);
            assertNotNull(jackpotRewardPoolTypeEnumLongMap);
            for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
                Long actualAmount = jackpotRewardPoolTypeEnumLongMap.get(poolType);
                Long configAmount = betAmountCommissionConfig.get(poolType.name());
                assertNotNull(actualAmount);
                assertNotNull(configAmount);
                assertEquals(configAmount, actualAmount);
            }
        });


        // 比例提成 5.5
        Map<JackpotRewardPoolTypeEnum, Long> jackpotRewardPoolTypeEnumLongMap_5500 = lamangJackpotHandler.calculateRewardPoolCommission(BetPromotionTypeEnum.NONE, BetTypeEnum.BUY_BET, 5500L);
        TreeMap<String, BigDecimal> proportionCommission_5500 = this.buildProportionCommissionConfig(BetTypeEnum.BUY_BET);
        assertNotNull(jackpotRewardPoolTypeEnumLongMap_5500);
        for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
            Long actualAmount = jackpotRewardPoolTypeEnumLongMap_5500.get(poolType);
            BigDecimal configRate = proportionCommission_5500.get(poolType.name()).divide(BigDecimal.valueOf(100));
            Long configAmount = new BigDecimal(5500L).multiply(configRate).longValue();
            assertNotNull(actualAmount);
            assertNotNull(configAmount);
            assertEquals(configAmount, actualAmount);
        }

        // 比例提成 60
        Map<JackpotRewardPoolTypeEnum, Long> jackpotRewardPoolTypeEnumLongMap_60000 = lamangJackpotHandler.calculateRewardPoolCommission(BetPromotionTypeEnum.NONE, BetTypeEnum.BUY_BET, 60000L);
        TreeMap<String, BigDecimal> proportionCommission_60000 = this.buildProportionCommissionConfig(BetTypeEnum.BUY_BET);
        assertNotNull(jackpotRewardPoolTypeEnumLongMap_60000);
        for (JackpotRewardPoolTypeEnum poolType : JackpotRewardPoolTypeEnum.values()) {
            Long actualAmount = jackpotRewardPoolTypeEnumLongMap_60000.get(poolType);
            BigDecimal configRate = proportionCommission_60000.get(poolType.name()).divide(BigDecimal.valueOf(100));
            Long configAmount = new BigDecimal(60000L).multiply(configRate).longValue();
            assertNotNull(actualAmount);
            assertNotNull(configAmount);
            assertEquals(configAmount, actualAmount);
        }

    }


    private TreeMap<String, Long> buildBetAmountCommissionConfig(BetTypeEnum betTypeEnum, Long betAmount) {
        TreeMap<Long, TreeMap<String, Long>> amountCommissionConfig = null;
        if (betTypeEnum == BetTypeEnum.NORMAL_BET) {
            amountCommissionConfig = commonCommissionConfig.getAmountCommissionConfig();
        } else if (betTypeEnum == BetTypeEnum.BUY_BET) {
            amountCommissionConfig = buyCommissionConfig.getAmountCommissionConfig();
        } else {
            throw new RuntimeException("betTypeEnum is error");
        }

        return amountCommissionConfig.get(betAmount);
    }

    private TreeMap<String, BigDecimal> buildProportionCommissionConfig(BetTypeEnum betTypeEnum) {
        if (betTypeEnum == BetTypeEnum.NORMAL_BET) {
            return commonCommissionConfig.getProportionCommissionConfig();
        } else if (betTypeEnum == BetTypeEnum.BUY_BET) {
            return buyCommissionConfig.getProportionCommissionConfig();
        } else {
            throw new RuntimeException("betTypeEnum is error");
        }
    }

}
