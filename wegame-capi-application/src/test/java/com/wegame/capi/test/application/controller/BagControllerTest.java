package com.wegame.capi.test.application.controller;

import com.alibaba.fastjson.JSON;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.application.controller.promotion.BagController;
import com.wegame.capi.client.domain.request.promotion.QueryBagCurrentGameRewardRequest;
import com.wegame.capi.client.domain.request.promotion.QueryBagOtherGameRewardRequest;
import com.wegame.capi.client.domain.vo.promotion.BagCurrentRewordVo;
import com.wegame.capi.client.domain.vo.promotion.BagOtherRewordVo;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.test.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BagControllerTest extends BaseTest {
    @Autowired
    private BagController bagController;

    @Test
    public void queryBagCurrentGameReward(){
        UserInfo userInfo = new UserInfo();
        userInfo.setPId("50");
        userInfo.setChannelId("1050");
        userInfo.setGameId("6001");
        userInfo.setPlayerId("slottest01");
        userInfo.setPlayerName("slottest01");
        UserInfoContext.setUserInfo(userInfo);

        QueryBagCurrentGameRewardRequest request = new QueryBagCurrentGameRewardRequest();
        CommonResponse<BagCurrentRewordVo> response = bagController.queryBagCurrentGameReward(request);

        System.out.println("result:"+ JSON.toJSONString(response));
    }

    @Test
    public void queryBagOtherGameReward(){
        UserInfo userInfo = new UserInfo();
        userInfo.setPId("50");
        userInfo.setChannelId("1050");
        userInfo.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
        userInfo.setPlayerId("slottest01");
        userInfo.setPlayerName("slottest01");
        UserInfoContext.setUserInfo(userInfo);

        QueryBagOtherGameRewardRequest request = new QueryBagOtherGameRewardRequest();
        CommonResponse<BagOtherRewordVo> response = bagController.queryBagOtherGameReward(request);

        System.out.println("result:"+ JSON.toJSONString(response));
    }

}
