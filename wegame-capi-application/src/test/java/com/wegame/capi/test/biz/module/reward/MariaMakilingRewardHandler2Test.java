package com.wegame.capi.test.biz.module.reward;//package com.wegame.capi.application.controller;

import cn.hutool.core.lang.Assert;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.reward.MariaMakilingRewardHandler;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.common.enums.SymbolRuleTypeEnum;
import com.wegame.capi.common.enums.SymbolTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static com.wegame.capi.biz.helper.MariaMakilingGameHelper.MARIA_MAKILING_GAME_INFO;

@Slf4j
public class MariaMakilingRewardHandler2Test extends BaseTest {

    @Resource()
    MariaMakilingRewardHandler mariaMakilingRewardHandler;

    @Test
    public void test_handle() {
        RewardHandleDo rewardHandle = RewardHandleDo.builder()
                .gameInfo(MARIA_MAKILING_GAME_INFO)
                .gameType(GameTypeEnum.MARIA_MAKILING)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .wheel(WheelDo.builder()
                        .index(1000)
                        .symbolArr(this.manualGeneratorSymbolArr())
                        .build())
                .betAmount(200L)
                .build();
        long startTime = DateUtil.getCurrentMilli();
        //        for (int i=0;i<1000000;i++){
        //            mariaMakilingRewardHandler.handle(rewardDo);
        //        }
        RewardResultDo rewardResult = mariaMakilingRewardHandler.handle(rewardHandle);
        long endTime = DateUtil.getCurrentMilli();
        Assert.notNull(rewardResult);
        rewardResult.print();
        Assert.isTrue(rewardResult.getRewardLineDetailList().size() == 2);
        log.info("test_handle，耗时：{}", endTime - startTime);
    }


    private SymbolDo[][] manualGeneratorSymbolArr() {
        SymbolDo[][] symbolArr = new SymbolDo[3][5];
        // 243线
        // 第一列
        symbolArr[0][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第二列
        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第三列
        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolRuleTypeEnum.MULTIPLE, BigDecimal.ZERO);
        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第四列
        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第五列
        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

// 8+1线
//        // 第一列
//        symbolArr[0][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Scatter, SymbolTypeEnum.SCATTER, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第二列
//        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][1] = this.buildASymbol(SymbolEnum.WildX2, SymbolTypeEnum.WILD, SymbolRuleTypeEnum.MULTIPLE, BigDecimal.valueOf(2L));
//        symbolArr[2][1] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第三列
//        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Scatter, SymbolTypeEnum.SCATTER, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第四列
//        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第五列
//        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Scatter, SymbolTypeEnum.SCATTER, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][4] = this.buildASymbol(SymbolEnum.WildX2, SymbolTypeEnum.WILD, SymbolRuleTypeEnum.MULTIPLE, BigDecimal.valueOf(2L));
        return symbolArr;
    }

    private SymbolDo buildASymbol(SymbolEnum symbol, SymbolTypeEnum symbolType, SymbolRuleTypeEnum symbolRuleType, BigDecimal ruleValue) {
        SymbolDo symbolDo = new SymbolDo();
        symbolDo.setId(symbol.getId());
        symbolDo.setName(symbol.getName());
        symbolDo.setSymbolType(symbolType);
        symbolDo.setSymbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(symbolRuleType).value(ruleValue).build());
        return symbolDo;
    }
}