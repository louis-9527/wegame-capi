package com.wegame.capi.api;

import com.alibaba.fastjson.JSON;
import com.wegame.base.utils.DateUtil;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.base.utils.ThreadMdcUtil;
import com.wegame.capi.api.domain.req.GetAuthLoginTokenReq;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.req.singlewallet.BoxingBetStatusReq;
import com.wegame.capi.api.domain.req.singlewallet.GetCreditReq;
import com.wegame.capi.api.domain.res.AuthLoginTokenRes;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.BetWithPayoutRes;
import com.wegame.capi.api.domain.res.singlewallet.BoxingBetStatusRes;
import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletCommonRes;
import com.wegame.capi.common.context.UserInfoContext;
import com.wegame.capi.common.enums.SingleWalletResStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
public class SingleWalletApi extends BaseApi {

    private static final String AUTH_LOGIN_TOKEN_URL_FORMAT = "%s/authLoginToken";
    private static final String GET_CREDIT_URL_FORMAT = "%s/getCredit";
    private static final String BET_WITH_PAYOUT_URL_FORMAT = "%s/betWithPayout";
    private static final String BET_STATUS_URL_FORMAT = "%s/betStatus";
    private static final int BET_TYPE = 2;

    public AuthLoginTokenRes authLoginToken(GetAuthLoginTokenReq req) {
        try {
            // 设置token签名
            String preSignature = String.format("%s%s%s%s", req.getChannelId(), req.getPlayerName(),req.getCreateTime(), req.getRefId());
            String key = StringUtils.equals(String.valueOf(req.getPId()), cpPlatformId) ? cpPrivateKey : igoPrivateKey;
            String iv = StringUtils.equals(String.valueOf(req.getPId()), cpPlatformId) ? cpPrivateIv : igoPrivateIv;
            // todo delete log
            log.info("platformId: {}; preSignature: {}; key: {}; iv: {}", req.getPId(), preSignature, key, iv);
            String encrypt = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);
            AssertUtil.assertNotNull(encrypt, ErrorCode.ERR_PARAM);
            req.setHash(encrypt);
            RequestEntity<GetAuthLoginTokenReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST, URI.create(String.format(AUTH_LOGIN_TOKEN_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<AuthLoginTokenRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<AuthLoginTokenRes>>() {});
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM);
            return responseEntity.getBody().getData();
        } catch (Exception e) {
            log.error("authTokenLogin err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        }
    }

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public GetCreditRes getCredit(GetCreditReq req) {
        try {
            // 参数签名
            assembleSingleSignature(req);
            RequestEntity<GetCreditReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST,
                    URI.create(String.format(GET_CREDIT_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<GetCreditRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<GetCreditRes>>() {
                    });
            // todo delete log
            log.info("getCredit err, req: {}, res: {}", req, responseEntity);
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody().getData();
        } catch (BusinessException be) {
            log.error("getCredit err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("getCredit err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public BetWithPayoutRes betWithPayout(BetWithPayoutReq req) {
        try {
            // 参数签名
            assembleSingleSignature(req);
            RequestEntity<BetWithPayoutReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST,
                    URI.create(String.format(BET_WITH_PAYOUT_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<BetWithPayoutRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<BetWithPayoutRes>>() {
                    });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            CommonRes<BetWithPayoutRes> commonRes = responseEntity.getBody();
            // 成功或者重复的refId都当作成功处理
            if (commonRes.isSuc()) {
                AssertUtil.assertNotNull(commonRes.getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
                return responseEntity.getBody().getData();
            } else if (Objects.equals(commonRes.getStatus(), SingleWalletResStatusEnum.DUPLICATE_REFERENCE_ID.getCode())) {
                return this.queryBetDetail(req);
            } else {
                log.error("betWithPayout result err, req: {}, res: {}", JSON.toJSONString(req), JSON.toJSONString(commonRes));
                throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "bet error,please retry again later");
            }
        } catch (BusinessException be) {
            log.error("betWithPayout err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("betWithPayout err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    /**
     * 单钱包状态查询
     *
     * @param req req
     * @return response
     */
    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public BoxingBetStatusRes betStatus(BoxingBetStatusReq req) {
        try {
            // 参数签名
            assembleSingleSignature(req);
            RequestEntity<BoxingBetStatusReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST,
                    URI.create(String.format(BET_STATUS_URL_FORMAT, singleDomain)));
            ResponseEntity<SingleWalletCommonRes<BoxingBetStatusRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<SingleWalletCommonRes<BoxingBetStatusRes>>() {
                    });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody().getData();
        } catch (BusinessException be) {
            log.error("betStatus business err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("betStatus err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    private BetWithPayoutRes queryBetDetail(BetWithPayoutReq req) {
        // 此时状态为成功,请求betStatus获取订单betId
        BoxingBetStatusReq betStatusReq = BoxingBetStatusReq.builder()
                .betType(BET_TYPE)
                .wagersDate(req.getBetDetails().getWagersDate())
                .refId(req.getRefId())
                .build();
        BoxingBetStatusRes betStatusRes = this.betStatus(betStatusReq);
        // 获取余额
        GetCreditReq creditReq = GetCreditReq.builder()
                .channelId(UserInfoContext.getUserInfo().getChannelId())
                .playerName(UserInfoContext.getUserInfo().getPlayerName())
                .refId(MDC.get(ThreadMdcUtil.TRACE_ID))
                .createTime(DateUtil.toUTC(new Date()))
                .pId(UserInfoContext.getUserInfo().getPlatformId())
                .build();
        GetCreditRes creditRes = this.getCredit(creditReq);
        return BetWithPayoutRes.builder()
                .betId(betStatusRes.getBetId())
                .playerName(creditReq.getPlayerName())
                .credit(creditRes.getCredit())
                .build();
    }

}
