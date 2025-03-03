package com.wegame.capi.test.api;

import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.promotion.RedeemAchievementReq;
import com.wegame.capi.api.domain.res.promotion.RedeemAchievementRes;
import com.wegame.capi.api.promotion.AchievementApi;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Slf4j
public class AchievementApiTest extends BaseTest {
    @Autowired
    AchievementApi achievementApi;

    @Before
    public void beforeTest() {
        ThreadMdcUtil.setTraceIdIfAbsent();
    }

    @Test
    public void test_redeemAchievement() {
        RedeemAchievementRes res = achievementApi.redeemAchievement(buildRedeemAchievementReq());
        System.out.println("test_redeemAchievement ret: {}" + res);
        // todo assert
    }

    private static RedeemAchievementReq buildRedeemAchievementReq() {
        //        {
        //            "channelId"  : "1001",
        //                "pId" : "50",
        //                "playerName" : "1111100520",
        //                "createTime" : "2025-02-18T10:05:38.649Z",
        //                "refId" : "ad4b269c-22ac-4262-b305-56272ce56711",
        //                "achievementId" : "ACH-1"
        //            "redeemId" : "6797033a84338b06398431b9"
        //        }
        return RedeemAchievementReq.builder()
                .pId("50")
                .channelId("1001")
                .playerName("1111100520")
                .refId("ad4b269c-22ac-4262-b305-56272ce56711")
                .achievementId("ACH-1")
                .redeemId("6797033a84338b06398431b9")
                .createTime(DateUtil.toUTC(new Date()))
                .build();
    }
}
