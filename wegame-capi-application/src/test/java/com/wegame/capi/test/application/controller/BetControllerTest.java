package com.wegame.capi.test.application.controller;

import cn.hutool.core.lang.Assert;
import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.domain.CommonResponse;
import com.wegame.capi.biz.converter.BetConverter;
import com.wegame.capi.biz.converter.BetResultConverter;
import com.wegame.capi.biz.domain.biz.bet.BetResultDo;
import com.wegame.capi.client.domain.vo.BetResultVo;
import com.wegame.capi.client.domain.vo.MariaMakilingBetResultVo;
import com.wegame.capi.common.context.UserInfo;
import com.wegame.capi.common.context.UserInfoContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import com.wegame.capi.biz.service.BetService;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.request.MariaMakilingBetRequest;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.BaseTest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BetControllerTest extends BaseTest {
    @Autowired
    BetService betService;

    @BeforeAll
    public static void beforeTest() {
        UserInfoContext.setUserInfo(UserInfo.builder()
                .id("10000")
                .userName("system")
                .build());
        OperatorContext.setOperator(Operator.builder().name("system").userId(10000L).playerId("10000").build());
    }

    @Test
    public void normal_bet_test() {
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
        betRequest.setBetType(BetTypeEnum.NORMAL_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.NORMAL_BET.getCode());

        MariaMakilingBetRequest betDetail = new MariaMakilingBetRequest();
        betDetail.setBetAmount(100L);
        betDetail.setPayAmount(100L);
        betRequest.setBetDetail(betDetail);

        BetResultDo betResultDo = betService.bet(BetConverter.requestToDo(betRequest));
        CommonResponse<BetResultVo> build = CommonResponse.build(BetResultConverter.doToVo(betResultDo));
        log.info("bet: {}", build);

        Assert.notNull(build);
        Assert.equals(build.getCode(), "1");
    }

    @Test
    public void buy_bet_test() {
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
        betRequest.setBetType(BetTypeEnum.BUY_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.BUY_BET.getCode());

        MariaMakilingBetRequest betDetail = new MariaMakilingBetRequest();
        betDetail.setBetAmount(100L);
        betDetail.setPayAmount(10000L);
        betRequest.setBetDetail(betDetail);

        BetResultDo betResultDo = betService.bet(BetConverter.requestToDo(betRequest));
        CommonResponse<BetResultVo> build = CommonResponse.build(BetResultConverter.doToVo(betResultDo));
        log.info("bet: {}", build);

        Assert.notNull(build);
        Assert.equals(build.getCode(), "1");
    }

    @Test
    public void buy_free_bet_test() {
        //        BetRequest betRequest = new BetRequest();
        //        betRequest.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
        //        betRequest.setBetType(BetTypeEnum.BUY_BET.getCode());
        //        betRequest.setBetSubType(BetSubTypeEnum.BUY_BET.getCode());
        //
        //        MariaMakilingBetRequest betDetail = new MariaMakilingBetRequest();
        //        betDetail.setBetAmount(100L);
        //        betDetail.setPayAmount(10000L);
        //        betRequest.setBetDetail(betDetail);
        //
        //        BetResultDo betResultDo = betService.bet(BetConverter.requestToDo(betRequest));
        //        CommonResponse<BetResultVo> build = CommonResponse.build(BetResultConverter.doToVo(betResultDo));
        //        log.info("bet: {}", build);
        //
        //        Assert.notNull(build);
        //        Assert.equals(build.getCode(), "1");
        //        BetResultVo data = build.getData();
        //        String betRecordId = data.getBetRecordId();
        //        MariaMakilingBetResultVo resultDetail = data.getResultDetail();
        //        List<Integer> freeSpinIds = resultDetail.getFreeSpinIds();
        //
        //        for (Integer freeId : freeSpinIds) {
        //            this.buy_free_bet_freeId(betRecordId,freeId);
        //        }
    }

    private void buy_free_bet_freeId(String recordId,Integer freeId){
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
        betRequest.setBetType(BetTypeEnum.BUY_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.FREE_SPINS_BUY_BET.getCode());

        MariaMakilingBetRequest betDetail = new MariaMakilingBetRequest();
        betDetail.setBetAmount(100L);
        betDetail.setBetRecordId(recordId);
        betDetail.setFreeSpinsId(freeId);
        betRequest.setBetDetail(betDetail);

        BetResultDo betResultDo = betService.bet(BetConverter.requestToDo(betRequest));
        CommonResponse<BetResultVo> build = CommonResponse.build(BetResultConverter.doToVo(betResultDo));
        log.info("bet: {}", build);
        Assert.notNull(build);
        Assert.equals(build.getCode(), "1");
    }

}
