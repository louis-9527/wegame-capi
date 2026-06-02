package com.wegame.capi.test.biz.service;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.biz.domain.biz.BetRecordDo;
import com.wegame.capi.biz.domain.biz.UserMetricInfoDo;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.SingleWalletBetResultDo;
import com.wegame.capi.biz.service.UserMetricService;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.common.enums.BetPromotionTypeEnum;
import com.wegame.capi.common.enums.BetStatusEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserMetricServiceTest extends BaseTest {

    @Autowired
    UserMetricService userMetricService;

    @BeforeAll
    public void beforeTest() {
        Map<String, Object> ext = new HashMap<>();
        ext.put("at", "template");
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .userName("altestwer")
                .playerId("altestwer")
                .playerName("altestwer")
                .pId("50")
                .channelId("1050")
                .gameId(GameTypeEnum.LAM_ANG.getId())
                .ext(ext)
                .build());
        OperatorContext.setOperator(Operator.builder()
                .name("system")
                .userId(10000L)
                .playerId("10000")
                .build());
        ThreadMdcUtil.setTraceIdIfAbsent();
    }

    @Test
    public void test_afterLoginMetric() {
        beforeTest();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        Date registerTime = DateUtil.getYesterdayBegin();
        Date lastLoginTime = DateUtil.getDayBegin(new Date());
        for (int i = 0; i < 10; i++) {
            userMetricService.afterLoginMetric(userInfo, registerTime, lastLoginTime);
            UserMetricInfoDo infoDo = userMetricService.getUserMetricInfo(userInfo);
            log.info("test_afterLoginMetric, infoDo: {}", infoDo);
        }
    }

    @Test
    public void test_afterBetMetric() {
        beforeTest();
        UserInfo userInfo = UserInfoContext.getUserInfo();
        BetRecordDo betRecordDo = BetRecordDo.builder()
                .payOutAmount(1000L)
                .payAmount(10L)
                .betPromotionType(BetPromotionTypeEnum.NONE.getCode())
                .build();
        SingleWalletBetResultDo result = SingleWalletBetResultDo.builder()
                .betStatus(BetStatusEnum.SUCCESS)
                .credit(100L)
                .build();
        for (int i = 0; i < 100; i++) {
            boolean hasRewarFreeWheel = false;
            BetRecordDo.ExtendInfo extendInfo = new BetRecordDo.ExtendInfo();
            extendInfo.setInterventionRuleId("ruleId" + i);
            betRecordDo.setExtendInfo(extendInfo);
            userMetricService.afterBetMetric(userInfo, betRecordDo, result, hasRewarFreeWheel);
            UserMetricInfoDo infoDo = userMetricService.getUserMetricInfo(userInfo);
            log.info("test_afterLoginMetric, infoDo: {}", infoDo);
        }
    }
}
