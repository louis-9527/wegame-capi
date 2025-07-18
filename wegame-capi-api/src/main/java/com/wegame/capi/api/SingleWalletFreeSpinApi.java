package com.wegame.capi.api;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.base.utils.FastJsonUtils;
import com.wegame.capi.api.base.FreeSpinBaseApi;
import com.wegame.capi.api.domain.req.freespin.FreeSpinDetailReq;
import com.wegame.capi.api.domain.req.freespin.FreeSpinHistoryOfPlayerReq;
import com.wegame.capi.api.domain.req.freespin.FreeSpinOfPlayerReq;
import com.wegame.capi.api.domain.req.freespin.FreeSpinReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinDetailRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinHistoryOfPlayerRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinOfPlayerRes;
import com.wegame.capi.api.domain.res.singlewallet.SingleWalletBetWithPayoutRes;
import com.wegame.capi.common.enums.FreeSpinErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.locks.LockSupport;

import static com.wegame.capi.common.enums.SingleWalletResStatusEnum.DUPLICATE_REFERENCE_ID;

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
public class SingleWalletFreeSpinApi extends FreeSpinBaseApi {
    private static final long FIVE_SECONDS = 5000L;
    private static final int MAX_DUPLICATE_COUNT = 2;

    private static final String FREE_SPIN = "%s/freeSpin";
    private static final String FREE_SPIN_OF_PLAYER = "%s/freeSpinOfPlayer";
    private static final String FREE_SPIN_HISTORY_OF_PLAYER = "%s/freeSpinHistoryOfPlayer";
    private static final String CREATE_FREE_SPIN = "%s/callback/createFreeSpin";
    private static final String FREE_SPIN_DETAIL = "%s/freeSpinDetail";

    public CommonRes<SingleWalletBetWithPayoutRes> freeSpin(FreeSpinReq req) {
        // 参数签名
        assembleSingleSignature(req);
        MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
        RequestEntity<FreeSpinReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(FREE_SPIN, freeSpinDomain)));
        long requestStartTime = System.currentTimeMillis();
        boolean canContinueExecute = true;
        int duplicateIdCount = 0;
        while (canContinueExecute) {
            try {
                ResponseEntity<CommonRes<SingleWalletBetWithPayoutRes>> responseEntity = freeSpinRestTemplate.exchange(requestEntity,
                        new ParameterizedTypeReference<CommonRes<SingleWalletBetWithPayoutRes>>()
                        {});
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
                        log.warn("betFreeSpin continue retry when DUPLICATE_REFERENCE_ID, sleep 1.5 seconds");
                        // 暂停1.5秒（1500000000纳秒）
                        LockSupport.parkNanos(1500000000L);
                    } else {
                        // 不能继续重试，此时则认为成功
                        log.warn("betFreeSpin stop retry when DUPLICATE_REFERENCE_ID，reach max retry：{} count, See it as a success", duplicateIdCount);
                        return commonRes;
                    }
                } else {
                    // 其他状态码，则直接返回
                    return commonRes;
                }
            } catch (Exception e) {
                log.error("betFreeSpin Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
                // 发生异常，判断是否达到最大重试时限（重试的最大时限：请求时间 + 5s）
                canContinueExecute = System.currentTimeMillis() < requestStartTime + FIVE_SECONDS;
                if (!canContinueExecute) {
                    log.error("betFreeSpin BusinessException, reach max retry time limit");
                    throw e;
                } else {
                    log.warn("betFreeSpin immediately retry when error");
                }
            }
        }
        throw new BusinessException(ErrorCode.FREE_SPIN_BET_USE_FAILED);
    }

    @Retryable(value = RetryException.class, maxAttempts = 2, backoff = @Backoff(delay = 0), exclude = BusinessException.class)
    public FreeSpinOfPlayerRes freeSpinOfPlayer(FreeSpinOfPlayerReq req) {
        try {
            log.info("freeSpinOfPlayer req: {}", FastJsonUtils.toJSONString(req));
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<FreeSpinOfPlayerReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(FREE_SPIN_OF_PLAYER, freeSpinDomain)));
            ResponseEntity<CommonRes<FreeSpinOfPlayerRes>> responseEntity = freeSpinRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<FreeSpinOfPlayerRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                FreeSpinErrorCode freeSpinErrorCode = FreeSpinErrorCode.valueOfThirdCode(responseEntity.getBody().getStatus());
                AssertUtil.assertNotNull(freeSpinErrorCode, ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
                throw new BusinessException(freeSpinErrorCode.getErrorCode(), freeSpinErrorCode.getMsg());
            }
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

    @Retryable(value = RetryException.class, maxAttempts = 2, backoff = @Backoff(delay = 0), exclude = BusinessException.class)
    public FreeSpinHistoryOfPlayerRes freeSpinHistoryOfPlayer(FreeSpinHistoryOfPlayerReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<FreeSpinHistoryOfPlayerReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(FREE_SPIN_HISTORY_OF_PLAYER, freeSpinDomain)));
            ResponseEntity<CommonRes<FreeSpinHistoryOfPlayerRes>> responseEntity = freeSpinRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<FreeSpinHistoryOfPlayerRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                FreeSpinErrorCode freeSpinErrorCode = FreeSpinErrorCode.valueOfThirdCode(responseEntity.getBody().getStatus());
                AssertUtil.assertNotNull(freeSpinErrorCode, ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
                throw new BusinessException(freeSpinErrorCode.getErrorCode(), freeSpinErrorCode.getMsg());
            }
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

    @Retryable(value = RetryException.class, maxAttempts = 2, backoff = @Backoff(delay = 0), exclude = BusinessException.class)
    public FreeSpinDetailRes freeSpinDetail(FreeSpinDetailReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<FreeSpinDetailReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(FREE_SPIN_DETAIL, freeSpinDomain)));
            ResponseEntity<CommonRes<FreeSpinDetailRes>> responseEntity = freeSpinRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<FreeSpinDetailRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                FreeSpinErrorCode freeSpinErrorCode = FreeSpinErrorCode.valueOfThirdCode(responseEntity.getBody().getStatus());
                AssertUtil.assertNotNull(freeSpinErrorCode, ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
                throw new BusinessException(freeSpinErrorCode.getErrorCode(), freeSpinErrorCode.getMsg());
            }
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
