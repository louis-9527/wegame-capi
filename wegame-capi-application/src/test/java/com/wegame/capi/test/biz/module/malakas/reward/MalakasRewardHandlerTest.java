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
import com.wegame.capi.common.enums.SymbolSizeEnum;
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

    @Resource
    MalakasRewardHandler malakasRewardHandler;

    @Test
    public void test_handle_1() {
        RewardHandleDo rewardHandle = RewardHandleDo.builder()
                .gameInfo(MALAKAS_GAME_INFO)
                .gameType(GameTypeEnum.MALAKAS)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .wheel(WheelDo.<SymbolDo>builder()
                        .index(1000)
                        .symbolArr(this.buildSymbolArr1())
                        .build())
                .betAmount(200L)
                .build();
        long startTime = DateUtil.getCurrentMilli();
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(rewardHandle);
        long endTime = DateUtil.getCurrentMilli();
        Assert.notNull(rewardResult);
        rewardResult.print();
        Assert.isTrue(rewardResult.getRewardLineDetailList().size() == 1);
        Assert.isTrue(  rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo::getRewardLineCount)
                .reduce(0, Integer::sum) == 1);
        log.info("test_handle_1，耗时：{}", endTime - startTime);
    }

    @Test
    public void test_handle_4() {
        RewardHandleDo rewardHandle = RewardHandleDo.builder()
                .gameInfo(MALAKAS_GAME_INFO)
                .gameType(GameTypeEnum.MALAKAS)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .wheel(WheelDo.<SymbolDo>builder()
                        .index(1000)
                        .symbolArr(this.buildSymbolArr4())
                        .build())
                .betAmount(200L)
                .build();
        long startTime = DateUtil.getCurrentMilli();
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(rewardHandle);
        long endTime = DateUtil.getCurrentMilli();
        Assert.notNull(rewardResult);
        rewardResult.print();
        Assert.isTrue(rewardResult.getRewardLineDetailList().size() == 1);
        Assert.isTrue(  rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo::getRewardLineCount)
                .reduce(0, Integer::sum) == 4);
        log.info("test_handle_4，耗时：{}", endTime - startTime);
    }

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
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(rewardHandle);
        long endTime = DateUtil.getCurrentMilli();
        Assert.notNull(rewardResult);
        rewardResult.print();
        Assert.isTrue(rewardResult.getRewardLineDetailList().size() == 1);
        Assert.isTrue(  rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo::getRewardLineCount)
                .reduce(0, Integer::sum) == 32400);
        log.info("test_handle_32400，耗时：{}", endTime - startTime);
    }

    @Test
    public void test_handle_m_1() {
        RewardHandleDo rewardHandle = RewardHandleDo.builder()
                .gameInfo(MALAKAS_GAME_INFO)
                .gameType(GameTypeEnum.MALAKAS)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .wheel(WheelDo.<SymbolDo>builder()
                        .index(1000)
                        .symbolArr(this.buildMSymbolArr1())
                        .build())
                .betAmount(200L)
                .build();
        long startTime = DateUtil.getCurrentMilli();
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(rewardHandle);
        long endTime = DateUtil.getCurrentMilli();
        Assert.notNull(rewardResult);
        rewardResult.print();
        Assert.isTrue(rewardResult.getRewardLineDetailList().size() == 1);
        Assert.isTrue(  rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo::getRewardLineCount)
                .reduce(0, Integer::sum) == 1);
        log.info("test_handle_m_1，耗时：{}", endTime - startTime);
    }

    @Test
    public void test_handle_l_1() {
        RewardHandleDo rewardHandle = RewardHandleDo.builder()
                .gameInfo(MALAKAS_GAME_INFO)
                .gameType(GameTypeEnum.MALAKAS)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .wheel(WheelDo.<SymbolDo>builder()
                        .index(1000)
                        .symbolArr(this.buildLSymbolArr1())
                        .build())
                .betAmount(200L)
                .build();
        long startTime = DateUtil.getCurrentMilli();
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(rewardHandle);
        long endTime = DateUtil.getCurrentMilli();
        Assert.notNull(rewardResult);
        rewardResult.print();
        Assert.isTrue(rewardResult.getRewardLineDetailList().size() == 1);
        Assert.isTrue(  rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo::getRewardLineCount)
                .reduce(0, Integer::sum) == 1);
        log.info("test_handle_l_1，耗时：{}", endTime - startTime);
    }

    @Test
    public void test_handle_l_14() {
        RewardHandleDo rewardHandle = RewardHandleDo.builder()
                .gameInfo(MALAKAS_GAME_INFO)
                .gameType(GameTypeEnum.MALAKAS)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .wheel(WheelDo.<SymbolDo>builder()
                        .index(1000)
                        .symbolArr(this.buildXLSymbolArr14())
                        .build())
                .betAmount(200L)
                .build();
        long startTime = DateUtil.getCurrentMilli();
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(rewardHandle);
        long endTime = DateUtil.getCurrentMilli();
        Assert.notNull(rewardResult);
        rewardResult.print();
        Assert.isTrue(rewardResult.getRewardLineDetailList().size() == 1);
        Assert.isTrue(  rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo::getRewardLineCount)
                .reduce(0, Integer::sum) == 1);
        log.info("test_handle_l_14，耗时：{}", endTime - startTime);
    }

    @Test
    public void test_handle_xl_1() {
        RewardHandleDo rewardHandle = RewardHandleDo.builder()
                .gameInfo(MALAKAS_GAME_INFO)
                .gameType(GameTypeEnum.MALAKAS)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .wheel(WheelDo.<SymbolDo>builder()
                        .index(1000)
                        .symbolArr(this.buildXLSymbolArr1())
                        .build())
                .betAmount(200L)
                .build();
        long startTime = DateUtil.getCurrentMilli();
        RewardResultDo<MalakasSymbolDo> rewardResult = malakasRewardHandler.handle(rewardHandle);
        long endTime = DateUtil.getCurrentMilli();
        Assert.notNull(rewardResult);
        rewardResult.print();
        Assert.isTrue(rewardResult.getRewardLineDetailList().size() == 1);
        Assert.isTrue(  rewardResult.getRewardLineDetailList()
                .stream()
                .map(RewardLineDetailDo::getRewardLineCount)
                .reduce(0, Integer::sum) == 1);
        log.info("test_handle_xl_1，耗时：{}", endTime - startTime);
    }

    /**
     * 测试用例
     * 1.构建2条线
     * 2.构建32400条线
     * 3.投建含有跨行元素
     * @return
     */

    private MalakasSymbolDo[][] buildSymbolArr1() {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = this.buildAPlaceHolderSymbol();
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第二列
        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第三列
        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第四列
        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第五列
        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第六列
        symbolArr[0][5] = this.buildAPlaceHolderSymbol();
        symbolArr[1][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        return symbolArr;
    }

    private MalakasSymbolDo[][] buildSymbolArr4() {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = this.buildAPlaceHolderSymbol();
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第二列
        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第三列
        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第四列
        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第五列
        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第六列
        symbolArr[0][5] = this.buildAPlaceHolderSymbol();
        symbolArr[1][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        return symbolArr;
    }

    private MalakasSymbolDo[][] buildSymbolArr32400() {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = this.buildAPlaceHolderSymbol();
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第二列
        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第三列
        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第四列
        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第五列
        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第六列
        symbolArr[0][5] = this.buildAPlaceHolderSymbol();
        symbolArr[1][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        return symbolArr;
    }

    private MalakasSymbolDo[][] buildMSymbolArr1() {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = this.buildAPlaceHolderSymbol();
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第二列
        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, SymbolSizeEnum.M, null);
        symbolArr[2][1] = this.buildAPlaceHolderSymbol();
        symbolArr[3][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第三列
        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第四列
        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第五列
        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第六列
        symbolArr[0][5] = this.buildAPlaceHolderSymbol();
        symbolArr[1][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        return symbolArr;
    }

    private MalakasSymbolDo[][] buildLSymbolArr1() {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = this.buildAPlaceHolderSymbol();
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第二列
        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, SymbolSizeEnum.L, null);
        symbolArr[2][1] = this.buildAPlaceHolderSymbol();
        symbolArr[3][1] = this.buildAPlaceHolderSymbol();
        symbolArr[4][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第三列
        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第四列
        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第五列
        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][4] = this.buildASymbol(SymbolEnum.Scatter, SymbolTypeEnum.SCATTER, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][4] = this.buildASymbol(SymbolEnum.Scatter, SymbolTypeEnum.SCATTER, SymbolRuleTypeEnum.COMMON, null);

        // 第六列
        symbolArr[0][5] = this.buildAPlaceHolderSymbol();
        symbolArr[1][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        return symbolArr;
    }

    private MalakasSymbolDo[][] buildXLSymbolArr1() {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = this.buildAPlaceHolderSymbol();
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第二列
        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][1] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, SymbolSizeEnum.XL, null);
        symbolArr[2][1] = this.buildAPlaceHolderSymbol();
        symbolArr[3][1] = this.buildAPlaceHolderSymbol();
        symbolArr[4][1] = this.buildAPlaceHolderSymbol();
        symbolArr[5][1] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第三列
        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][2] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第四列
        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][3] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第五列
        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][4] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第六列
        symbolArr[0][5] = this.buildAPlaceHolderSymbol();
        symbolArr[1][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        return symbolArr;
    }

    private MalakasSymbolDo[][] buildXLSymbolArr14() {
        MalakasSymbolDo[][] symbolArr = new MalakasSymbolDo[6][6];
        // 第一列
        symbolArr[0][0] = this.buildAPlaceHolderSymbol();
        symbolArr[1][0] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][0] = this.buildASymbol(SymbolEnum.Symbol1009, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][0] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][0] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][0] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第二列
        symbolArr[0][1] = this.buildASymbol(SymbolEnum.Symbol1007, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][1] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][1] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, SymbolSizeEnum.XL, null);
        symbolArr[3][1] = this.buildAPlaceHolderSymbol();
        symbolArr[4][1] = this.buildAPlaceHolderSymbol();
        symbolArr[5][1] = this.buildAPlaceHolderSymbol();

        // 第三列
        symbolArr[0][2] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][2] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, SymbolSizeEnum.XL, null);
        symbolArr[2][2] = this.buildAPlaceHolderSymbol();
        symbolArr[3][2] = this.buildAPlaceHolderSymbol();
        symbolArr[4][2] = this.buildAPlaceHolderSymbol();
        symbolArr[5][2] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第四列
        symbolArr[0][3] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][3] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, SymbolSizeEnum.M, null);
        symbolArr[2][3] = this.buildAPlaceHolderSymbol();
        symbolArr[3][3] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, SymbolSizeEnum.M, null);
        symbolArr[4][3] = this.buildAPlaceHolderSymbol();
        symbolArr[5][3] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第五列
        symbolArr[0][4] = this.buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[1][4] = this.buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, SymbolSizeEnum.M, null);
        symbolArr[2][4] = this.buildAPlaceHolderSymbol();
        symbolArr[3][4] = this.buildASymbol(SymbolEnum.Symbol1003, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, SymbolSizeEnum.M, null);
        symbolArr[4][4] = this.buildAPlaceHolderSymbol();
        symbolArr[5][4] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);

        // 第六列
        symbolArr[0][5] = this.buildAPlaceHolderSymbol();
        symbolArr[1][5] = this.buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[2][5] = this.buildASymbol(SymbolEnum.Symbol1005, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[3][5] = this.buildASymbol(SymbolEnum.Symbol1002, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[4][5] = this.buildASymbol(SymbolEnum.Symbol1001, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        symbolArr[5][5] = this.buildASymbol(SymbolEnum.Symbol1004, SymbolTypeEnum.COMMON, SymbolRuleTypeEnum.COMMON, null);
        return symbolArr;
    }


    private MalakasSymbolDo buildASymbol(SymbolEnum symbol, SymbolTypeEnum symbolType, SymbolRuleTypeEnum symbolRuleType, BigDecimal ruleValue) {
        MalakasSymbolDo symbolDo = new MalakasSymbolDo();
        symbolDo.setId(symbol.getId());
        symbolDo.setName(symbol.getName());
        symbolDo.setSymbolType(symbolType);
        return symbolDo;
    }

    private MalakasSymbolDo buildASymbol(SymbolEnum symbol, SymbolTypeEnum symbolType, SymbolRuleTypeEnum symbolRuleType, SymbolSizeEnum symbolSizeEnum, BigDecimal ruleValue) {
        MalakasSymbolDo symbolDo = new MalakasSymbolDo();
        symbolDo.setId(symbol.getId());
        symbolDo.setName(symbol.getName());
        symbolDo.setSymbolType(symbolType);
        symbolDo.setSymbolSize(symbolSizeEnum);
        return symbolDo;
    }

    private MalakasSymbolDo buildAPlaceHolderSymbol() {
        return MalakasSymbolDo.builder()
                .id(Symbol1000.getId())
                .name(Symbol1000.getName())
                .symbolType(SymbolTypeEnum.COMMON)
                .build();
    }
}