package com.wegame.capi.api.promotion;

import com.wegame.base.enums.ErrorCode;
import com.wegame.base.exception.BusinessException;
import com.wegame.base.exception.RetryException;
import com.wegame.base.utils.AssertUtil;
import com.wegame.capi.api.base.PromotionBaseApi;
import com.wegame.capi.api.domain.req.promotion.GetBagCurrentRewardReq;
import com.wegame.capi.api.domain.req.promotion.GetBagOtherRewardReq;
import com.wegame.capi.api.domain.res.CommonRes;
import com.wegame.capi.api.domain.res.promotion.GetBagCurrentRewardRes;
import com.wegame.capi.api.domain.res.promotion.GetBagOtherRewardRes;
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
 * 背包api
 */
@Slf4j
@Component
public class BagApi extends PromotionBaseApi {

    private static final String GET_REWARD_BAG = "%s/getRewardBag";
    private static final String GET_OTHER_GAME = "%s/getOtherGame";


    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public GetBagCurrentRewardRes getBagCurrentReward(GetBagCurrentRewardReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Connection", "Keep-Alive");
            headers.add("uid", req.getPlayerName());
            RequestEntity<GetBagCurrentRewardReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(GET_REWARD_BAG, promotionDomain)));
            ResponseEntity<CommonRes<GetBagCurrentRewardRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<GetBagCurrentRewardRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException be) {
            log.error("getRewardBag err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("getRewardBag err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }

    @Retryable(value = RetryException.class, exclude = BusinessException.class)
    public GetBagOtherRewardRes getBagOtherReward(GetBagOtherRewardReq req) {
        try {
            assembleSingleSignature(req);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Connection", "Keep-Alive");
            headers.add("uid", req.getPlayerName());
            RequestEntity<GetBagOtherRewardReq> requestEntity = new RequestEntity<>(req, headers, HttpMethod.POST, URI.create(String.format(GET_OTHER_GAME, promotionDomain)));
            ResponseEntity<CommonRes<GetBagOtherRewardRes>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<CommonRes<GetBagOtherRewardRes>>() {
            });
            AssertUtil.assertNotNull(responseEntity, ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertNotNull(responseEntity.getBody(), ErrorCode.ERR_MIDDLE_PLATFORM);
            AssertUtil.assertTrue(responseEntity.getBody().isSuc(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            AssertUtil.assertNotNull(responseEntity.getBody().getData(), ErrorCode.ERR_MIDDLE_PLATFORM, responseEntity.getBody().getMessage());
            return responseEntity.getBody().getData();
        } catch (BusinessException be) {
            log.error("getRewardBag err, req: {}, errMsg: {}", req, be.getMessage(), be);
            throw be;
        } catch (Exception e) {
            log.error("getRewardBag err, req: {}, errMsg: {}", req, e.getMessage(), e);
            throw new RetryException(ErrorCode.ERR_MIDDLE_PLATFORM, "inner error,please retry again later", e);
        }
    }
}
