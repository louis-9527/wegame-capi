//package com.wegame.capi.api;
//
//import com.wegame.base.enums.ErrorCode;
//import com.wegame.base.exception.BusinessException;
//import com.wegame.base.exception.RetryException;
//import com.wegame.capi.api.domain.req.singlewallet.BetStatusReq;
//import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
//import com.wegame.capi.api.domain.req.singlewallet.GetAuthLoginTokenReq;
//import com.wegame.capi.api.domain.req.singlewallet.GetCreditReq;
//import com.wegame.capi.api.domain.res.CommonRes;
//import com.wegame.capi.api.domain.res.singlewallet.AuthLoginTokenRes;
//import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
//import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
//import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.retry.annotation.Retryable;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//
//import static com.wegame.capi.common.enums.SingleWalletBetStatusEnum.COMPLETED_PAYOUT_REQUEST;
//
//@Slf4j
//@Component
//public class MockSingleWalletApi extends SingleWalletApi {
//
//    @Override
//    public AuthLoginTokenRes authLoginToken(GetAuthLoginTokenReq req) {
//        try {
//            return AuthLoginTokenRes.builder()
//                    .refId(req.getRefId())
//                    .errorMessages(null)
//                    .build();
//        } catch (Exception e) {
//            log.error("authTokenLogin err, req: {}, errMsg: {}", req, e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    @Override
//    @Retryable(value = RetryException.class, exclude = BusinessException.class)
//    public GetCreditRes getCredit(GetCreditReq req) {
//        try {
//            return GetCreditRes.builder().credit(new BigDecimal("1000000")).build();
//        } catch (BusinessException be) {
//            log.error("getCredit err, req: {}, errMsg: {}", req, be.getMessage(), be);
//            throw be;
//        } catch (Exception e) {
//            log.error("getCredit err, req: {}, errMsg: {}", req, e.getMessage(), e);
//            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
//        }
//    }
//
//    @Override
//    @Retryable(value = RetryException.class, exclude = BusinessException.class)
//    public CommonRes<SingleWalletBetWithPayoutRes> betWithPayout(BetWithPayoutReq req) {
//        try {
//            SingleWalletBetWithPayoutRes data = SingleWalletBetWithPayoutRes.builder()
//                    .betId(req.getRefId())
//                    .refId(req.getRefId())
//                    .credit(new BigDecimal(1000000))
//                    .build();
//            return CommonRes.<SingleWalletBetWithPayoutRes>builder().status(CommonRes.SUC_CODE).message("").data(data).build();
//        } catch (BusinessException be) {
//            log.error("betWithPayout err, req: {}, errMsg: {}", req, be.getMessage(), be);
//            throw be;
//        } catch (Exception e) {
//            log.error("betWithPayout err, req: {}, errMsg: {}", req, e.getMessage(), e);
//            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
//        }
//    }
//
//    /**
//     * 单钱包状态查询
//     *
//     * @param req req
//     * @return response
//     */
//    @Override
//    public BetStatusRes getBetStatus(BetStatusReq req) {
//        try {
//            // 参数签名
//            return BetStatusRes.builder().betId(req.getRefId()).code(COMPLETED_PAYOUT_REQUEST.getCode()).build();
//        } catch (BusinessException be) {
//            log.error("betStatus business err, req: {}, errMsg: {}", req, be.getMessage(), be);
//            throw be;
//        } catch (Exception e) {
//            log.error("betStatus err, req: {}, errMsg: {}", req, e.getMessage(), e);
//            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
//        }
//    }
//}
