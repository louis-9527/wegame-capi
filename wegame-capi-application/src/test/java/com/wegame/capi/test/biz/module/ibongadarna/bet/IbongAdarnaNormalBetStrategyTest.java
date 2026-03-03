//package com.wegame.capi.test.biz.module.ibongadarna.bet;
//
//import com.wegame.base.context.OperatorContext;
//import com.wegame.base.utils.OperatorUtil;
//import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
//import com.wegame.capi.biz.domain.biz.game.IbongAdarnaGameRecordDo;
//import com.wegame.capi.biz.module.ibongadarna.bet.strategy.IbongAdarnaNormalBetStrategy;
//import com.wegame.capi.client.context.UserInfo;
//import com.wegame.capi.client.context.UserInfoContext;
//import com.wegame.capi.client.enums.BetSubTypeEnum;
//import com.wegame.capi.test.BaseTest;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import static org.junit.Assert.*;
//
//@Slf4j
//public class IbongAdarnaNormalBetStrategyTest extends BaseTest {
//    @Autowired
//    IbongAdarnaNormalBetStrategy ibongAdarnaNormalBetStrategy;
//
//    @Before
//    public void before() {
//        UserInfoContext.setUserInfo(UserInfo.builder()
//                .id("1")
//                .userName("jayniTestName")
//                .pId("50")
//                .channelId("1050")
//                .nickName("jayniTestName")
//                .playerId("jayniTestPlayId")
//                .playerName("jayniTestName")
//                .build());
//        OperatorContext.setOperator(OperatorUtil.getOperator());
//    }
//
//    @Test
//    public void bet_normal_test() {
//        BetResultDo<IbongAdarnaGameRecordDo> result = ibongAdarnaNormalBetStrategy.bet(BetTestMocker.mockGamePlayDo(BetSubTypeEnum.NORMAL_BET, 100L));
//        assertNotNull(result);
//        assertNotNull(result.getResultDetail());
//        assertNotNull(result.getResultDetail().getWheel());
//        assertNotNull(result.getResultDetail().getRewardResult());
//    }
//
//
//}
