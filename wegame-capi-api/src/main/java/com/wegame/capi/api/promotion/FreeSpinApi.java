package com.wegame.capi.api.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.SingleWalletBaseApi;
import com.wegame.capi.api.domain.req.promotion.FreeSpinHistoryOfPlayerReq;
import com.wegame.capi.api.domain.req.promotion.FreeSpinOfPlayerReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinHistoryOfPlayerRes;
import com.wegame.capi.api.domain.res.promotion.FreeSpinOfPlayerRes;
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
 * Free Spin api
 * 文档导航：8.Free Spin Api
 * @see <a href="https://casinoplus.sg.larksuite.com/docx/Mvlid9I9goOgUUx0hxjlPIiAg7g"/>
 */
@Slf4j
@Component
public class FreeSpinApi extends SingleWalletBaseApi {
    private static final String FREE_SPIN_OF_PLAYER = "%s/freeSpinOfPlayer";
    private static final String FREE_SPIN_HISTORY_OF_PLAYER = "%s/freeSpinHistoryOfPlayer";

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public FreeSpinOfPlayerRes freeSpinOfPlayer(FreeSpinOfPlayerReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<FreeSpinOfPlayerReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(FREE_SPIN_OF_PLAYER, singleDomain)));
            ResponseEntity<CommonRes<FreeSpinOfPlayerRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<FreeSpinOfPlayerRes>>() {
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

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public FreeSpinHistoryOfPlayerRes freeSpinHistoryOfPlayer(FreeSpinHistoryOfPlayerReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<FreeSpinHistoryOfPlayerReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(FREE_SPIN_HISTORY_OF_PLAYER, singleDomain)));
            ResponseEntity<CommonRes<FreeSpinHistoryOfPlayerRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<FreeSpinHistoryOfPlayerRes>>() {
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
}
