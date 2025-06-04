package com.wegame.capi.api.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.PromotionBaseApi;
import com.wegame.capi.api.domain.req.promotion.GetAchievementReq;
import com.wegame.capi.api.domain.req.promotion.RedeemAchievementReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.GetAchievementRes;
import com.wegame.capi.api.domain.res.promotion.RedeemAchievementRes;
import com.wegame.capi.common.enums.AchievementErrorCode;
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
 * 成就api
 * @see <a href="https://casinoplus.sg.larksuite.com/docx/XZzMdmW4aonhYfxtMEili1qGgkb"/>
 */
@Slf4j
@Component
public class AchievementApi extends PromotionBaseApi {

    private static final String REDEEM_ACHIEVEMENT_URL_FORMAT = "%s/redeemAchievement";
    private static final String GET_ACHIEVEMENT_URL_FORMAT = "%s/getAchievement";

    @Retryable(value = RetryException.class, maxAttempts = 2, exclude = BusinessException.class)
    public GetAchievementRes getAchievement(GetAchievementReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<GetAchievementReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(GET_ACHIEVEMENT_URL_FORMAT, promotionDomain)));
            ResponseEntity<CommonRes<GetAchievementRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<GetAchievementRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                AchievementErrorCode achievementErrorCode = AchievementErrorCode.valueOfThirdCode(responseEntity.getBody().getStatus());
                AssertUtil.assertNotNull(achievementErrorCode, ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
                throw new BusinessException(achievementErrorCode.getErrorCode(), achievementErrorCode.getMsg());
            }
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("getAchievement BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("getAchievement Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    public RedeemAchievementRes redeemAchievement(RedeemAchievementReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<RedeemAchievementReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(REDEEM_ACHIEVEMENT_URL_FORMAT, promotionDomain)));
            ResponseEntity<CommonRes<RedeemAchievementRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<RedeemAchievementRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            if (!responseEntity.getBody().isSuc()) {
                AchievementErrorCode achievementErrorCode = AchievementErrorCode.valueOfThirdCode(responseEntity.getBody().getStatus());
                AssertUtil.assertNotNull(achievementErrorCode, ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
                throw new BusinessException(achievementErrorCode.getErrorCode(), achievementErrorCode.getMsg());
            }
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("redeemAchievement BusinessException, req: {}, errorCode: {}, errMsg: {}", req, e.getErrorCode().getCode(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("redeemAchievement Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
}
