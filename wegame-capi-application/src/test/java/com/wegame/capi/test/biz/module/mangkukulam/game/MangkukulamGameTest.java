package com.wegame.capi.test.biz.module.mangkukulam.game;//package com.wegame.capi.test.biz.module.game;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.MangkukulamSymbolDo;
import com.wegame.capi.biz.domain.biz.wheel.WheelDo;
import com.wegame.capi.biz.module.mangkukulam.MangkukulamSymbolUtil;
import com.wegame.capi.biz.module.mangkukulam.game.MangkukulamGameNormalPlay;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.enums.VolatilityEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
public class MangkukulamGameTest extends BaseTest {
    @Autowired
    MangkukulamGameNormalPlay normalPlay;
    private static final int MAX_TIMES_IN_MS = 5;


    private static final List<Integer> SPECIAL_WILD_EXISTS_COLUMNS = Arrays.asList(1, 2, 3);
    private static final List<Integer> FIRE_BORDER_EXISTS_COLUMNS = Arrays.asList(1, 2, 3, 4);
    private static final List<Integer> WILD_EXISTS_COLUMNS = Arrays.asList(1, 2, 3, 4);

    @Test
    public void testAll() {
        long currentTime = System.currentTimeMillis();
        int maxTimes = 10;
        for (int i = 0; i < maxTimes; i++) {
            test_buy_bet();
            test_normal_bet();
            test_normal_bet_continue_play();
        }

        long lastTime = System.currentTimeMillis();
        long cost = lastTime - currentTime;
        System.out.println("testAll cost : " + cost + "ms");
    }

    @Before
    public void beforeTest() {
        ThreadMdcUtil.setTraceIdIfAbsent();
        UserInfo userInfo = UserInfo.builder()
                .id("1001")
                .pId("50")
                .channelId("1050")
                .gameId(GameTypeEnum.LAM_ANG.getId())
                .playerId(TEST_PLAYER_ID)
                .playerName(TEST_PLAYER_NAME)
                .userName(TEST_USER_NAME)
                .nickName(TEST_NICK_NAME)
                .userLevel(TEST_USER_LEVEL)
                .token(TEST_TOKEN)
                .build();
        UserInfoContext.setUserInfo(userInfo);
        OperatorContext.setOperator(Operator.builder()
                .name(userInfo.getUserName())
                .userId(Long.valueOf(userInfo.getId()))
                .playerId(userInfo.getPlayerId())
                .build());
    }

    @Test
    public void test_normal_bet_continue_play() {
        GamePlayDo.ContinuePlayContext context = GamePlayDo.ContinuePlayContext.builder()
                .parentBetRecordId(new Random(1000000000).nextLong())
                .parentGameRecordId(new Random(1000000000).nextLong())
                .freeWheelTimes(15)
                .totalPayoutAmount(15000L).build();
        GamePlayDo playDo = GamePlayDo.builder()
                .gameType(GameTypeEnum.MANGKUKULAM)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.FREE_WHEEL_NORMAL_BET)
                .betAmount(1000L)
                .volatility(VolatilityEnum.HIGH)
                .continuePlayContext(context)
                .build();
        long currentTime = System.currentTimeMillis();
        List<MangkukulamGameRecordDo> recordList = normalPlay.play(playDo);
        long lastTime = System.currentTimeMillis();
        long cost = lastTime - currentTime;
        if (cost > MAX_TIMES_IN_MS) {
            System.out.println("test_buy_bet cost more than " + MAX_TIMES_IN_MS + "ms : " + cost + "ms");
        }
        recordList.forEach(record -> {
            validateGameRecord(record);
            MangkukulamSymbolUtil.print(record.getWheel().getSymbolArr(), record.getWheel().getIndex());
        });
        // 15轮
        Assert.assertTrue(recordList.size() >= 15);
    }

    @Test
    public void test_normal_bet() {
        GamePlayDo playDo = GamePlayDo.builder()
                .gameType(GameTypeEnum.MANGKUKULAM)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .betAmount(100L)
                .build();
        long currentTime = System.currentTimeMillis();
        List<MangkukulamGameRecordDo> recordList = normalPlay.play(playDo);
        long lastTime = System.currentTimeMillis();
        long cost = lastTime - currentTime;
        if (cost > MAX_TIMES_IN_MS) {
            System.out.println("test_buy_bet cost more than " + MAX_TIMES_IN_MS + "ms : " + cost + "ms");
        }
        recordList.forEach(record -> {
            validateGameRecord(record);
            MangkukulamSymbolUtil.print(record.getWheel().getSymbolArr(), record.getWheel().getIndex());
        });
    }

    @Test
    public void test_buy_bet() {
        GamePlayDo playDo = GamePlayDo.builder()
                .gameType(GameTypeEnum.MANGKUKULAM)
                .betType(BetTypeEnum.BUY_BET)
                .betSubType(BetSubTypeEnum.BUY_BET)
                .volatility(VolatilityEnum.HIGH)
                .betAmount(1000L)
                .payAmount(35000L)
                .build();
        long currentTime = System.currentTimeMillis();
        List<MangkukulamGameRecordDo> recordList = normalPlay.play(playDo);
        long lastTime = System.currentTimeMillis();
        long cost = lastTime - currentTime;
        if (cost > MAX_TIMES_IN_MS) {
            System.out.println("test_buy_bet cost more than " + MAX_TIMES_IN_MS + "ms : " + cost + "ms");
        }
        // 校验
        //// 1.是否为空
        Assert.assertNotNull(recordList);
        //// 2.校验数据条数（3个scatter 10轮 + 1 初始轮）
//        Assert.assertTrue(recordList.size() > 7);

        // 打印
        recordList.forEach(record -> {
            validateGameRecord(record);
            MangkukulamSymbolUtil.print(record.getWheel().getSymbolArr(), record.getWheel().getIndex());
        });
    }

    private void validateGameRecord(MangkukulamGameRecordDo gameRecordDo) {
        Assert.assertNotNull(gameRecordDo);
        WheelDo<MangkukulamSymbolDo> wheelDo = gameRecordDo.getWheel();
        Assert.assertNotNull(wheelDo);
        MangkukulamSymbolDo[][] symbolArr = wheelDo.getSymbolArr();
        for (int i = 0; i < 5; i++) {
            int specialWildCount = MangkukulamSymbolUtil.countSpecialWildInColumn(symbolArr, i);
            int normalWildCount = MangkukulamSymbolUtil.countNormalWildInColumn(symbolArr, i);
            int fireBorderCount = MangkukulamSymbolUtil.countFireBorderInColumn(symbolArr, i);
            int scatterCount = MangkukulamSymbolUtil.countScatterInColumn(symbolArr, i);
            if (SPECIAL_WILD_EXISTS_COLUMNS.contains(i)) {
                Assert.assertTrue(specialWildCount <= 1);
                if (specialWildCount == 1) {
                    Assert.assertEquals(0, fireBorderCount);
                    Assert.assertEquals(0, scatterCount);
                    Assert.assertEquals(0, normalWildCount);
                }
            } else {
                Assert.assertEquals(0, specialWildCount);
            }
            if (!FIRE_BORDER_EXISTS_COLUMNS.contains(i)) {
                Assert.assertEquals(0, fireBorderCount);
            }
            if (!WILD_EXISTS_COLUMNS.contains(i)) {
                Assert.assertEquals(0, normalWildCount);
            }
        }
    }
}