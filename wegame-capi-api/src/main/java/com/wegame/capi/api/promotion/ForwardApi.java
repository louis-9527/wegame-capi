package com.wegame.capi.api.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.PromotionBaseApi;
import com.wegame.capi.api.domain.req.promotion.PromotionForwardReq;
import com.wegame.capi.api.domain.res.CommonRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.Map;

/**
 * 游戏转发 Redirect-api
 * @see
 * <a href="https://casinoplus.sg.larksuite.com/docx/AllddxYUvoE3N1xl3jUl0MC7gqg"/>
 */
@Slf4j
@Component
public class ForwardApi extends PromotionBaseApi {

    private static final String PROMOTION_PATH_FORMAT = "%s/%s";

    @Retryable(value = RetryException.class, maxAttempts = 2, exclude = BusinessException.class)
    public Map<String, Object> forward(String urlPath, PromotionForwardReq req) {
        try {
            assembleSignature(req, req.getPreSignature());
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            URI uri = URI.create(String.format(PROMOTION_PATH_FORMAT, promotionDomain(req.getPId()), urlPath));
            RequestEntity<Map<String, Object>> requestEntity = new RequestEntity<>(req.getParamesMap(), headers, HttpMethod.POST, uri);
            ResponseEntity<CommonRes<Map<String, Object>>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<Map<String, Object>>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                throw new BusinessException(PROMOTION_PREFIX_ERR_CODE + responseEntity.getBody().getStatus(), responseEntity.getBody().getMessage());
            }
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("promotion forward BusinessException, path: {}, req: {}, errMsg: {}", urlPath, req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("promotion forward Exception, path: {}, req: {}, errMsg: {}", urlPath, req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

}
