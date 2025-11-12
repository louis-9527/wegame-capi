package com.wegame.capi.test.biz.module.malakas.game;//package com.wegame.capi.test.biz.module.game;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.domain.biz.symbol.MalakasSymbolDo;
import com.wegame.capi.biz.module.malakas.MalakasSymbolUtil;
import com.wegame.capi.biz.module.malakas.game.MalakasGameNormalPlay;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.SymbolEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wegame.capi.biz.module.malakas.MalakasSymbolUtil.countIndexSymbol;
import static com.wegame.capi.common.constants.GameConstants.*;

@Slf4j
public class MalakasGameTest extends BaseTest {

    @Autowired
    MalakasGameNormalPlay malakasGame;

    @Before
    public void beforeTest() {
        ThreadMdcUtil.setTraceIdIfAbsent();
        UserInfo userInfo = UserInfo.builder()
                .id("10000")
                .pId("55")
                .channelId("1051")
                .gameId(GameTypeEnum.MARIA_MAKILING.getId())
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
    public void test_play_normal_bet() {
        GamePlayDo playDo = GamePlayDo.builder()
                .gameType(GameTypeEnum.MALAKAS)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .betAmount(100L)
                .build();
        List<MalakasGameRecordDo> recordList = malakasGame.play(playDo);
        recordList.forEach(item -> {
            MalakasSymbolUtil.print(item.getWheel().getSymbolArr(), "test");
        });
    }

    @Test
    public void test_play_buy_bet() {
        GamePlayDo playDo = GamePlayDo.builder()
                .gameType(GameTypeEnum.MALAKAS)
                .betType(BetTypeEnum.BUY_BET)
                .betSubType(BetSubTypeEnum.BUY_BET)
                .betAmount(100L)
                .payAmount(10000L)
                .build();
        List<MalakasGameRecordDo> recordList = malakasGame.play(playDo);
        // 校验
        //// 1.是否为空
        Assert.assertNotNull(recordList);
        //// 2.校验数据条数（至少中4个scatter对应获得8轮+启动轮至少1条，所以gameRecord至少9条）
        Assert.assertTrue(recordList.size() >= 9);
        //// 3.校验数据scatter个数
        recordList.forEach(gameRecord -> validScatterNumberInColumn(gameRecord.getWheel().getSymbolArr()));

        MalakasGameRecordDo firstWheelFirstGameRecord = recordList.get(0);
        Assert.assertTrue(Objects.equals(firstWheelFirstGameRecord.getParentId(), 0L));
        List<MalakasGameRecordDo> firstWheelGameRecordList = recordList.stream()
                .filter(item -> Objects.equals(item.getBatchId(), firstWheelFirstGameRecord.getId()))
                .sorted(Comparator.comparing(MalakasGameRecordDo::getIndex))
                .collect(Collectors.toList());
        MalakasGameRecordDo firstWheelLastGameRecord = firstWheelGameRecordList.get(firstWheelGameRecordList.size() - 1);
        MalakasSymbolDo[][] symbolArr = firstWheelLastGameRecord.getWheel().getSymbolArr();
        // 购买启动轮最后回合，scatter个数在4-6之间
        int wheelScatterCount = MalakasSymbolUtil.countIndexSymbol(symbolArr, SymbolEnum.Scatter);
        Assert.assertTrue( 4 <= wheelScatterCount && wheelScatterCount <= 6);
        validScatterNumberInColumn(symbolArr);

        // 打印
        recordList.forEach(item -> MalakasSymbolUtil.print(item.getWheel().getSymbolArr(), "test"));
    }

    private void validScatterNumberInColumn(MalakasSymbolDo[][] symbolArr) {
        for (int column = ONE_COLUM_INDEX; column < SEVEN_COLUM_INDEX; column++) {
            // 一列最多包含两个scatter
            int scatterNumberInColumn = countIndexSymbol(symbolArr, SymbolEnum.Scatter, column);
            Assert.assertTrue(scatterNumberInColumn <= 2);
        }
    }
}