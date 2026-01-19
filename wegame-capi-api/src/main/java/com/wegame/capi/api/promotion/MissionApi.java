package com.wegame.capi.api.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.PromotionBaseApi;
import com.wegame.capi.api.domain.req.promotion.GetDailyMissionReq;
import com.wegame.capi.api.domain.req.promotion.RedeemDailyMissionReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.GetDailyMissionRes;
import com.wegame.capi.api.domain.res.promotion.RedeemDailyMissionRes;
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
 * 任务api
 *
 * @see <a href="https://casinoplus.sg.larksuite.com/docx/XZzMdmW4aonhYfxtMEili1qGgkb"/>
 */
@Slf4j
@Component
public class MissionApi extends PromotionBaseApi {

    private static final String GET_DAILY_MISSION = "%s/getDailyMission";
    private static final String REDEEM_DAILY_MISSION = "%s/redeemDailyMission";

    @Retryable(value = RetryException.class, maxAttempts = 2, exclude = BusinessException.class)
    public GetDailyMissionRes getDailyMission(GetDailyMissionReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<GetDailyMissionReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(GET_DAILY_MISSION, promotionDomain(req.getPId()))));
            ResponseEntity<CommonRes<GetDailyMissionRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<GetDailyMissionRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            //            if (!responseEntity.getBody().isSuc()) {
            //                AchievementErrorCode achievementErrorCode = AchievementErrorCode.valueOfThirdCode(responseEntity.getBody().getStatus());
            //                AssertUtil.assertNotNull(achievementErrorCode, ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            //                throw new BusinessException(achievementErrorCode.getErrorCode(), achievementErrorCode.getMsg());
            //            }
            if (!responseEntity.getBody().isSuc()) {
                throw new BusinessException(PROMOTION_PREFIX_ERR_CODE + responseEntity.getBody().getStatus(), responseEntity.getBody().getMessage());
            }
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("getDailyMission BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("getDailyMission Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    public RedeemDailyMissionRes redeemDailyMission(RedeemDailyMissionReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = buildHeaders(req.getPlayerName());
            RequestEntity<RedeemDailyMissionReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(REDEEM_DAILY_MISSION, promotionDomain(req.getPId()))));
            ResponseEntity<CommonRes<RedeemDailyMissionRes>> responseEntity = promotionRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<RedeemDailyMissionRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            //            if (!responseEntity.getBody().isSuc()) {
            //                AchievementErrorCode achievementErrorCode = AchievementErrorCode.valueOfThirdCode(responseEntity.getBody().getStatus());
            //                AssertUtil.assertNotNull(achievementErrorCode, ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            //                throw new BusinessException(achievementErrorCode.getErrorCode(), achievementErrorCode.getMsg());
            //            }
            if (!responseEntity.getBody().isSuc()) {
                throw new BusinessException(PROMOTION_PREFIX_ERR_CODE + responseEntity.getBody().getStatus(), responseEntity.getBody().getMessage());
            }
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException e) {
            log.error("redeemDailyMission BusinessException, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("redeemDailyMission Exception, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
}
