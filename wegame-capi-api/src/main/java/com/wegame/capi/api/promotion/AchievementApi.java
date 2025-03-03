package com.wegame.capi.api.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.PromotionBaseApi;
import com.wegame.capi.api.domain.req.promotion.RedeemAchievementReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.RedeemAchievementRes;
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

@Slf4j
@Component
public class AchievementApi extends PromotionBaseApi {

    private static final String REDEEM_ACHIEVEMENT_URL_FORMAT = "%s/redeemAchievement";

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public RedeemAchievementRes redeemAchievement(RedeemAchievementReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Connection", "Keep-Alive");
            headers.add("uid", req.getPlayerName());
            RequestEntity<RedeemAchievementReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(REDEEM_ACHIEVEMENT_URL_FORMAT, promotionDomain)));
            ResponseEntity<CommonRes<RedeemAchievementRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<RedeemAchievementRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (Exception e) {
            log.error("redeemAchievement err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        }
    }
}
