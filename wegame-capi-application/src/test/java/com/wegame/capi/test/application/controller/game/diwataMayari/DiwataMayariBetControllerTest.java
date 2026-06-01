package com.wegame.capi.test.application.controller.game.diwataMayari;

import cn.hutool.core.lang.Assert;
import com.wegame.base.context.Operator;
import com.wegame.base.context.OperatorContext;
import com.wegame.base.domain.CommonResponse;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.client.context.UserInfo;
import com.wegame.capi.client.context.UserInfoContext;
import com.wegame.capi.client.domain.request.BetRequest;
import com.wegame.capi.client.domain.vo.BetResultVo;
import com.wegame.capi.client.domain.vo.diwatamayari.DiwataMayariBetResultVo;
import com.wegame.capi.client.enums.BetSubTypeEnum;
import com.wegame.capi.client.enums.BetTypeEnum;
import com.wegame.capi.client.enums.GameTypeEnum;
import com.wegame.capi.test.application.controller.BetControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DiwataMayariBetControllerTest extends BetControllerTest {

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
    public void test_bet_normal_bet() {
        beforeTest();
        for (long i = 0; i <= 10000000L; i++) {
            try {
                BetRequest betRequest = buildNormalBetRequest();
                CommonResponse<BetResultVo> commonResponse = betController.bet(betRequest);
                Assert.notNull(commonResponse);
                Assert.notNull(commonResponse.getData());
                Assert.isTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
                Assert.isTrue(StringUtils.isNotEmpty(commonResponse.getData().getBetRecordId()));
                log.info("test: {}", i);
            } catch (Exception e) {
                log.error(" test error: {}", i);
            }
        }
    }

    @Test
    public void test_bet_buy_bet() {
        beforeTest();
        for (long i = 0; i <= 10000000L; i++) {
            // 购买免费轮
            try {
                BetRequest betRequest = buildBuyBetRequest();
                CommonResponse<BetResultVo> commonResponse = betController.bet(betRequest);
                Assert.notNull(commonResponse);
                Assert.notNull(commonResponse.getData());
                Assert.isTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
                Assert.isTrue(StringUtils.isNotEmpty(commonResponse.getData().getBetRecordId()));
                Assert.isTrue(commonResponse.getData().getResultDetail() instanceof DiwataMayariBetResultVo);
                DiwataMayariBetResultVo resultVo = (DiwataMayariBetResultVo) commonResponse.getData().getResultDetail();
                Assert.notNull(resultVo);
                log.error("test : {}", i);
            } catch (Exception e) {
                log.error("test error: {}", i);
            }
            // 查询免费轮
            //String betRecordId = commonResponse.getData().getBetRecordId();
            //List<String> gameRecordIds = resultVo.getGameRecordIds();
            //        for (String gameRecordId : CollectionUtils.emptyIfNull(gameRecordIds)) {
            //            BetRequest queryBetRequest = buildQueryBuyBetRequest(gameRecordId, betRecordId);
            //            CommonResponse<BetResultVo> res = betController.bet(queryBetRequest);
            //            Assert.notNull(res);
            //            Assert.notNull(res.getData());
            //            Assert.isTrue(StringUtils.equals(res.getCode(), CommonResponse.SUC_CODE));
            //            Assert.isTrue(StringUtils.isNotEmpty(res.getData().getBetRecordId()));
            ////            Assert.isTrue(commonResponse.getData().getResultDetail() instanceof MariaMakilingBetResultVo);
            ////            MariaMakilingBetResultVo vo = (MariaMakilingBetResultVo) commonResponse.getData().getResultDetail();
            ////            Assert.notNull(vo);
            //        }
        }
    }

    private BetRequest buildNormalBetRequest() {
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.DIWATA_MAYARI.getId());
        betRequest.setBetType(BetTypeEnum.NORMAL_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.NORMAL_BET.getCode());
        BetRequest.BetDetail betDetail = new BetRequest.BetDetail();
        betDetail.setBetAmount(2 * 1000L);
        betDetail.setPayAmount(2 * 1000L);
        betRequest.setBetDetail(betDetail);
        return betRequest;
    }

    private BetRequest buildBuyBetRequest() {
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.DIWATA_MAYARI.getId());
        betRequest.setBetType(BetTypeEnum.BUY_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.BUY_BET.getCode());
        BetRequest.BetDetail betDetail = new BetRequest.BetDetail();
        betDetail.setBetAmount(45 * 1000L);
        betDetail.setPayAmount(45 * 75 * 1000L);
        betRequest.setBetDetail(betDetail);
        return betRequest;
    }

    private static BetRequest buildQueryBuyBetRequest(String gameRecordId, String betRecordId) {
        BetRequest betRequest = new BetRequest();
        betRequest.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
        betRequest.setBetType(BetTypeEnum.BUY_BET.getCode());
        betRequest.setBetSubType(BetSubTypeEnum.FREE_SPINS_BUY_BET.getCode());

        BetRequest.BetDetail betDetail = new BetRequest.BetDetail();
        betDetail.setBetAmount(1000L);
        betDetail.setPayAmount(0L);
        betDetail.setBetRecordId(betRecordId);
        betDetail.setGameRecordId(gameRecordId);
        betRequest.setBetDetail(betDetail);
        return betRequest;
    }
}
