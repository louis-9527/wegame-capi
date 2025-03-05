package com.wegame.capi.api.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.PromotionBaseApi;
import com.wegame.capi.api.domain.req.promotion.GetDailyMissionReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.GetDailyMissionRes;
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

/**
 * 任务api
 */
@Slf4j
@Component
public class MissionApi extends PromotionBaseApi {
    private static final String GET_DAILY_MISSION = "%s/getDailyMission";

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public GetDailyMissionRes getDailyMission(GetDailyMissionReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Connection", "Keep-Alive");
            headers.add("uid", req.getPlayerName());
            RequestEntity<GetDailyMissionReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(GET_DAILY_MISSION, promotionDomain)));
            ResponseEntity<CommonRes<GetDailyMissionRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<GetDailyMissionRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (Exception e) {
            log.error("getDailyMission err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw e;
        }
    }

}
