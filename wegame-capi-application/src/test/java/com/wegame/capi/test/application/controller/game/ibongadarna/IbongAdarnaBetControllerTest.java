package com.wegame.capi.test.application.controller.game.ibongadarna;

import cn.hutool.core.lang.Assert;
import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.vo.BetResultVo;
import com.wegame.capi.client.domain.vo.ibongadarna.IbongAdarnaBetResultVo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.application.controller.BetControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

@Slf4j
public class IbongAdarnaBetControllerTest extends BetControllerTest {

    @BeforeAll
    public static void beforeTest() {
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
    public void test_bet_normal_bet() {
        beforeTest();
        BetRequest betRequest = buildNormalBetRequest();
        CommonResponse<BetResultVo> commonResponse = betController.bet(betRequest);
        Assert.notNull(commonResponse);
        Assert.notNull(commonResponse.getData());
        Assert.isTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
        Assert.isTrue(StringUtils.isNotEmpty(commonResponse.getData().getBetRecordId()));
        Assert.isTrue(commonResponse.getData().getResultDetail() instanceof IbongAdarnaBetResultVo);
        IbongAdarnaBetResultVo resultVo = (IbongAdarnaBetResultVo) commonResponse.getData().getResultDetail();
        Assert.notNull(resultVo);
    }

    //    @Test
    //    public void test_bet_buy_bet() {
    //        beforeTest();
    //        // 购买免费轮
    //        BetRequest betRequest = buildBuyBetRequest();
    //        CommonResponse<BetResultVo> commonResponse = betController.bet(betRequest);
    //        Assert.notNull(commonResponse);
    //        Assert.notNull(commonResponse.getData());
    //        Assert.isTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
    //        Assert.isTrue(StringUtils.isNotEmpty(commonResponse.getData().getBetRecordId()));
    //        Assert.isTrue(commonResponse.getData().getResultDetail() instanceof IbongAdarnaBetResultVo);
    //        IbongAdarnaBetResultVo resultVo = (IbongAdarnaBetResultVo) commonResponse.getData().getResultDetail();
    //        Assert.notNull(resultVo);
    //    }

    private BetRequest buildNormalBetRequest() {
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.IBONG_ADARNA.getId());
        betRequest.setBetType(BetTypeEnum.NORMAL_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.NORMAL_BET.getCode());
        BetRequest.BetDetail betDetail = new BetRequest.BetDetail();
        betDetail.setBetAmount(600L);
        betDetail.setPayAmount(600L);
        betRequest.setBetDetail(betDetail);
        return betRequest;
    }

    private BetRequest buildBuyBetRequest() {
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.IBONG_ADARNA.getId());
        betRequest.setBetType(BetTypeEnum.BUY_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.BUY_BET.getCode());
        BetRequest.BetDetail betDetail = new BetRequest.BetDetail();
        betDetail.setBetAmount(500L);
        betDetail.setPayAmount(50000L);
        betRequest.setBetDetail(betDetail);
        return betRequest;
    }
}
