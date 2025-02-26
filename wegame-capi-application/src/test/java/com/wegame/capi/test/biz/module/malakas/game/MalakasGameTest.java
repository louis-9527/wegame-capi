package com.wegame.capi.test.biz.module.malakas.game;//package com.wegame.capi.test.biz.module.game;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MalakasGameRecordDo;
import com.wegame.capi.biz.module.malakas.MalakasSymbolUtil;
import com.wegame.capi.biz.module.malakas.game.MalakasGame;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class MalakasGameTest extends BaseTest {

    @Autowired
    MalakasGame malakasGame;

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
        for (int i = 0; i < 100; i++) {
            List<MalakasGameRecordDo> recordList = malakasGame.play(playDo);
            if (recordList.size() > 1) {
                recordList.forEach(item -> {
                    MalakasSymbolUtil.print(item.getWheel().getSymbolArr(), "test");
                });
            }
            System.out.println("end");
        }
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
        for (int i = 0; i < 100; i++) {
            List<MalakasGameRecordDo> recordList = malakasGame.play(playDo);
            if (recordList.size() > 1) {
                recordList.forEach(item -> {
                    MalakasSymbolUtil.print(item.getWheel().getSymbolArr(), "test");
                });
            }
            System.out.println("end");
        }
    }
}