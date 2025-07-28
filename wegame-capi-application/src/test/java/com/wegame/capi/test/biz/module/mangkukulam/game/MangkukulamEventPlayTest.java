package com.wegame.capi.test.biz.module.mangkukulam.game;//package com.wegame.capi.test.biz.module.game;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.domain.biz.game.GameOperationEventDo;
import com.wegame.capi.biz.domain.biz.game.GamePlayDo;
import com.wegame.capi.biz.domain.biz.game.MangkukulamGameRecordDo;
import com.wegame.capi.biz.module.mangkukulam.game.MangkukulamGameEventPlay;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GamePlayTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MangkukulamEventPlayTest extends BaseTest {
    @Autowired
    MangkukulamGameEventPlay eventPlay;

    // 倍率map
    private static final Map<Long, Long> MULTIPLE_MAP = new HashMap<>();
    // 游戏记录条数map
    private static final Map<Long, Integer> SIZE_MAP = new HashMap<>();

    static {
        MULTIPLE_MAP.put(1949738752390815744L, 325L);
        MULTIPLE_MAP.put(1949739161511616512L, 280L);

        SIZE_MAP.put(1949738752390815744L, 23);
        SIZE_MAP.put(1949739161511616512L, 26);
    }

    @Test
    public void testEventPlay() {
        Long betAmount = 2000L;
        for (Long betRecordId : MULTIPLE_MAP.keySet()) {
            List<MangkukulamGameRecordDo> result = eventPlay.play(createGamePlayDo(betAmount, betRecordId));
            Assert.assertEquals(SIZE_MAP.get(betRecordId).intValue(), result.size());
            long totalReward = result.stream()
                    .map(r -> r.getRewardResult().getTotalRewardAmount())
                    .mapToLong(amount -> amount).sum();
            Assert.assertEquals(betAmount * MULTIPLE_MAP.get(betRecordId), totalReward * 10);
        }
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

    private GamePlayDo createGamePlayDo(Long betAmount, Long configId) {
        return GamePlayDo.builder().gameType(GameTypeEnum.MANGKUKULAM)
                .betType(BetTypeEnum.NORMAL_BET)
                .betSubType(BetSubTypeEnum.NORMAL_BET)
                .betAmount(betAmount)
                .payAmount(betAmount)
                .gamePlayType(GamePlayTypeEnum.EVENT_PLAY)
                .gameEventDo(GameOperationEventDo.builder().eventResult(true).configId(configId).build())
                .build();
    }
}