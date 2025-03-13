package com.wegame.capi.test.api;

import com.alibaba.fastjson.JSON;
import com.wegame.base.utils.DateUtil;
import com.wegame.capi.api.domain.req.promotion.GetBagCurrentRewardReq;
import com.wegame.capi.api.domain.res.promotion.GetBagCurrentRewardRes;
import com.wegame.capi.api.promotion.BagApi;
import com.wegame.capi.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class BagApiTest extends BaseTest {
    @Autowired
    private BagApi bagApi;

    @Test
    public void test_getRewardBag_SUCCESS(){
        GetBagCurrentRewardReq bagReq = GetBagCurrentRewardReq.builder()
                .pId("50")
                .channelId("1050")
                .gameCode("zhumeng002")
                .playerName("slottest01")
                .type(1)
                .refId("ad4b269c-22ac-4262-b305-56272ce52226711")
                .createTime(DateUtil.toUTC(new Date()))
                .build();

        GetBagCurrentRewardRes rewardBag = bagApi.getBagCurrentReward(bagReq);
        System.out.println(JSON.toJSONString(rewardBag));
    }
}
