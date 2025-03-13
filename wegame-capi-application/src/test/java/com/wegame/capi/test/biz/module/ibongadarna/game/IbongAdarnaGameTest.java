package com.wegame.capi.test.biz.module.ibongadarna.game;

import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
import com.wegame.capi.biz.module.ibongadarna.game.IbongAdarnaGame;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class IbongAdarnaGameTest extends BaseTest {

    @Autowired
    IbongAdarnaGame ibongAdarnaGame;

    @Before
    public void before() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("1")
                .userName("jayniTestName")
                .pId("50")
                .channelId("1050")
                .nickName("jayniTestName")
                .playerId("jayniTestPlayId")
                .playerName("jayniTestName")
                .build());
    }

    @Test
    public void play_normal_test() {
        List<IbongAdarnaGameRecordDo> result = ibongAdarnaGame.play(GameTestMocker.mockGamePlayDo(BetSubTypeEnum.NORMAL_BET, 100L));
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getWheel());
    }

}