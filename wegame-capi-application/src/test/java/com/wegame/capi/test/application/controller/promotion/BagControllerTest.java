package com.wegame.capi.test.application.controller.promotion;

import com.alibaba.fastjson.JSON;
import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.application.controller.promotion.BagController;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.request.promotion.QueryBagCurrentGameRewardRequest;
import com.wegame.capi.client.domain.request.promotion.QueryBagOtherGameRewardRequest;
import com.wegame.capi.client.domain.vo.promotion.BagCurrentRewardVo;
import com.wegame.capi.client.domain.vo.promotion.BagOtherRewardVo;
import com.wegame.capi.test.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BagControllerTest extends BaseTest {

    @Autowired
    BagController bagController;

    @Before
    public void beforeTest() {
        ThreadMdcUtil.setTraceIdIfAbsent();
        //        UserInfo userInfo = UserInfo.builder()
        //                .id("10000")
        //                .pId("55")
        //                .channelId("1051")
        //                .gameId(GameTypeEnum.MARIA_MAKILING.getId())
        //                .playerId(TEST_PLAYER_ID)
        //                .playerName(TEST_PLAYER_NAME)
        //                .userName(TEST_USER_NAME)
        //                .nickName(TEST_NICK_NAME)
        //                .userLevel(TEST_USER_LEVEL)
        //                .token(TEST_TOKEN)
        //                .build();
        //        UserInfoContext.setUserInfo(userInfo);
        UserInfo userInfo = new UserInfo();
        userInfo.setPId("50");
        userInfo.setChannelId("1050");
        userInfo.setGameId("6001");
        userInfo.setPlayerId("slottest01");
        userInfo.setPlayerName("slottest01");
        UserInfoContext.setUserInfo(userInfo);
        OperatorContext.setOperator(Operator.builder()
                .name(userInfo.getUserName())
                .userId(Long.valueOf(userInfo.getId()))
                .playerId(userInfo.getPlayerId())
                .build());
    }

    @Test
    public void test_queryBagCurrentGameReward() {
        QueryBagCurrentGameRewardRequest request = new QueryBagCurrentGameRewardRequest();
        CommonResponse<BagCurrentRewardVo> response = bagController.queryBagCurrentGameReward(request);
        // todo 断言 @tutu
        System.out.println("result:" + JSON.toJSONString(response));
    }

    @Test
    public void test_queryBagOtherGameReward() {
        QueryBagOtherGameRewardRequest request = new QueryBagOtherGameRewardRequest();
        CommonResponse<BagOtherRewardVo> response = bagController.queryBagOtherGameReward(request);
        // todo 断言 @tutu
        System.out.println("result:" + JSON.toJSONString(response));
    }

}
