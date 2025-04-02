package com.wegame.capi.test.application.controller.game.malakas;

import com.wegame.capi.test.application.controller.BetControllerTest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MalakasBetControllerTest extends BetControllerTest {

    //    @BeforeAll
    //    public static void beforeTest() {
    //        UserInfoContext.setUserInfo(UserInfo.builder()
    //                .id("10000")
    //                .userName("system")
    //                .build());
    //        OperatorContext.setOperator(Operator.builder()
    //                .name("system")
    //                .userId(10000L)
    //                .playerId("10000")
    //                .build());
    //    }
    //
    //    @Test
    //    public void test_bet_normal_bet() {
    //        beforeTest();
    //        BetRequest betRequest = buildNormalBetRequest();
    //        CommonResponse<BetResultVo> commonResponse = betController.bet(betRequest);
    //        Assert.notNull(commonResponse);
    //        Assert.notNull(commonResponse.getData());
    //        Assert.isTrue(StringUtils.equals(commonResponse.getCode(), CommonResponse.SUC_CODE));
    //        Assert.isTrue(StringUtils.isNotEmpty(commonResponse.getData().getBetRecordId()));
    //        Assert.isTrue(commonResponse.getData().getResultDetail() instanceof MariaMakilingBetResultVo);
    //        MariaMakilingBetResultVo resultVo = (MariaMakilingBetResultVo) commonResponse.getData().getResultDetail();
    //        Assert.notNull(resultVo);
    //    }
    //
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
    //        Assert.isTrue(commonResponse.getData().getResultDetail() instanceof MariaMakilingBetResultVo);
    //        MariaMakilingBetResultVo resultVo = (MariaMakilingBetResultVo) commonResponse.getData().getResultDetail();
    //        Assert.notNull(resultVo);
    //        // 查询免费轮
    //        String betRecordId = commonResponse.getData().getBetRecordId();
    //        List<String> gameRecordIds = resultVo.getGameRecordIds();
    //        for (String gameRecordId : CollectionUtils.emptyIfNull(gameRecordIds)) {
    //            BetRequest queryBetRequest = buildQueryBuyBetRequest(gameRecordId, betRecordId);
    //            CommonResponse<BetResultVo> res = betController.bet(queryBetRequest);
    //            Assert.notNull(res);
    //            Assert.notNull(res.getData());
    //            Assert.isTrue(StringUtils.equals(res.getCode(), CommonResponse.SUC_CODE));
    //            Assert.isTrue(StringUtils.isNotEmpty(res.getData().getBetRecordId()));
    //            Assert.isTrue(commonResponse.getData().getResultDetail() instanceof MariaMakilingBetResultVo);
    //            MariaMakilingBetResultVo vo = (MariaMakilingBetResultVo) commonResponse.getData().getResultDetail();
    //            Assert.notNull(vo);
    //        }
    //    }
    //
    //    private BetRequest buildNormalBetRequest() {
    //        BetRequest betRequest = new BetRequest();
    //        betRequest.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
    //        betRequest.setBetType(BetTypeEnum.NORMAL_BET.getCode());
    //        betRequest.setBetSubType(BetSubTypeEnum.NORMAL_BET.getCode());
    //        MariaMakilingBetRequest betDetail = new MariaMakilingBetRequest();
    //        betDetail.setBetAmount(100L);
    //        betDetail.setPayAmount(100L);
    //        betRequest.setBetDetail(betDetail);
    //        return betRequest;
    //    }
    //
    //    private BetRequest buildBuyBetRequest() {
    //        BetRequest betRequest = new BetRequest();
    //        betRequest.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
    //        betRequest.setBetType(BetTypeEnum.BUY_BET.getCode());
    //        betRequest.setBetSubType(BetSubTypeEnum.BUY_BET.getCode());
    //        MariaMakilingBetRequest betDetail = new MariaMakilingBetRequest();
    //        betDetail.setBetAmount(100L);
    //        betDetail.setPayAmount(10000L);
    //        betRequest.setBetDetail(betDetail);
    //        return betRequest;
    //    }
    //
    //    private static BetRequest buildQueryBuyBetRequest(String gameRecordId, String betRecordId) {
    //        BetRequest betRequest = new BetRequest();
    //        betRequest.setGameId(GameTypeEnum.MARIA_MAKILING.getId());
    //        betRequest.setBetType(BetTypeEnum.BUY_BET.getCode());
    //        betRequest.setBetSubType(BetSubTypeEnum.FREE_SPINS_BUY_BET.getCode());
    //
    //        MariaMakilingBetRequest betDetail = new MariaMakilingBetRequest();
    //        betDetail.setBetAmount(100L);
    //        betDetail.setPayAmount(0L);
    //        betDetail.setBetRecordId(betRecordId);
    //        betDetail.setGameRecordId(gameRecordId);
    //        betRequest.setBetDetail(betDetail);
    //        return betRequest;
    //    }
}
