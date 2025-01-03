package com.wegame.capi.application.service;//package com.wegame.capi.application.controller;

import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.application.BaseTest;
import com.wegame.capi.biz.domain.SymbolDo;
import com.wegame.capi.biz.domain.WheelDo;
import com.wegame.capi.biz.domain.RewardHandleDo;
import com.wegame.capi.biz.domain.RewardResultDo;
import com.wegame.capi.biz.domain.RewardRuleDo;
import com.wegame.capi.biz.domain.RewardRuleResultDo;
import com.wegame.capi.biz.module.reward.MariaMakilingRewardHandler;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Slf4j
public class RewardServiceTest extends BaseTest {

    @Autowired
    MariaMakilingRewardHandler mariaMakilingRewardHandler;


    @Test
    public void test_calcRewardResult() {
        SymbolDo[][] symbolArr = this.manualGeneratorSymbolArr();
        RewardHandleDo rewardDo = RewardHandleDo.builder()
                .gameType(GameTypeEnum.MARIA_MAKILING)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .wheel(WheelDo.builder().index(1000).symbolArr(symbolArr).build())
                .betAmount(200L)
                .build();
        long startTime = DateUtil.getCurrentMilli();
//        for (int i=0;i<1000000;i++){
//            mariaMakilingRewardHandler.handle(rewardDo);
//        }
        RewardResultDo rewardResultDo = mariaMakilingRewardHandler.handle(rewardDo);
        long endTime = DateUtil.getCurrentMilli();

//        log.info("消耗时间：{}", endTime - startTime);

        log.info("test_calcRewardLines-calcRewardResult result:{},消耗时间：{}", FastJsonUtils.toJSONString(rewardResultDo), endTime - startTime);
    }


    @Test
    public void test_calcRewardLines() {
        SymbolDo[][] symbolArr = this.manualGeneratorSymbolArr();
        RewardRuleDo rewardRuleDo = RewardRuleDo.builder()
                .gameType(GameTypeEnum.MARIA_MAKILING)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .symbolArr(symbolArr)
                .build();
        long startTime = DateUtil.getCurrentTimestamp();
//        for (int i=0;i<1000000;i++){
//            RewardRuleResultDo resultDo = mariaMakilingRewardHandler.calcRewardLines(rewardRuleDo);
//        }
        RewardRuleResultDo resultDo = mariaMakilingRewardHandler.calcRewardLines(rewardRuleDo);
        long endTime = DateUtil.getCurrentTimestamp();

        log.info("消耗时间：{}", endTime - startTime);

        log.info("test_calcRewardLines-calcRewardLines result:{}", FastJsonUtils.toJSONString(resultDo));
    }

    private SymbolDo[][] manualGeneratorSymbolArr() {
        SymbolDo[][] symbolArr = new SymbolDo[3][5];
        // 243线
//        // 第一列
//        symbolArr[0][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第二列
//        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第三列
//        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Wild, SymbolTypeEnum.WILD, SymbolRuleTypeEnum.MULTIPLE, BigDecimal.ZERO);
//        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第四列
//        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第五列
//        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

// 8+1线
        // 第一列
        symbolArr[0][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Scatter, SymbolTypeEnum.SCATTER, SymbolRuleTypeEnum.COMMON, null);

        // 第二列
        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][1] = this.buildASymbol(SymbolEnum.WildX2, SymbolTypeEnum.WILD, SymbolRuleTypeEnum.MULTIPLE, BigDecimal.valueOf(2L));
        symbolArr[2][1] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第三列
        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Scatter, SymbolTypeEnum.SCATTER, SymbolRuleTypeEnum.COMMON, null);

        // 第四列
        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第五列
        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Scatter, SymbolTypeEnum.SCATTER, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][4] = this.buildASymbol(SymbolEnum.WildX2, SymbolTypeEnum.WILD, SymbolRuleTypeEnum.MULTIPLE, BigDecimal.valueOf(2L));

// 随机
//        // 第一列
//        symbolArr[0][0] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol2004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第二列
//        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][1] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][1] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第三列
//        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第四列
//        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol2004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//
//        // 第五列
//        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol2001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
//        symbolArr[1][4] = this.buildASymbol(SymbolEnum.WildX2, SymbolTypeEnum.WILD, SymbolRuleTypeEnum.MULTIPLE, BigDecimal.valueOf(2L));
//        symbolArr[2][4] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);


        return symbolArr;
    }

    private SymbolDo buildASymbol(SymbolEnum symbolEnum, SymbolTypeEnum symbolTypeEnum, SymbolRuleTypeEnum symbolRuleTypeEnum, BigDecimal ruleValue) {
        SymbolDo symbolDo = new SymbolDo();
        symbolDo.setId(symbolEnum.getId());
        symbolDo.setName(symbolEnum.getName());
        symbolDo.setSymbolType(symbolTypeEnum);
        symbolDo.setSymbolRule(SymbolDo.SymbolRuleDo.builder().symbolRuleType(symbolRuleTypeEnum).value(ruleValue).build());
        return symbolDo;
    }
}