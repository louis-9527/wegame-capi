package com.wegame.capi.test.application.controller.game.malakas;

import cn.hutool.core.lang.Assert;
import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.vo.BetResultVo;
import com.wegame.capi.client.domain.vo.malakas.MalakasBetResultDetailVo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.application.controller.BetControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class MalakasBetControllerTest2 extends BetControllerTest {

    @BeforeAll
    public static void beforeTest() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .pId("50")
                .channelId("1050")
                .userName("system")
                .build());
        OperatorContext.setOperator(Operator.builder()
                .name("system")
                .userId(10000L)
                .playerId("10000")
                .build());
    }

    @Test
    public void test_bet_normal_bet() {
        beforeTest();
        for (int i = 0; i < 10000; i ++) {
            try {
                BetRequest betRequest = buildNormalBetRequest();
                CommonResponse<BetResultVo> commonResponse = betController.bet(betRequest);
                Assert.notNull(commonResponse);
                Assert.notNull(commonResponse.getData());
                Assert.isTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
                Assert.isTrue(StringUtils.isNotEmpty(commonResponse.getData().getBetRecordId()));
                Assert.isTrue(commonResponse.getData().getResultDetail() instanceof MalakasBetResultDetailVo);
                MalakasBetResultDetailVo resultVo = (MalakasBetResultDetailVo) commonResponse.getData().getResultDetail();
                Assert.notNull(resultVo);
            } catch (Exception e) {
                log.error("test_bet_normal_bet error", e);
            }
        }
    }

    @Test
    public void test_bet_buy_bet() {
        beforeTest();
        // 购买免费轮
        BetRequest betRequest = buildBuyBetRequest();
        CommonResponse<BetResultVo> commonResponse = betController.bet(betRequest);
        Assert.notNull(commonResponse);
        Assert.notNull(commonResponse.getData());
        Assert.isTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
        Assert.isTrue(StringUtils.isNotEmpty(commonResponse.getData().getBetRecordId()));
        Assert.isTrue(commonResponse.getData().getResultDetail() instanceof MalakasBetResultDetailVo);
        MalakasBetResultDetailVo resultVo = (MalakasBetResultDetailVo) commonResponse.getData().getResultDetail();
        Assert.notNull(resultVo);
    }

    private BetRequest buildNormalBetRequest() {
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.MALAKAS.getId());
        betRequest.setBetType(BetTypeEnum.NORMAL_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.NORMAL_BET.getCode());
        BetRequest.BetDetail betDetail = new BetRequest.BetDetail();
        long betAmount = generateRandomNumberInRandomRange().multiply(new BigDecimal(1000)).longValue();
        betDetail.setBetAmount(betAmount);
        betDetail.setPayAmount(betAmount);
        betRequest.setBetDetail(betDetail);
        return betRequest;
    }

    private BetRequest buildBuyBetRequest() {
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.MALAKAS.getId());
        betRequest.setBetType(BetTypeEnum.BUY_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.BUY_BET.getCode());
        BetRequest.BetDetail betDetail = new BetRequest.BetDetail();
        long betAmount = generateRandomNumberInRandomRange().multiply(new BigDecimal(1000)).longValue();
        betDetail.setBetAmount(betAmount);
        betDetail.setPayAmount(betAmount * 35);
        betRequest.setBetDetail(betDetail);
        return betRequest;
    }

    private static BetRequest buildQueryBuyBetRequest(String gameRecordId, String betRecordId) {
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.MALAKAS.getId());
        betRequest.setBetType(BetTypeEnum.BUY_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.FREE_SPINS_BUY_BET.getCode());
        BetRequest.BetDetail betDetail = new BetRequest.BetDetail();
        betDetail.setBetAmount(100L);
        betDetail.setPayAmount(0L);
        betDetail.setBetRecordId(betRecordId);
        betDetail.setGameRecordId(gameRecordId);
        betRequest.setBetDetail(betDetail);
        return betRequest;
    }

    // 定义范围列表
    private static final List<String> RANGE_LIST = new ArrayList<>();
    // 随机数生成器（推荐用Random，如需线程安全可改用ThreadLocalRandom）
    private static final Random RANDOM = new Random();

    static {
        // 初始化范围数据
        RANGE_LIST.add("0.3_3");
        RANGE_LIST.add("5_40");
        RANGE_LIST.add("50_500");
        RANGE_LIST.add("1000_10000");
    }

    /**
     * 核心方法：随机选择一个范围，生成该范围内的随机数（保留1位小数）
     * @return 符合要求的随机数（BigDecimal类型，避免浮点精度问题）
     */
    public static BigDecimal generateRandomNumberInRandomRange() {
        // 1. 随机选择一个范围字符串
        String randomRangeStr = RANGE_LIST.get(RANDOM.nextInt(RANGE_LIST.size()));

        // 2. 分割范围字符串为左右边界（下划线分割）
        String[] rangeParts = randomRangeStr.split("_");
        if (rangeParts.length != 2) {
            throw new IllegalArgumentException("范围格式错误：" + randomRangeStr);
        }

        // 3. 解析左右边界为BigDecimal（避免double精度丢失）
        BigDecimal min = new BigDecimal(rangeParts[0]);
        BigDecimal max = new BigDecimal(rangeParts[1]);

        // 4. 生成[min, max]范围内的随机数（保留1位小数）
        return generateRandomNumber(min, max);
    }

    /**
     * 生成指定范围内的随机数，保留1位小数
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @return 保留1位小数的随机数
     */
    private static BigDecimal generateRandomNumber(BigDecimal min, BigDecimal max) {
        // 校验边界合法性
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("最小值不能大于最大值：min=" + min + ", max=" + max);
        }
        // 生成0-1之间的随机小数
        double randomDouble = RANDOM.nextDouble();
        // 计算范围差值
        BigDecimal diff = max.subtract(min);
        // 计算随机偏移量：diff * 随机数
        BigDecimal offset = diff.multiply(BigDecimal.valueOf(randomDouble));
        // 最终随机数 = 最小值 + 偏移量
        BigDecimal randomNum = min.add(offset);

        // 保留1位小数（四舍五入，也可根据需求改为DOWN/TRUNCATED）
        return randomNum.setScale(1, RoundingMode.HALF_UP);
    }
}
