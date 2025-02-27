package com.wegame.capi.api;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.SingleWalletSignatureUtil;
import com.wegame.capi.api.domain.req.singlewallet.*;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.AuthLoginTokenRes;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

@Slf4j
//@Component
public class SingleWalletApi extends BaseApi {

    private static final String AUTH_LOGIN_TOKEN_URL_FORMAT = "%s/authLoginToken";
    private static final String GET_CREDIT_URL_FORMAT = "%s/getCredit";
    private static final String BET_WITH_PAYOUT_URL_FORMAT = "%s/betWithPayout";
    private static final String BET_STATUS_URL_FORMAT = "%s/betStatus";
    @Value("${api.sport.singleDomain}")
    String singleDomain;
    // cp
    @Value("${api.sport.cp.platformId}")
    String cpPlatformId;
    @Value("${api.sport.cp.channelId}")
    String cpChannelId;
    @Value("${api.sport.cp.privateKey}")
    String cpPrivateKey;
    @Value("${api.sport.cp.privateIv}")
    String cpPrivateIv;
    // igo
    @Value("${api.sport.igo.platformId}")
    String igoPlatformId;
    @Value("${api.sport.igo.channelId}")
    String igoChannelId;
    @Value("${api.sport.igo.privateKey}")
    String igoPrivateKey;
    @Value("${api.sport.igo.privateIv}")
    String igoPrivateIv;

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public AuthLoginTokenRes authLoginToken(GetAuthLoginTokenReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Connection", "Keep-Alive");
            RequestEntity<GetAuthLoginTokenReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(AUTH_LOGIN_TOKEN_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<AuthLoginTokenRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<AuthLoginTokenRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
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
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Connection", "Keep-Alive");
            RequestEntity<GetCreditReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST,
                    URI.create(String.format(GET_CREDIT_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<GetCreditRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<GetCreditRes>>() {
                    });
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
    public CommonRes<SingleWalletBetWithPayoutRes> betWithPayout(BetWithPayoutReq req) {
        try {
            // 参数签名
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Connection", "Keep-Alive");
            RequestEntity<BetWithPayoutReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST,
                    URI.create(String.format(BET_WITH_PAYOUT_URL_FORMAT, singleDomain)));
           // log.info("before request betWithPayout, timeStamp: {}", System.currentTimeMillis());
            ResponseEntity<CommonRes<SingleWalletBetWithPayoutRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<SingleWalletBetWithPayoutRes>>() {
                    });
           // log.info("after request betWithPayout, timeStamp: {}", System.currentTimeMillis());
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody();
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
    public BetStatusRes getBetStatus(BetStatusReq req) {
        try {
            // 参数签名
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Connection", "Keep-Alive");
            RequestEntity<BetStatusReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST,
                    URI.create(String.format(BET_STATUS_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<BetStatusRes>> responseEntity = restTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<BetStatusRes>>() {
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

    public Pair<String, String> getPrivateKeyAndIvByChannelId(String channelId) {
        if (StringUtils.equals(channelId, cpChannelId)) {
            return Pair.of(cpPrivateKey, cpPrivateIv);
        }
        if (StringUtils.equals(channelId, igoChannelId)) {
            return Pair.of(igoPrivateKey, igoPrivateIv);
        }
        throw new BusinessException(ErrorCode.ERR_PARAM);
    }

    private void assembleSingleSignature(SingleWalletBaseReq req) {
        String channelId = getChannelId(req.getPId());
        Pair<String, String> privateKeyAndIv = getPrivateKeyAndIv(req.getPId());
        String hash = hash(channelId, req.getPlayerName(), req.getCreateTime(), req.getRefId(), privateKeyAndIv.getLeft(), privateKeyAndIv.getRight());
        req.setChannelId(channelId);
        req.setHash(hash);
    }

    public String hash(String channelId, String playerName, String createTime, String refId, String key, String iv) {
        String preSignature = String.format("%s%s%s%s", channelId, playerName, createTime, refId);
        String hash = SingleWalletSignatureUtil.encrypt(preSignature, key, iv);
        AssertUtil.assertNotNull(hash, ErrorCode.ERR_PARAM);
        return hash;
    }

    private String getChannelId(String platformId) {
        if (StringUtils.equals(platformId, cpPlatformId)) {
            return cpChannelId;
        }
        if (StringUtils.equals(platformId, igoPlatformId)) {
            return igoChannelId;
        }
        throw new BusinessException(ErrorCode.ERR_PARAM);
    }

    private Pair<String, String> getPrivateKeyAndIv(String platformId) {
        if (StringUtils.equals(platformId, cpPlatformId)) {
            return Pair.of(cpPrivateKey, cpPrivateIv);
        }
        if (StringUtils.equals(platformId, igoPlatformId)) {
            return Pair.of(igoPrivateKey, igoPrivateIv);
        }
        throw new BusinessException(ErrorCode.ERR_PARAM);
    }
}
