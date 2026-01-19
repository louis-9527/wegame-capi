package com.wegame.capi.api.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.PromotionBaseApi;
import com.wegame.capi.api.domain.req.redirect.GetRedirectGameUrlReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.GetRedirectGameUrlRes;
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
 * 游戏转发 Redirect-api
 *
 * @see
 * <a href="https://casinoplus.sg.larksuite.com/docx/AllddxYUvoE3N1xl3jUl0MC7gqg"/>
 * 2.22 HTTP getRedirectGameUrl  (BP)
 */
@Slf4j
@Component
public class RedirectApi extends PromotionBaseApi {

    private static final String GET_REDIRECT_GAME_URL_FORMAT = "%s/getRedirectGameUrl";

    @Retryable(value = RetryException.class, maxAttempts = 2, exclude = BusinessException.class)
    public GetRedirectGameUrlRes getRedirectGameUrl(GetRedirectGameUrlReq req) {
        try {
            assembleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<GetRedirectGameUrlReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(GET_REDIRECT_GAME_URL_FORMAT, promotionDomain(req.getPId()))));
            ResponseEntity<CommonRes<GetRedirectGameUrlRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<GetRedirectGameUrlRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                throw new BusinessException(PROMOTION_PREFIX_ERR_CODE + responseEntity.getBody().getStatus(), responseEntity.getBody().getMessage());
            }
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("getRedirectGameUrl BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("getRedirectGameUrl Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

}
