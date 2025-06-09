package com.wegame.capi.test.biz.module.bakunawa.game;

import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.module.bakunawa.game.BakunawaGameNormalPlay;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.test.BaseTest;
import com.wegame.capi.test.biz.module.ibongadarna.game.GameTestMocker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
public class BukunawaGameTest extends BaseTest {

    @Autowired
    BakunawaGameNormalPlay bakunawaGameNormalPlay;

    @Before
    public void before() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("1")
                .userName("cptest001")
                .pId("50")
                .channelId("1050")
                .nickName("cptest001")
                .playerId("cptest001")
                .playerName("cptest001")
                .build());
    }

    @Test
    public void play_normal_test() {
        for (int i = 0; i < 100; i++) {
            List<BakunawaGameRecordDo> result = bakunawaGameNormalPlay.play(GameTestMocker.mockGamePlayDo(BetSubTypeEnum.NORMAL_BET, 100L));
            assertNotNull(result);
            assertEquals(1, result.size());
            assertNotNull(result.get(0).getWheel());
        }
    }

}