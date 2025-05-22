package com.wegame.capi.test.biz.module.bakunawa.bet;

import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.capi.biz.domain.biz.bet.BetDo;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.biz.domain.biz.game.BakunawaGameRecordDo;
import com.wegame.capi.biz.module.bakunawa.bet.strategy.BakunawaNormalBetStrategy;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.test.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BakunawaMultipleCalculateStrategyTest extends BaseTest {
    @Autowired
    BakunawaNormalBetStrategy bakunawaNormalBetStrategy;

    @BeforeAll
    public void beforeTest() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .userName("system")
                .build());
        OperatorContext.setOperator(Operator.builder()
                .name("system")
                .userId(10000L)
                .playerId("10000")
                .build());
    }

    @Test
    public void updateRewardNotifyInfo_test() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .userName("system")
                .build());
        BetDo betDo = new BetDo();
        betDo.setBetType(BetTypeEnum.NORMAL_BET);
        betDo.setBetSubType(BetSubTypeEnum.NORMAL_BET);
        betDo.setBetAmount(1000L);
        betDo.setPayAmount(1000L);
        BetResultDo<BakunawaGameRecordDo> betResult = bakunawaNormalBetStrategy.bet(betDo);
        Assert.assertNotNull(betResult);
    }
}
