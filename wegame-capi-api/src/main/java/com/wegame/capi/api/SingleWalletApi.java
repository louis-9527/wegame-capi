package com.wegame.capi.api;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.SingleWalletBaseApi;
import com.wegame.capi.api.domain.req.singlewallet.*;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.singlewallet.AuthLoginTokenRes;
import com.wegame.capi.api.domain.res.singlewallet.BetStatusRes;
import com.wegame.capi.api.domain.res.singlewallet.GetCreditRes;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.locks.LockSupport;

import static com.wegame.capi.common.enums.SingleWalletResStatusEnum.DUPLICATE_REFERENCE_ID;

/**
 * Single Wallet Api
 * 文档导航：<a href="https://casinoplus.sg.larksuite.com/docx/IwUhdnb3aodz42xnUAWlLp12gpf"/>
 * 2.1      authLoginToken
 * 2.2      getCredit
 * 3.2.3    betWithPayout
 * 5.6      starSpin
 * 8.1      betStatus
 */
@Slf4j
@Component
public class SingleWalletApi extends SingleWalletBaseApi {

    private static final long FIVE_SECONDS = 5000L;
    private static final int MAX_DUPLICATE_COUNT = 2;

    private static final String AUTH_LOGIN_TOKEN_URL_FORMAT = "%s/authLoginToken";
    private static final String GET_CREDIT_URL_FORMAT = "%s/getCredit";
    private static final String BET_WITH_PAYOUT_URL_FORMAT = "%s/betWithPayout";
    private static final String BET_STATUS_URL_FORMAT = "%s/betStatus";
    private static final String BET_STAR_SPIN_URL_FORMAT = "%s/starSpin";

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public AuthLoginTokenRes authLoginToken(GetAuthLoginTokenReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders();
            RequestEntity<GetAuthLoginTokenReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(AUTH_LOGIN_TOKEN_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<AuthLoginTokenRes>> responseEntity = walletRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<AuthLoginTokenRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("authTokenLogin BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("authTokenLogin Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public GetCreditRes getCredit(GetCreditReq req) {
        try {
            // 参数签名
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders();
            RequestEntity<GetCreditReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST,
                    URI.create(String.format(GET_CREDIT_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<GetCreditRes>> responseEntity = walletRestTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<GetCreditRes>>() {
                    });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody().getData();
        } catch (BusinessException be) {
            log.error("getCredit BusinessException, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("getCredit Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    public CommonRes<SingleWalletBetWithPayoutRes> betWithPayout(BetWithPayoutReq req) {
        // 参数签名
        assembleSingleSignature(req);
        RequestEntity<BetWithPayoutReq> requestEntity = new RequestEntity<>(req,
                buildHeaders(),
                HttpMethod.POST,
                URI.create(String.format(BET_WITH_PAYOUT_URL_FORMAT, singleDomain)));
        long requestStartTime = System.currentTimeMillis();
        boolean canContinueExecute = true;
        int duplicateIdCount = 0;
        while (canContinueExecute) {
            try {
                ResponseEntity<CommonRes<SingleWalletBetWithPayoutRes>> responseEntity = walletRestTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<CommonRes<SingleWalletBetWithPayoutRes>>() {
                        });
                if (Objects.isNull(responseEntity) || Objects.isNull(responseEntity.getBody())) {
                    canContinueExecute = System.currentTimeMillis() < requestStartTime + FIVE_SECONDS;
                    continue;
                }
                CommonRes<SingleWalletBetWithPayoutRes> commonRes = responseEntity.getBody();
                if (commonRes.isSuc()) {
                    // 成功，则直接返回
                    return commonRes;
                } else if (Objects.equals(commonRes.getStatus(), DUPLICATE_REFERENCE_ID.getCode())) {
                    duplicateIdCount = duplicateIdCount + 1;
                    // 重复id，则判断能否继续重试(重试次数小于2 || 未达到重试时间上限)。
                    // （单钱包认为3s后的重复id为成功）通过duplicateCount次数 + 每次重试sleep1.5s控制
                    canContinueExecute = duplicateIdCount < MAX_DUPLICATE_COUNT;
                    if (canContinueExecute) {
                        // 能继续重试
                        log.warn("betWithPayout continue retry when DUPLICATE_REFERENCE_ID, sleep 1.5 seconds");
                        // 暂停1.5秒（1500000000纳秒）
                        LockSupport.parkNanos(1500000000L);
                    } else {
                        // 不能继续重试，此时则认为成功
                        log.warn("betWithPayout stop retry when DUPLICATE_REFERENCE_ID，reach max retry：{} count, See it as a success", duplicateIdCount);
                        return commonRes;
                    }
                } else {
                    // 其他状态码，则直接返回
                    return commonRes;
                }
            } catch (Exception e) {
                log.error("betWithPayout Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
                // 发生异常，判断是否达到最大重试时限（重试的最大时限：请求时间 + 5s）
                canContinueExecute = System.currentTimeMillis() < requestStartTime + FIVE_SECONDS;
                if (!canContinueExecute) {
                    log.error("betWithPayout BusinessException, reach max retry time limit");
                    throw e;
                } else {
                    log.warn("betWithPayout immediately retry when error");
                }
            }
        }
        throw new BusinessException(ErrorCode.ERR_NET);
    }

    /**
     * 星卡投注
     * @param req
     * @return
     */
    public CommonRes<SingleWalletBetWithPayoutRes> starSpin(BetStarSpinReq req) {
        // 参数签名
        assembleSingleSignature(req);
        RequestEntity<BetStarSpinReq> requestEntity = new RequestEntity<>(req,
                buildHeaders(),
                HttpMethod.POST,
                URI.create(String.format(BET_STAR_SPIN_URL_FORMAT, singleDomain)));
        long requestStartTime = System.currentTimeMillis();
        boolean canContinueExecute = true;
        int duplicateIdCount = 0;
        while (canContinueExecute) {
            try {
                ResponseEntity<CommonRes<SingleWalletBetWithPayoutRes>> responseEntity = walletRestTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<CommonRes<SingleWalletBetWithPayoutRes>>() {
                        });
                if (Objects.isNull(responseEntity) || Objects.isNull(responseEntity.getBody())) {
                    canContinueExecute = System.currentTimeMillis() < requestStartTime + FIVE_SECONDS;
                    continue;
                }
                CommonRes<SingleWalletBetWithPayoutRes> commonRes = responseEntity.getBody();
                if (commonRes.isSuc()) {
                    // 成功，则直接返回
                    return commonRes;
                } else if (Objects.equals(commonRes.getStatus(), DUPLICATE_REFERENCE_ID.getCode())) {
                    duplicateIdCount = duplicateIdCount + 1;
                    // 重复id，则判断能否继续重试(重试次数小于2 || 未达到重试时间上限)。
                    // （单钱包认为3s后的重复id为成功）通过duplicateCount次数 + 每次重试sleep1.5s控制
                    canContinueExecute = duplicateIdCount < MAX_DUPLICATE_COUNT;
                    if (canContinueExecute) {
                        // 能继续重试
                        log.warn("betStarSpin continue retry when DUPLICATE_REFERENCE_ID, sleep 1.5 seconds");
                        // 暂停1.5秒（1500000000纳秒）
                        LockSupport.parkNanos(1500000000L);
                    } else {
                        // 不能继续重试，此时则认为成功
                        log.warn("betStarSpin stop retry when DUPLICATE_REFERENCE_ID，reach max retry：{} count, See it as a success", duplicateIdCount);
                        return commonRes;
                    }
                } else {
                    // 其他状态码，则直接返回
                    return commonRes;
                }
            } catch (Exception e) {
                log.error("betStarSpin Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
                // 发生异常，判断是否达到最大重试时限（重试的最大时限：请求时间 + 5s）
                canContinueExecute = System.currentTimeMillis() < requestStartTime + FIVE_SECONDS;
                if (!canContinueExecute) {
                    log.error("betStarSpin BusinessException, reach max retry time limit");
                    throw e;
                } else {
                    log.warn("betStarSpin immediately retry when error");
                }
            }
        }
        throw new BusinessException(ErrorCode.ERR_NET);
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
            MultiValueMap<String, String> headers = buildHeaders();
            RequestEntity<BetStatusReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST,
                    URI.create(String.format(BET_STATUS_URL_FORMAT, singleDomain)));
            ResponseEntity<CommonRes<BetStatusRes>> responseEntity = walletRestTemplate.exchange(requestEntity,
                    new ParameterizedTypeReference<CommonRes<BetStatusRes>>() {
                    });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_SINGLE_WALLET_PLATFORM);
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("betStatus BusinessException err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("betStatus Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    private MultiValueMap<String, String> buildHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Connection", "Keep-Alive");
        headers.add("Keep-Alive", "timeout=180");
        return headers;
    }
}
