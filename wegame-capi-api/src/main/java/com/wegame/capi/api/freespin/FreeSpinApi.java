package com.wegame.capi.api.freespin;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.api.base.FreeSpinBaseApi;
import com.wegame.capi.api.domain.req.promotion.*;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.CreateFreeSpinRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinDetailRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinHistoryOfPlayerRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinOfPlayerRes;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.net.URI;

/**
 * Free Spin Api
 * 文档导航：<a href="https://casinoplus.sg.larksuite.com/docx/Mvlid9I9goOgUUx0hxjlPIiAg7g"/>
 * 8.Free Spin Api
 * 8.1 freeSpin
 * 8.2 freeSpinOfPlayer
 * 8.3 freeSpinHistoryOfPlayer
 */
@Slf4j
@Component
public class FreeSpinApi extends FreeSpinBaseApi {

    private static final String FREE_SPIN = "%s/freeSpin";
    private static final String FREE_SPIN_OF_PLAYER = "%s/freeSpinOfPlayer";
    private static final String FREE_SPIN_HISTORY_OF_PLAYER = "%s/freeSpinHistoryOfPlayer";
    private static final String CREATE_FREE_SPIN = "%s/callback/createFreeSpin";
    private static final String FREE_SPIN_DETAIL = "%s/freeSpinDetail";

    @Retryable(value = RetryException.class, maxAttempts = 1, exclude = BusinessException.class)
    public CommonRes<SingleWalletBetWithPayoutRes> freeSpin(FreeSpinReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<FreeSpinReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(FREE_SPIN, freeSpinDomain)));
            ResponseEntity<CommonRes<SingleWalletBetWithPayoutRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<SingleWalletBetWithPayoutRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.FREE_SPIN_BET_USE_FAILED);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.FREE_SPIN_BET_USE_FAILED);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.FREE_SPIN_BET_USE_FAILED, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.FREE_SPIN_BET_USE_FAILED, responseEntity.getBody().getMessage());
            return responseEntity.getBody();
        } catch (BusinessException e) {
            log.error("freeSpin BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("freeSpin Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.FREE_SPIN_BET_USE_FAILED, e);
        }
    }
    @Retryable(value = RetryException.class, maxAttempts = 1, exclude = BusinessException.class)
    public FreeSpinOfPlayerRes freeSpinOfPlayer(FreeSpinOfPlayerReq req) {
        try {
            log.info("freeSpinOfPlayer req: {}", FastJsonUtils.toJSONString(req));
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<FreeSpinOfPlayerReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(FREE_SPIN_OF_PLAYER, freeSpinDomain)));
            ResponseEntity<CommonRes<FreeSpinOfPlayerRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<FreeSpinOfPlayerRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("freeSpinOfPlayer BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("freeSpinOfPlayer Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
    @Retryable(value = RetryException.class, maxAttempts = 1, exclude = BusinessException.class)
    public FreeSpinHistoryOfPlayerRes freeSpinHistoryOfPlayer(FreeSpinHistoryOfPlayerReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<FreeSpinHistoryOfPlayerReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(FREE_SPIN_HISTORY_OF_PLAYER, freeSpinDomain)));
            ResponseEntity<CommonRes<FreeSpinHistoryOfPlayerRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<FreeSpinHistoryOfPlayerRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("freeSpinHistoryOfPlayer BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("freeSpinHistoryOfPlayer Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
    public CreateFreeSpinRes createFreeSpin(CreateFreeSpinReq req) {
        try {
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<CreateFreeSpinReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(CREATE_FREE_SPIN, "http://igo-sw-callback-public.igo8.me")));
            ResponseEntity<CommonRes<CreateFreeSpinRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<CreateFreeSpinRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("createFreeSpin BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("createFreeSpin Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
    public FreeSpinDetailRes freeSpinDetail(FreeSpinDetailReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<FreeSpinDetailReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(FREE_SPIN_DETAIL, freeSpinDomain)));
            ResponseEntity<CommonRes<FreeSpinDetailRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<FreeSpinDetailRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("freeSpinDetail BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("freeSpinDetail Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
}
