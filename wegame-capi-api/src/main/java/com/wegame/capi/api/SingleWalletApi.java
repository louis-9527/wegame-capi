package com.wegame.capi.api;

import com.alibaba.fastjson.JSON;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.capi.api.domain.req.GetAuthLoginTokenReq;
import com.wegame.capi.api.domain.req.singlewallet.BetWithPayoutReq;
import com.wegame.capi.api.domain.req.singlewallet.GetCreditReq;
import com.wegame.capi.api.domain.res.AuthLoginTokenRes;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.BetWithPayoutRes;
import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Component
public class SingleWalletApi extends BaseApi {

    public static final String GET_CREDIT_URL_FORMAT = "%s/getCredit";

    public static final String BET_WITH_PAYOUT = "%s/betWithPayout";

    public static final String AUTH_LOGIN_TOKEN_URL_FORMAT = "%s/authLoginToken";

    public AuthLoginTokenRes authLoginToken(GetAuthLoginTokenReq req) {
        try {
            // 设置token签名
            String preSignature = String.format("%s%s%s%s", req.getChannelId(), req.getPlayerName(),req.getCreateTime(), req.getRefId());
            String key = StringUtils.equals(String.valueOf(req.getPId()), cpPlatformId) ? cpPrivateKey : igoPrivateKey;
            String iv = StringUtils.equals(String.valueOf(req.getPId()), cpPlatformId) ? cpPrivateIv : igoPrivateIv;
            log.info("platformId: {}; preSignature: {}; key: {}; iv: {}", req.getPId(), preSignature, key, iv);
            String encrypt = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);
            AssertUtil.assertNotNull(encrypt, ErrorCode.ERR_PARAM);
            req.setHash(encrypt);
            RequestEntity<GetAuthLoginTokenReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST,
                    URI.create(String.format(AUTH_LOGIN_TOKEN_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<AuthLoginTokenRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<AuthLoginTokenRes>>() {});
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

    public BetWithPayoutRes betWithPayout(BetWithPayoutReq req) {
        try {
            // 参数签名
            assembleSingleSignature(req);
            RequestEntity<BetWithPayoutReq> requestEntity = new RequestEntity<>(req, HttpMethod.POST,
                    URI.create(String.format(BET_WITH_PAYOUT, singleDomain)));
            log.info("betWithPayout , req: {}", req);
            ResponseEntity<CommonRes<BetWithPayoutRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<BetWithPayoutRes>>() {
                    });
            log.info("betWithPayout , resp: {}", JSON.toJSONString(responseEntity));
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody().getData();
        } catch (BusinessException be) {
            log.error("betWithPayout err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("betWithPayout err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
}
