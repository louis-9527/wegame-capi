package com.wegame.capi.test.application.controller.game.diwataMayari;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.application.controller.RewardControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class DiwataMayariRewardControllerTest extends RewardControllerTest {


    //@Before
    //public void beforeTest() {
    //    ThreadMdcUtil.setTraceIdIfAbsent();
    //    UserInfo userInfo = UserInfo.builder()
    //            .id("10000")
    //            .pId("55")
    //            .channelId("1051")
    //            .gameId(GameTypeEnum.MARIA_MAKILING.getId())
    //            .playerId(TEST_PLAYER_ID)
    //            .playerName(TEST_PLAYER_NAME)
    //            .userName(TEST_USER_NAME)
    //            .nickName(TEST_NICK_NAME)
    //            .userLevel(TEST_USER_LEVEL)
    //            .token(TEST_TOKEN)
    //            .build();
    //    UserInfoContext.setUserInfo(userInfo);
    //    OperatorContext.setOperator(Operator.builder()
    //            .name(userInfo.getUserName())
    //            .userId(Long.valueOf(userInfo.getId()))
    //            .playerId(userInfo.getPlayerId())
    //            .build());
    //}
    //
    //@Test
    //public void test_get() {
    //    for(int i = 0; i < 5; i++) {
    //        commonGet();
    //    }
    //}
}
