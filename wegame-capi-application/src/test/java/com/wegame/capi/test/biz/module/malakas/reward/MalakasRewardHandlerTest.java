package com.wegame.capi.test.biz.module.malakas.reward;

import cn.hutool.core.lang.Assert;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.biz.domain.biz.reward.RewardHandleDo;
import com.wegame.capi.biz.domain.biz.reward.RewardResultDo;
import com.wegame.capi.biz.domain.biz.reward.line.RewardLineDetailDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.domain.biz.symbol.SymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.malakas.reward.MalakasRewardHandler;
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

import static com.wegame.capi.biz.module.malakas.MalakasGameHelper.MALAKAS_GAME_INFO;
import static com.wegame.capi.common.enums.SymbolEnum.Symbol1000;

@Slf4j
public class MalakasRewardHandlerTest extends BaseTest {

    @Resource()
    MalakasRewardHandler malakasRewardHandler;

    @Test
    public void test_handle_32400() {
        RewardHandleDo rewardHandle = RewardHandleDo.builder()
                .gameInfo(MALAKAS_GAME_INFO)
                .gameType(GameTypeEnum.MALAKAS)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .wheel(WheelDo.<SymbolDo>builder()
                        .index(1000)
                        .symbolArr(this.buildSymbolArr32400())
                        .build())
                .betAmount(200L)
                .build();
        long startTime = DateUtil.getCurrentMilli();
        RewardResultDo<SymbolDo> rewardResult = malakasRewardHandler.handle(rewardHandle);
        long endTime = DateUtil.getCurrentMilli();
        Assert.notNull(rewardResult);
        rewardResult.print();
        Assert.isTrue(rewardResult.getRewardLineDetailList().size() == 2);
        Assert.isTrue(  rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo::getRewardLineCount)
                .reduce(0, Integer::sum) == 9);
        log.info("test_handle_1，耗时：{}", endTime - startTime);
    }

    /**
     * 测试用例
     * 1.构建1条线
     * 2.构建2条线
     * 3.构建32400条线
     * 4.投建含有跨行元素
     * @return
     */
    private SymbolDo[][] buildSymbolArr32400() {
        SymbolDo[][] symbolArr = new SymbolDo[3][5];
        // 第一列
        symbolArr[0][0] = this.buildAPlaceHolderSymbol();
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第二列
        symbolArr[0][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第三列
        symbolArr[0][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第四列
        symbolArr[0][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第五列
        symbolArr[0][0] = this.buildAPlaceHolderSymbol();
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
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

    private SymbolDo buildAPlaceHolderSymbol() {
        return MalakasSymbolDo.builder()
                .id(Symbol1000.getId())
                .name(Symbol1000.getName())
                .symbolType(SymbolTypeEnum.COMMON)
                .symbolRule(SymbolDo.SymbolRuleDo
                        .builder()
                        .symbolRuleType(SymbolRuleTypeEnum.COMMON)
                        .value(null)
                        .build())
                .build();
    }
}